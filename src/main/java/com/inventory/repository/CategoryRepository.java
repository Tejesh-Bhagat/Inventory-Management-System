package com.inventory.repository;

import com.inventory.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Category Repository Interface
 * 
 * This interface provides data access methods for Category entity.
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Find category by name
     * 
     * @param name Category name
     * @return Optional containing the category if found
     */
    Optional<Category> findByName(String name);

    /**
     * Find categories by name containing the search term (case-insensitive)
     * 
     * @param name Search term
     * @return List of categories matching the search criteria
     */
    List<Category> findByNameContainingIgnoreCase(String name);

    /**
     * Check if category name exists
     * 
     * @param name Category name to check
     * @return true if name exists, false otherwise
     */
    boolean existsByName(String name);

    /**
     * Find all categories ordered by name
     * 
     * @return List of categories ordered alphabetically by name
     */
    List<Category> findAllByOrderByNameAsc();

    /**
     * Find categories with products count
     * Custom query to get categories along with their product count
     * 
     * @return List of categories with product count information
     */
    @Query("SELECT c FROM Category c LEFT JOIN FETCH c.products")
    List<Category> findAllWithProducts();

    /**
     * Count categories
     * 
     * @return Total number of categories
     */
    @Query("SELECT COUNT(c) FROM Category c")
    Long countCategories();
}
