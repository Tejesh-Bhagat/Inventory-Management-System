package com.inventory.repository;

import com.inventory.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Supplier Repository Interface
 * 
 * This interface provides data access methods for Supplier entity.
 */
@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    /**
     * Find supplier by name
     * 
     * @param name Supplier name
     * @return Optional containing the supplier if found
     */
    Optional<Supplier> findByName(String name);

    /**
     * Find supplier by email
     * 
     * @param email Supplier email
     * @return Optional containing the supplier if found
     */
    Optional<Supplier> findByEmail(String email);

    /**
     * Find suppliers by name containing the search term (case-insensitive)
     * 
     * @param name Search term
     * @return List of suppliers matching the search criteria
     */
    List<Supplier> findByNameContainingIgnoreCase(String name);

    /**
     * Check if supplier email exists
     * 
     * @param email Email to check
     * @return true if email exists, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Find all suppliers ordered by name
     * 
     * @return List of suppliers ordered alphabetically by name
     */
    List<Supplier> findAllByOrderByNameAsc();

    /**
     * Find suppliers with products
     * Custom query to get suppliers along with their products
     * 
     * @return List of suppliers with product information
     */
    @Query("SELECT s FROM Supplier s LEFT JOIN FETCH s.products")
    List<Supplier> findAllWithProducts();

    /**
     * Count suppliers
     * 
     * @return Total number of suppliers
     */
    @Query("SELECT COUNT(s) FROM Supplier s")
    Long countSuppliers();

    /**
     * Find suppliers by contact person
     * 
     * @param contactPerson Contact person name
     * @return List of suppliers with the specified contact person
     */
    List<Supplier> findByContactPersonContainingIgnoreCase(String contactPerson);
}
