package com.inventory.controller;

import com.inventory.model.Category;
import com.inventory.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * Category REST Controller
 * 
 * This controller handles HTTP requests related to category operations.
 */
@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "*")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * Get all categories
     * 
     * GET /api/categories
     * 
     * @return ResponseEntity containing list of all categories
     */
    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        try {
            List<Category> categories = categoryService.getAllCategories();
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get category by ID
     * 
     * GET /api/categories/{id}
     * 
     * @param id Category ID
     * @return ResponseEntity containing the category or 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
        try {
            Optional<Category> category = categoryService.getCategoryById(id);
            return category.map(ResponseEntity::ok)
                          .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Create a new category
     * 
     * POST /api/categories
     * 
     * @param category Category data from request body
     * @return ResponseEntity containing the created category
     */
    @PostMapping
    public ResponseEntity<?> createCategory(@Valid @RequestBody Category category) {
        try {
            Category savedCategory = categoryService.saveCategory(category);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedCategory);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Validation Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("Error creating category: " + e.getMessage());
        }
    }

    /**
     * Update an existing category
     * 
     * PUT /api/categories/{id}
     * 
     * @param id Category ID
     * @param categoryDetails Updated category data
     * @return ResponseEntity containing the updated category
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable Long id, 
                                           @Valid @RequestBody Category categoryDetails) {
        try {
            Category updatedCategory = categoryService.updateCategory(id, categoryDetails);
            return ResponseEntity.ok(updatedCategory);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Validation Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("Error updating category: " + e.getMessage());
        }
    }

    /**
     * Delete a category
     * 
     * DELETE /api/categories/{id}
     * 
     * @param id Category ID
     * @return ResponseEntity with appropriate status
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        try {
            categoryService.deleteCategory(id);
            return ResponseEntity.ok().body("Category deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("Error deleting category: " + e.getMessage());
        }
    }

    /**
     * Search categories by name
     * 
     * GET /api/categories/search?q=searchTerm
     * 
     * @param searchTerm Search query
     * @return ResponseEntity containing list of matching categories
     */
    @GetMapping("/search")
    public ResponseEntity<List<Category>> searchCategories(@RequestParam("q") String searchTerm) {
        try {
            List<Category> categories = categoryService.searchCategories(searchTerm);
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get total category count
     * 
     * GET /api/categories/count
     * 
     * @return ResponseEntity containing total category count
     */
    @GetMapping("/count")
    public ResponseEntity<Long> getTotalCategoryCount() {
        try {
            Long count = categoryService.getTotalCategoryCount();
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
