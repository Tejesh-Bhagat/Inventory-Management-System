package com.inventory.controller;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.inventory.model.Supplier;
import com.inventory.service.SupplierService;

/**
 * Supplier REST Controller
 * 
 * This controller handles HTTP requests related to supplier operations.
 */
@RestController
@RequestMapping("/api/suppliers")
@CrossOrigin(origins = "*")
public class SupplierController {

    @Autowired
    private SupplierService supplierService;

    /**
     * Get all suppliers
     */
    @GetMapping
    public ResponseEntity<List<Supplier>> getAllSuppliers() {
        try {
            List<Supplier> suppliers = supplierService.getAllSuppliers();
            return ResponseEntity.ok(suppliers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get supplier by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Supplier> getSupplierById(@PathVariable Long id) {
        try {
            Optional<Supplier> supplier = supplierService.getSupplierById(id);
            return supplier.map(ResponseEntity::ok)
                           .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Create a new supplier
     */
    @PostMapping
    public ResponseEntity<?> createSupplier(@Valid @RequestBody Supplier supplier) {
        try {
            Supplier savedSupplier = supplierService.saveSupplier(supplier);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedSupplier);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Validation Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error creating supplier: " + e.getMessage());
        }
    }

    /**
     * Update an existing supplier
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateSupplier(@PathVariable Long id, 
                                            @Valid @RequestBody Supplier supplierDetails) {
        try {
            Supplier updatedSupplier = supplierService.updateSupplier(id, supplierDetails);
            return ResponseEntity.ok(updatedSupplier);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Validation Error: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error updating supplier: " + e.getMessage());
        }
    }

    /**
     * Delete a supplier
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSupplier(@PathVariable Long id) {
        try {
            supplierService.deleteSupplier(id);
            return ResponseEntity.ok().body("Supplier deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error deleting supplier: " + e.getMessage());
        }
    }

    /**
     * Search suppliers by name
     */
    @GetMapping("/search")
    public ResponseEntity<List<Supplier>> searchSuppliers(@RequestParam("q") String searchTerm) {
        try {
            List<Supplier> suppliers = supplierService.searchSuppliers(searchTerm);
            return ResponseEntity.ok(suppliers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get total supplier count
     */
    @GetMapping("/count")
    public ResponseEntity<Long> getTotalSupplierCount() {
        try {
            Long count = supplierService.getTotalSupplierCount();
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
