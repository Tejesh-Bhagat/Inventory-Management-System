package com.inventory.repository;

import com.inventory.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Product Repository Interface
 * 
 * This interface extends JpaRepository to provide CRUD operations for Product entity.
 * Spring Data JPA automatically implements this interface at runtime.
 * 
 * @Repository - Marks this interface as a repository component
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Find product by SKU (Stock Keeping Unit)
     * Spring Data JPA automatically generates the implementation based on method name
     * 
     * @param sku Product SKU
     * @return Optional containing the product if found
     */
    Optional<Product> findBySku(String sku);

    /**
     * Find products by category ID
     * 
     * @param categoryId Category ID
     * @return List of products in the specified category
     */
    List<Product> findByCategoryId(Long categoryId);

    /**
     * Find products by supplier ID
     * 
     * @param supplierId Supplier ID
     * @return List of products from the specified supplier
     */
    List<Product> findBySupplierId(Long supplierId);

    /**
     * Find products with low stock
     * Uses custom JPQL query to find products where quantity <= minStockLevel
     * 
     * @return List of products with low stock
     */
    @Query("SELECT p FROM Product p WHERE p.quantity <= p.minStockLevel")
    List<Product> findLowStockProducts();

    /**
     * Search products by name containing the search term (case-insensitive)
     * 
     * @param name Search term
     * @param pageable Pagination information
     * @return Page of products matching the search criteria
     */
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);

    /**
     * Search products by name or SKU containing the search term (case-insensitive)
     * Custom JPQL query for advanced search functionality
     * 
     * @param searchTerm Search term to match against name or SKU
     * @param pageable Pagination information
     * @return Page of products matching the search criteria
     */
    @Query("SELECT p FROM Product p WHERE " +
           "LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.sku) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Product> searchProducts(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Find products by category name
     * Uses JOIN to search by category name instead of ID
     * 
     * @param categoryName Category name
     * @return List of products in the specified category
     */
    @Query("SELECT p FROM Product p JOIN p.category c WHERE c.name = :categoryName")
    List<Product> findByCategoryName(@Param("categoryName") String categoryName);

    /**
     * Find products by supplier name
     * Uses JOIN to search by supplier name instead of ID
     * 
     * @param supplierName Supplier name
     * @return List of products from the specified supplier
     */
    @Query("SELECT p FROM Product p JOIN p.supplier s WHERE s.name = :supplierName")
    List<Product> findBySupplierName(@Param("supplierName") String supplierName);

    /**
     * Count products with low stock
     * 
     * @return Number of products with low stock
     */
    @Query("SELECT COUNT(p) FROM Product p WHERE p.quantity <= p.minStockLevel")
    Long countLowStockProducts();

    /**
     * Find products with quantity less than specified amount
     * 
     * @param quantity Quantity threshold
     * @return List of products with quantity less than specified amount
     */
    List<Product> findByQuantityLessThan(Integer quantity);

    /**
     * Check if SKU exists (useful for validation)
     * 
     * @param sku SKU to check
     * @return true if SKU exists, false otherwise
     */
    boolean existsBySku(String sku);

    /**
     * Find products ordered by creation date (newest first)
     * 
     * @param pageable Pagination information
     * @return Page of products ordered by creation date
     */
    Page<Product> findAllByOrderByCreatedAtDesc(Pageable pageable);

    /**
     * Find products by price range
     * 
     * @param minPrice Minimum price
     * @param maxPrice Maximum price
     * @return List of products within the specified price range
     */
    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice")
    List<Product> findByPriceRange(@Param("minPrice") Double minPrice, @Param("maxPrice") Double maxPrice);
}
