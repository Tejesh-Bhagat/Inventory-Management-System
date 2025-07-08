package com.inventory.controller;

import com.inventory.model.Product;
import com.inventory.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * Product REST Controller
 * 
 * This controller handles HTTP requests related to product operations.
 * It provides RESTful endpoints for CRUD operations and additional business features.
 * 
 * @RestController - Combines @Controller and @ResponseBody
 * @RequestMapping - Maps HTTP requests to handler methods
 * @CrossOrigin - Enables CORS for frontend integration
 */
@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
public class ProductController {

    @Autowired
    private ProductService productService;

    /**
     * Get all products with pagination and sorting
     * 
     * GET /api/products?page=0&size=10&sort=name,asc
     * 
     * @param page Page number (default: 0)
     * @param size Page size (default: 10)
     * @param sort Sort criteria (default: id,asc)
     * @return ResponseEntity containing page of products
     */
    @GetMapping
    public ResponseEntity<Page<Product>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        try {
            // Create sort object based on direction
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                       Sort.by(sortBy).descending() : 
                       Sort.by(sortBy).ascending();
            
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<Product> products = productService.getAllProducts(pageable);
            
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get all products as a simple list (for dropdowns, etc.)
     * 
     * GET /api/products/all
     * 
     * @return ResponseEntity containing list of all products
     */
    @GetMapping("/all")
    public ResponseEntity<List<Product>> getAllProductsList() {
        try {
            List<Product> products = productService.getAllProducts();
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get product by ID
     * 
     * GET /api/products/{id}
     * 
     * @param id Product ID
     * @return ResponseEntity containing the product or 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        try {
            Optional<Product> product = productService.getProductById(id);
            return product.map(ResponseEntity::ok)
                         .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get product by SKU
     * 
     * GET /api/products/sku/{sku}
     * 
     * @param sku Product SKU
     * @return ResponseEntity containing the product or 404 if not found
     */
    @GetMapping("/sku/{sku}")
    public ResponseEntity<Product> getProductBySku(@PathVariable String sku) {
        try {
            Optional<Product> product = productService.getProductBySku(sku);
            return product.map(ResponseEntity::ok)
                         .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Create a new product
     * 
     * POST /api/products
     * 
     * @param product Product data from request body
     * @return ResponseEntity containing the created product
     */
    @PostMapping
    public ResponseEntity<?> createProduct(@Valid @RequestBody Product product) {
        try {
            Product savedProduct = productService.saveProduct(product);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Validation Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("Error creating product: " + e.getMessage());
        }
    }

    /**
     * Update an existing product
     * 
     * PUT /api/products/{id}
     * 
     * @param id Product ID
     * @param productDetails Updated product data
     * @return ResponseEntity containing the updated product
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, 
                                          @Valid @RequestBody Product productDetails) {
        try {
            Product updatedProduct = productService.updateProduct(id, productDetails);
            return ResponseEntity.ok(updatedProduct);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Validation Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("Error updating product: " + e.getMessage());
        }
    }

    /**
     * Delete a product
     * 
     * DELETE /api/products/{id}
     * 
     * @param id Product ID
     * @return ResponseEntity with appropriate status
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.ok().body("Product deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("Error deleting product: " + e.getMessage());
        }
    }

    /**
     * Search products by name or SKU
     * 
     * GET /api/products/search?q=searchTerm&page=0&size=10
     * 
     * @param searchTerm Search query
     * @param page Page number
     * @param size Page size
     * @return ResponseEntity containing page of matching products
     */
    @GetMapping("/search")
    public ResponseEntity<Page<Product>> searchProducts(
            @RequestParam("q") String searchTerm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Product> products = productService.searchProducts(searchTerm, pageable);
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get products by category
     * 
     * GET /api/products/category/{categoryId}
     * 
     * @param categoryId Category ID
     * @return ResponseEntity containing list of products in the category
     */
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<Product>> getProductsByCategory(@PathVariable Long categoryId) {
        try {
            List<Product> products = productService.getProductsByCategory(categoryId);
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get products by supplier
     * 
     * GET /api/products/supplier/{supplierId}
     * 
     * @param supplierId Supplier ID
     * @return ResponseEntity containing list of products from the supplier
     */
    @GetMapping("/supplier/{supplierId}")
    public ResponseEntity<List<Product>> getProductsBySupplier(@PathVariable Long supplierId) {
        try {
            List<Product> products = productService.getProductsBySupplier(supplierId);
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get products with low stock
     * 
     * GET /api/products/low-stock
     * 
     * @return ResponseEntity containing list of low stock products
     */
    @GetMapping("/low-stock")
    public ResponseEntity<List<Product>> getLowStockProducts() {
        try {
            List<Product> products = productService.getLowStockProducts();
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get count of low stock products
     * 
     * GET /api/products/low-stock/count
     * 
     * @return ResponseEntity containing count of low stock products
     */
    @GetMapping("/low-stock/count")
    public ResponseEntity<Long> getLowStockCount() {
        try {
            Long count = productService.getLowStockCount();
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Update product stock quantity
     * 
     * PATCH /api/products/{id}/stock
     * 
     * @param id Product ID
     * @param quantity New stock quantity
     * @return ResponseEntity containing updated product
     */
    @PatchMapping("/{id}/stock")
    public ResponseEntity<?> updateStock(@PathVariable Long id, 
                                        @RequestParam Integer quantity) {
        try {
            Product updatedProduct = productService.updateStock(id, quantity);
            return ResponseEntity.ok(updatedProduct);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Validation Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("Error updating stock: " + e.getMessage());
        }
    }

    /**
     * Get products by price range
     * 
     * GET /api/products/price-range?min=10&max=100
     * 
     * @param minPrice Minimum price
     * @param maxPrice Maximum price
     * @return ResponseEntity containing list of products in price range
     */
    @GetMapping("/price-range")
    public ResponseEntity<List<Product>> getProductsByPriceRange(
            @RequestParam Double minPrice,
            @RequestParam Double maxPrice) {
        
        try {
            List<Product> products = productService.getProductsByPriceRange(minPrice, maxPrice);
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get total product count
     * 
     * GET /api/products/count
     * 
     * @return ResponseEntity containing total product count
     */
    @GetMapping("/count")
    public ResponseEntity<Long> getTotalProductCount() {
        try {
            Long count = productService.getTotalProductCount();
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Check if SKU exists
     * 
     * GET /api/products/check-sku/{sku}
     * 
     * @param sku SKU to check
     * @return ResponseEntity containing boolean result
     */
    @GetMapping("/check-sku/{sku}")
    public ResponseEntity<Boolean> checkSkuExists(@PathVariable String sku) {
        try {
            boolean exists = productService.existsBySku(sku);
            return ResponseEntity.ok(exists);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
