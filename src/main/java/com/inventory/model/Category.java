package com.inventory.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Category Entity Class
 * 
 * This class represents product categories in our inventory system.
 * Categories help organize products into logical groups.
 */
@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Category name with validation
     */
    @NotBlank(message = "Category name is required")
    @Size(min = 2, max = 50, message = "Category name must be between 2 and 50 characters")
    @Column(unique = true, nullable = false, length = 50)
    private String name;

    /**
     * Category description
     */
    @Column(length = 200)
    private String description;

    /**
     * One-to-Many relationship with Products
     * @OneToMany - Defines the relationship type
     * mappedBy - Specifies the field in Product entity that owns the relationship
     * cascade - Defines cascade operations
     * fetch - Defines fetching strategy
     */
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Product> products = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Constructors
    public Category() {}

    public Category(String name, String description) {
        this.name = name;
        this.description = description;
    }

    /**
     * Business method to get product count in this category
     * @return Number of products in this category
     */
    public int getProductCount() {
        return products != null ? products.size() : 0;
    }

    /**
     * Business method to add a product to this category
     * @param product Product to add
     */
    public void addProduct(Product product) {
        if (products == null) {
            products = new ArrayList<>();
        }
        products.add(product);
        product.setCategory(this);
    }

    /**
     * Business method to remove a product from this category
     * @param product Product to remove
     */
    public void removeProduct(Product product) {
        if (products != null) {
            products.remove(product);
            product.setCategory(null);
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<Product> getProducts() { return products; }
    public void setProducts(List<Product> products) { this.products = products; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
