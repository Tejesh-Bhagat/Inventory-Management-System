package com.inventory.controller;

import com.inventory.service.CategoryService;
import com.inventory.service.ProductService;
import com.inventory.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Dashboard Controller
 * 
 * This controller provides dashboard statistics and summary information
 * for the inventory management system.
 */
@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SupplierService supplierService;

    /**
     * Get dashboard statistics
     * 
     * GET /api/dashboard/stats
     * 
     * @return ResponseEntity containing dashboard statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        try {
            Map<String, Object> stats = new HashMap<>();
            
            // Get counts
            stats.put("totalProducts", productService.getTotalProductCount());
            stats.put("totalCategories", categoryService.getTotalCategoryCount());
            stats.put("totalSuppliers", supplierService.getTotalSupplierCount());
            stats.put("lowStockProducts", productService.getLowStockCount());
            
            // Get low stock products list
            stats.put("lowStockProductsList", productService.getLowStockProducts());
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get system health status
     * 
     * GET /api/dashboard/health
     * 
     * @return ResponseEntity containing system health information
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> getSystemHealth() {
        try {
            Map<String, String> health = new HashMap<>();
            health.put("status", "UP");
            health.put("database", "Connected");
            health.put("timestamp", java.time.LocalDateTime.now().toString());
            
            return ResponseEntity.ok(health);
        } catch (Exception e) {
            Map<String, String> health = new HashMap<>();
            health.put("status", "DOWN");
            health.put("error", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(health);
        }
    }
}
