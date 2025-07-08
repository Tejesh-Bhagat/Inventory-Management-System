package com.inventory.model;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Supplier Entity Class
 * 
 * This class represents suppliers who provide products to our inventory.
 * Suppliers are companies or individuals from whom we purchase products.
 */
@Entity
@Table(name = "suppliers")
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Supplier company name
     */
    @NotBlank(message = "Supplier name is required")
    @Size(min = 2, max = 100, message = "Supplier name must be between 2 and 100 characters")
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * Contact person name
     */
    @Size(max = 100, message = "Contact person name cannot exceed 100 characters")
    @Column(name = "contact_person", length = 100)
    private String contactPerson;

    /**
     * Supplier email with validation
     */
    @Email(message = "Please provide a valid email address")
    @Column(length = 100)
    private String email;

    /**
     * Supplier phone number
     */
    @Size(max = 20, message = "Phone number cannot exceed 20 characters")
    @Column(length = 20)
    private String phone;

    /**
     * Supplier address
     */
    @Column(length = 200)
    private String address;

    /**
     * One-to-Many relationship with Products
     */
    @OneToMany(mappedBy = "supplier", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
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
    public Supplier() {}

    public Supplier(String name, String contactPerson, String email, String phone) {
        this.name = name;
        this.contactPerson = contactPerson;
        this.email = email;
        this.phone = phone;
    }

    /**
     * Business method to get product count supplied by this supplier
     * @return Number of products supplied by this supplier
     */
    public int getProductCount() {
        return products != null ? products.size() : 0;
    }

    /**
     * Business method to add a product to this supplier
     * @param product Product to add
     */
    public void addProduct(Product product) {
        if (products == null) {
            products = new ArrayList<>();
        }
        products.add(product);
        product.setSupplier(this);
    }

    /**
     * Business method to remove a product from this supplier
     * @param product Product to remove
     */
    public void removeProduct(Product product) {
        if (products != null) {
            products.remove(product);
            product.setSupplier(null);
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getContactPerson() { return contactPerson; }
    public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public List<Product> getProducts() { return products; }
    public void setProducts(List<Product> products) { this.products = products; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "Supplier{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", contactPerson='" + contactPerson + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
