package com.inventory.service;

import com.inventory.model.Supplier;
import com.inventory.repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Supplier Service Class
 * 
 * This service class contains business logic for supplier operations.
 */
@Service
@Transactional
public class SupplierService {

    @Autowired
    private SupplierRepository supplierRepository;

    /**
     * Get all suppliers
     * 
     * @return List of all suppliers ordered by name
     */
    @Transactional(readOnly = true)
    public List<Supplier> getAllSuppliers() {
        return supplierRepository.findAllByOrderByNameAsc();
    }

    /**
     * Get supplier by ID
     * 
     * @param id Supplier ID
     * @return Optional containing the supplier if found
     */
    @Transactional(readOnly = true)
    public Optional<Supplier> getSupplierById(Long id) {
        return supplierRepository.findById(id);
    }

    /**
     * Get supplier by name
     * 
     * @param name Supplier name
     * @return Optional containing the supplier if found
     */
    @Transactional(readOnly = true)
    public Optional<Supplier> getSupplierByName(String name) {
        return supplierRepository.findByName(name);
    }

    /**
     * Get supplier by email
     * 
     * @param email Supplier email
     * @return Optional containing the supplier if found
     */
    @Transactional(readOnly = true)
    public Optional<Supplier> getSupplierByEmail(String email) {
        return supplierRepository.findByEmail(email);
    }

    /**
     * Save a new supplier or update existing supplier
     * 
     * @param supplier Supplier to save
     * @return Saved supplier
     * @throws IllegalArgumentException if validation fails
     */
    public Supplier saveSupplier(Supplier supplier) {
        validateSupplier(supplier);
        
        // Check for duplicate email (only for new suppliers)
        if (supplier.getId() == null && supplier.getEmail() != null && 
            supplierRepository.existsByEmail(supplier.getEmail())) {
            throw new IllegalArgumentException("Supplier with email '" + supplier.getEmail() + "' already exists");
        }
        
        return supplierRepository.save(supplier);
    }

    /**
     * Update existing supplier
     * 
     * @param id Supplier ID
     * @param supplierDetails Updated supplier details
     * @return Updated supplier
     * @throws RuntimeException if supplier not found
     */
    public Supplier updateSupplier(Long id, Supplier supplierDetails) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found with id: " + id));

        supplier.setName(supplierDetails.getName());
        supplier.setContactPerson(supplierDetails.getContactPerson());
        supplier.setEmail(supplierDetails.getEmail());
        supplier.setPhone(supplierDetails.getPhone());
        supplier.setAddress(supplierDetails.getAddress());

        validateSupplier(supplier);
        
        return supplierRepository.save(supplier);
    }

    /**
     * Delete supplier by ID
     * 
     * @param id Supplier ID
     * @throws RuntimeException if supplier not found or has associated products
     */
    public void deleteSupplier(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found with id: " + id));

        // Check if supplier has associated products
        if (supplier.getProductCount() > 0) {
            throw new RuntimeException("Cannot delete supplier with associated products. Please reassign or delete products first.");
        }

        supplierRepository.deleteById(id);
    }

    /**
     * Search suppliers by name
     * 
     * @param searchTerm Search term
     * @return List of suppliers matching the search criteria
     */
    @Transactional(readOnly = true)
    public List<Supplier> searchSuppliers(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllSuppliers();
        }
        return supplierRepository.findByNameContainingIgnoreCase(searchTerm.trim());
    }

    /**
     * Get total supplier count
     * 
     * @return Total number of suppliers
     */
    @Transactional(readOnly = true)
    public Long getTotalSupplierCount() {
        return supplierRepository.count();
    }

    /**
     * Check if supplier exists by ID
     * 
     * @param id Supplier ID
     * @return true if supplier exists, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return supplierRepository.existsById(id);
    }

    /**
     * Check if supplier email exists
     * 
     * @param email Email to check
     * @return true if email exists, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return supplierRepository.existsByEmail(email);
    }

    /**
     * Private method to validate supplier data
     * 
     * @param supplier Supplier to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validateSupplier(Supplier supplier) {
        if (supplier == null) {
            throw new IllegalArgumentException("Supplier cannot be null");
        }

        if (supplier.getName() == null || supplier.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Supplier name is required");
        }

        if (supplier.getName().length() > 100) {
            throw new IllegalArgumentException("Supplier name cannot exceed 100 characters");
        }

        // Validate email format if provided
        if (supplier.getEmail() != null && !supplier.getEmail().trim().isEmpty()) {
            if (!isValidEmail(supplier.getEmail())) {
                throw new IllegalArgumentException("Invalid email format");
            }
        }

        // Validate phone format if provided
        if (supplier.getPhone() != null && !supplier.getPhone().trim().isEmpty()) {
            if (supplier.getPhone().length() > 20) {
                throw new IllegalArgumentException("Phone number cannot exceed 20 characters");
            }
        }
    }

    /**
     * Simple email validation
     * 
     * @param email Email to validate
     * @return true if email format is valid, false otherwise
     */
    private boolean isValidEmail(String email) {
        return email.contains("@") && email.contains(".");
    }
}
