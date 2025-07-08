package com.inventory.model;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Product Entity Class
 * 
 * This class represents a product in our inventory system.
 * It uses JPA annotations for database mapping and validation annotations
 * for input validation.
 * 
 * @Entity - Marks this class as a JPA entity
 * @Table - Specifies the table name in the database
 */
@Entity
@Table(name = "products")
public class Product {

    /**
     * Primary key for the product
     * @Id - Marks this field as the primary key
     * @GeneratedValue - Specifies that the value is auto-generated
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Product name with validation
     * @NotBlank - Ensures the field is not null and not empty
     * @Size - Validates the length of the string
     */
    @NotBlank(message = "Product name is required")
    @Size(min = 2, max = 100, message = "Product name must be between 2 and 100 characters")
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * Product description
     */
    @Column(length = 500)
    private String description;

    /**
     * Product SKU (Stock Keeping Unit) - unique identifier
     */
    @NotBlank(message = "SKU is required")
    @Column(unique = true, nullable = false, length = 50)
    private String sku;

    /**
     * Product price with validation
     * @DecimalMin - Ensures minimum value
     * @Digits - Validates decimal precision
     */
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Price format is invalid")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    /**
     * Current stock quantity
     */
    @NotNull(message = "Quantity is required")
    @Min(value = 0, message = "Quantity cannot be negative")
    @Column(nullable = false)
    private Integer quantity;

    /**
     * Minimum stock level for alerts
     */
    @Min(value = 0, message = "Minimum stock level cannot be negative")
    @Column(name = "min_stock_level")
    private Integer minStockLevel = 10;

    /**
     * Many-to-One relationship with Category
     * @ManyToOne - Defines the relationship type
     * @JoinColumn - Specifies the foreign key column
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    /**
     * Many-to-One relationship with Supplier
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    /**
     * Timestamp when the product was created
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when the product was last updated
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * JPA callback method to set timestamps before persisting
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    /**
     * JPA callback method to update timestamp before updating
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Default constructor required by JPA
    public Product() {}

    /**
     * Constructor with essential fields
     */
    public Product(String name, String sku, BigDecimal price, Integer quantity) {
        this.name = name;
        this.sku = sku;
        this.price = price;
        this.quantity = quantity;
    }

    /**
     * Business method to check if product is low in stock
     * @return true if current quantity is below minimum stock level
     */
    public boolean isLowStock() {
        return quantity != null && minStockLevel != null && quantity <= minStockLevel;
    }

    /**
     * Business method to update stock quantity
     * @param newQuantity New quantity to set
     * @throws IllegalArgumentException if quantity is negative
     */
    public void updateStock(Integer newQuantity) {
        if (newQuantity < 0) {
            throw new IllegalArgumentException("Stock quantity cannot be negative");
        }
        this.quantity = newQuantity;
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Integer getMinStockLevel() { return minStockLevel; }
    public void setMinStockLevel(Integer minStockLevel) { this.minStockLevel = minStockLevel; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public Supplier getSupplier() { return supplier; }
    public void setSupplier(Supplier supplier) { this.supplier = supplier; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", sku='" + sku + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                '}';
    }
}
