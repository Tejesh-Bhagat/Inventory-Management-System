package com.inventory.service;

import com.inventory.model.Category;
import com.inventory.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Category Service Class
 * 
 * This service class contains business logic for category operations.
 */
@Service
@Transactional
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    /**
     * Get all categories
     * 
     * @return List of all categories ordered by name
     */
    @Transactional(readOnly = true)
    public List<Category> getAllCategories() {
        return categoryRepository.findAllByOrderByNameAsc();
    }

    /**
     * Get category by ID
     * 
     * @param id Category ID
     * @return Optional containing the category if found
     */
    @Transactional(readOnly = true)
    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    /**
     * Get category by name
     * 
     * @param name Category name
     * @return Optional containing the category if found
     */
    @Transactional(readOnly = true)
    public Optional<Category> getCategoryByName(String name) {
        return categoryRepository.findByName(name);
    }

    /**
     * Save a new category or update existing category
     * 
     * @param category Category to save
     * @return Saved category
     * @throws IllegalArgumentException if validation fails
     */
    public Category saveCategory(Category category) {
        validateCategory(category);
        
        // Check for duplicate name (only for new categories)
        if (category.getId() == null && categoryRepository.existsByName(category.getName())) {
            throw new IllegalArgumentException("Category with name '" + category.getName() + "' already exists");
        }
        
        return categoryRepository.save(category);
    }

    /**
     * Update existing category
     * 
     * @param id Category ID
     * @param categoryDetails Updated category details
     * @return Updated category
     * @throws RuntimeException if category not found
     */
    public Category updateCategory(Long id, Category categoryDetails) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));

        category.setName(categoryDetails.getName());
        category.setDescription(categoryDetails.getDescription());

        validateCategory(category);
        
        return categoryRepository.save(category);
    }

    /**
     * Delete category by ID
     * 
     * @param id Category ID
     * @throws RuntimeException if category not found or has associated products
     */
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));

        // Check if category has associated products
        if (category.getProductCount() > 0) {
            throw new RuntimeException("Cannot delete category with associated products. Please reassign or delete products first.");
        }

        categoryRepository.deleteById(id);
    }

    /**
     * Search categories by name
     * 
     * @param searchTerm Search term
     * @return List of categories matching the search criteria
     */
    @Transactional(readOnly = true)
    public List<Category> searchCategories(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllCategories();
        }
        return categoryRepository.findByNameContainingIgnoreCase(searchTerm.trim());
    }

    /**
     * Get total category count
     * 
     * @return Total number of categories
     */
    @Transactional(readOnly = true)
    public Long getTotalCategoryCount() {
        return categoryRepository.count();
    }

    /**
     * Check if category exists by ID
     * 
     * @param id Category ID
     * @return true if category exists, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return categoryRepository.existsById(id);
    }

    /**
     * Check if category name exists
     * 
     * @param name Category name
     * @return true if name exists, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return categoryRepository.existsByName(name);
    }

    /**
     * Private method to validate category data
     * 
     * @param category Category to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validateCategory(Category category) {
        if (category == null) {
            throw new IllegalArgumentException("Category cannot be null");
        }

        if (category.getName() == null || category.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Category name is required");
        }

        if (category.getName().length() > 50) {
            throw new IllegalArgumentException("Category name cannot exceed 50 characters");
        }
    }
}
