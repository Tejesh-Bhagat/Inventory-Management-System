package com.inventory.service;

import com.inventory.model.Product;
import com.inventory.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Product Service Class
 * 
 * This service class contains business logic for product operations.
 * It acts as an intermediary between the controller and repository layers.
 * 
 * @Service - Marks this class as a service component
 * @Transactional - Ensures database transactions are handled properly
 */
@Service
@Transactional
public class ProductService {

    /**
     * Dependency injection of ProductRepository
     * @Autowired - Tells Spring to inject the dependency automatically
     */
    @Autowired
    private ProductRepository productRepository;

    /**
     * Get all products with pagination
     * 
     * @param pageable Pagination information
     * @return Page of products
     */
    @Transactional(readOnly = true)
    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    /**
     * Get all products as a list
     * 
     * @return List of all products
     */
    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    /**
     * Get product by ID
     * 
     * @param id Product ID
     * @return Optional containing the product if found
     */
    @Transactional(readOnly = true)
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    /**
     * Get product by SKU
     * 
     * @param sku Product SKU
     * @return Optional containing the product if found
     */
    @Transactional(readOnly = true)
    public Optional<Product> getProductBySku(String sku) {
        return productRepository.findBySku(sku);
    }

    /**
     * Save a new product or update existing product
     * Includes business logic validation
     * 
     * @param product Product to save
     * @return Saved product
     * @throws IllegalArgumentException if validation fails
     */
    public Product saveProduct(Product product) {
        // Business logic validation
        validateProduct(product);
        
        // Check for duplicate SKU (only for new products)
        if (product.getId() == null && productRepository.existsBySku(product.getSku())) {
            throw new IllegalArgumentException("Product with SKU '" + product.getSku() + "' already exists");
        }
        
        return productRepository.save(product);
    }

    /**
     * Update existing product
     * 
     * @param id Product ID
     * @param productDetails Updated product details
     * @return Updated product
     * @throws RuntimeException if product not found
     */
    public Product updateProduct(Long id, Product productDetails) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        // Update fields
        product.setName(productDetails.getName());
        product.setDescription(productDetails.getDescription());
        product.setPrice(productDetails.getPrice());
        product.setQuantity(productDetails.getQuantity());
        product.setMinStockLevel(productDetails.getMinStockLevel());
        product.setCategory(productDetails.getCategory());
        product.setSupplier(productDetails.getSupplier());

        // Validate before saving
        validateProduct(product);
        
        return productRepository.save(product);
    }

    /**
     * Delete product by ID
     * 
     * @param id Product ID
     * @throws RuntimeException if product not found
     */
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

    /**
     * Search products by name or SKU
     * 
     * @param searchTerm Search term
     * @param pageable Pagination information
     * @return Page of products matching the search criteria
     */
    @Transactional(readOnly = true)
    public Page<Product> searchProducts(String searchTerm, Pageable pageable) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return productRepository.findAll(pageable);
        }
        return productRepository.searchProducts(searchTerm.trim(), pageable);
    }

    /**
     * Get products by category ID
     * 
     * @param categoryId Category ID
     * @return List of products in the specified category
     */
    @Transactional(readOnly = true)
    public List<Product> getProductsByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    /**
     * Get products by supplier ID
     * 
     * @param supplierId Supplier ID
     * @return List of products from the specified supplier
     */
    @Transactional(readOnly = true)
    public List<Product> getProductsBySupplier(Long supplierId) {
        return productRepository.findBySupplierId(supplierId);
    }

    /**
     * Get products with low stock
     * Business logic to identify products that need restocking
     * 
     * @return List of products with low stock
     */
    @Transactional(readOnly = true)
    public List<Product> getLowStockProducts() {
        return productRepository.findLowStockProducts();
    }

    /**
     * Get count of low stock products
     * 
     * @return Number of products with low stock
     */
    @Transactional(readOnly = true)
    public Long getLowStockCount() {
        return productRepository.countLowStockProducts();
    }

    /**
     * Update product stock quantity
     * Business method with validation
     * 
     * @param id Product ID
     * @param newQuantity New stock quantity
     * @return Updated product
     * @throws RuntimeException if product not found
     * @throws IllegalArgumentException if quantity is invalid
     */
    public Product updateStock(Long id, Integer newQuantity) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        if (newQuantity < 0) {
            throw new IllegalArgumentException("Stock quantity cannot be negative");
        }

        product.updateStock(newQuantity);
        return productRepository.save(product);
    }

    /**
     * Get products by price range
     * 
     * @param minPrice Minimum price
     * @param maxPrice Maximum price
     * @return List of products within the specified price range
     */
    @Transactional(readOnly = true)
    public List<Product> getProductsByPriceRange(Double minPrice, Double maxPrice) {
        return productRepository.findByPriceRange(minPrice, maxPrice);
    }

    /**
     * Get total product count
     * 
     * @return Total number of products
     */
    @Transactional(readOnly = true)
    public Long getTotalProductCount() {
        return productRepository.count();
    }

    /**
     * Private method to validate product data
     * Contains business rules and validation logic
     * 
     * @param product Product to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validateProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }

        if (product.getName() == null || product.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Product name is required");
        }

        if (product.getSku() == null || product.getSku().trim().isEmpty()) {
            throw new IllegalArgumentException("Product SKU is required");
        }

        if (product.getPrice() == null || product.getPrice().doubleValue() <= 0) {
            throw new IllegalArgumentException("Product price must be greater than 0");
        }

        if (product.getQuantity() == null || product.getQuantity() < 0) {
            throw new IllegalArgumentException("Product quantity cannot be negative");
        }

        if (product.getMinStockLevel() != null && product.getMinStockLevel() < 0) {
            throw new IllegalArgumentException("Minimum stock level cannot be negative");
        }
    }

    /**
     * Check if product exists by ID
     * 
     * @param id Product ID
     * @return true if product exists, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return productRepository.existsById(id);
    }

    /**
     * Check if SKU exists
     * 
     * @param sku SKU to check
     * @return true if SKU exists, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean existsBySku(String sku) {
        return productRepository.existsBySku(sku);
    }
}
