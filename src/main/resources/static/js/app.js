/**
 * JavaScript Application for Inventory Management System
 *
 * This file contains all the client-side logic for the inventory management system.
 * It handles API calls, DOM manipulation, and user interactions.
 *
 * @author Inventory Management Team
 * @version 1.0
 */

// API Base URL - Change this to match your backend server
const API_BASE_URL = "http://localhost:8080/api"

// Global variables to store data
let products = []
let categories = []
let suppliers = []
let currentEditingProduct = null
let currentEditingCategory = null
let currentEditingSupplier = null

// Bootstrap library
const bootstrap = window.bootstrap

/**
 * Initialize the application when DOM is loaded
 */
document.addEventListener("DOMContentLoaded", () => {
  console.log("🚀 Inventory Management System Initialized")

  // Load initial data
  loadDashboardData()
  loadCategories()
  loadSuppliers()
  loadProducts()

  // Set up event listeners
  setupEventListeners()

  // Show dashboard by default
  showSection("dashboard")
})

/**
 * Set up event listeners for various UI elements
 */
function setupEventListeners() {
  // Search functionality
  document.getElementById("productSearch").addEventListener("keypress", (e) => {
    if (e.key === "Enter") {
      searchProducts()
    }
  })

  // Navigation active state management
  document.querySelectorAll(".nav-link").forEach((link) => {
    link.addEventListener("click", function () {
      document.querySelectorAll(".nav-link").forEach((l) => l.classList.remove("active"))
      this.classList.add("active")
    })
  })
}

/**
 * Show/Hide different sections of the application
 * @param {string} sectionName - Name of the section to show
 */
function showSection(sectionName) {
  // Hide all sections
  document.querySelectorAll(".content-section").forEach((section) => {
    section.style.display = "none"
  })

  // Show selected section
  const targetSection = document.getElementById(sectionName)
  if (targetSection) {
    targetSection.style.display = "block"

    // Load section-specific data
    switch (sectionName) {
      case "dashboard":
        loadDashboardData()
        break
      case "products":
        loadProducts()
        break
      case "categories":
        loadCategories()
        break
      case "suppliers":
        loadSuppliers()
        break
    }
  }
}

/**
 * Show loading spinner
 */
function showLoading() {
  document.getElementById("loadingSpinner").classList.remove("d-none")
}

/**
 * Hide loading spinner
 */
function hideLoading() {
  document.getElementById("loadingSpinner").classList.add("d-none")
}

/**
 * Show success message
 * @param {string} message - Success message to display
 */
function showSuccess(message) {
  // Create and show Bootstrap toast or alert
  const alertDiv = document.createElement("div")
  alertDiv.className = "alert alert-success alert-dismissible fade show position-fixed"
  alertDiv.style.top = "20px"
  alertDiv.style.right = "20px"
  alertDiv.style.zIndex = "9999"
  alertDiv.innerHTML = `
        ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `
  document.body.appendChild(alertDiv)

  // Auto remove after 3 seconds
  setTimeout(() => {
    if (alertDiv.parentNode) {
      alertDiv.parentNode.removeChild(alertDiv)
    }
  }, 3000)
}

/**
 * Show error message
 * @param {string} message - Error message to display
 */
function showError(message) {
  const alertDiv = document.createElement("div")
  alertDiv.className = "alert alert-danger alert-dismissible fade show position-fixed"
  alertDiv.style.top = "20px"
  alertDiv.style.right = "20px"
  alertDiv.style.zIndex = "9999"
  alertDiv.innerHTML = `
        ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `
  document.body.appendChild(alertDiv)

  setTimeout(() => {
    if (alertDiv.parentNode) {
      alertDiv.parentNode.removeChild(alertDiv)
    }
  }, 5000)
}

// ==================== DASHBOARD FUNCTIONS ====================

/**
 * Load dashboard statistics and data
 */
async function loadDashboardData() {
  try {
    showLoading()

    const response = await fetch(`${API_BASE_URL}/dashboard/stats`)
    if (!response.ok) throw new Error("Failed to load dashboard data")

    const data = await response.json()

    // Update statistics cards
    document.getElementById("totalProducts").textContent = data.totalProducts || 0
    document.getElementById("totalCategories").textContent = data.totalCategories || 0
    document.getElementById("totalSuppliers").textContent = data.totalSuppliers || 0
    document.getElementById("lowStockProducts").textContent = data.lowStockProducts || 0

    // Update low stock products table
    updateLowStockTable(data.lowStockProductsList || [])
  } catch (error) {
    console.error("Error loading dashboard data:", error)
    showError("Failed to load dashboard data")
  } finally {
    hideLoading()
  }
}

/**
 * Update low stock products table
 * @param {Array} lowStockProducts - Array of low stock products
 */
function updateLowStockTable(lowStockProducts) {
  const tableContainer = document.getElementById("lowStockTable")

  if (lowStockProducts.length === 0) {
    tableContainer.innerHTML =
      '<p class="text-success"><i class="fas fa-check-circle"></i> All products are well stocked!</p>'
    return
  }

  let tableHTML = `
        <div class="table-responsive">
            <table class="table table-sm table-striped">
                <thead class="table-warning">
                    <tr>
                        <th>SKU</th>
                        <th>Product Name</th>
                        <th>Current Stock</th>
                        <th>Min Stock Level</th>
                        <th>Action</th>
                    </tr>
                </thead>
                <tbody>
    `

  lowStockProducts.forEach((product) => {
    tableHTML += `
            <tr>
                <td><strong>${product.sku}</strong></td>
                <td>${product.name}</td>
                <td><span class="badge bg-warning">${product.quantity}</span></td>
                <td>${product.minStockLevel}</td>
                <td>
                    <button class="btn btn-sm btn-primary" onclick="updateProductStock(${product.id})">
                        <i class="fas fa-plus"></i> Restock
                    </button>
                </td>
            </tr>
        `
  })

  tableHTML += "</tbody></table></div>"
  tableContainer.innerHTML = tableHTML
}

// ==================== PRODUCT FUNCTIONS ====================

/**
 * Load all products and display in table
 */
async function loadProducts() {
  try {
    showLoading()

    const response = await fetch(`${API_BASE_URL}/products/all`)
    if (!response.ok) throw new Error("Failed to load products")

    products = await response.json()
    displayProducts(products)

    // Also load categories and suppliers for dropdowns
    await loadCategoriesForDropdown()
    await loadSuppliersForDropdown()
  } catch (error) {
    console.error("Error loading products:", error)
    showError("Failed to load products")
  } finally {
    hideLoading()
  }
}

/**
 * Display products in the table
 * @param {Array} productsToDisplay - Array of products to display
 */
function displayProducts(productsToDisplay) {
  const tbody = document.getElementById("productsTableBody")

  if (productsToDisplay.length === 0) {
    tbody.innerHTML = '<tr><td colspan="8" class="text-center">No products found</td></tr>'
    return
  }

  let html = ""
  productsToDisplay.forEach((product) => {
    const status = getStockStatus(product)
    const categoryName = product.category ? product.category.name : "N/A"
    const supplierName = product.supplier ? product.supplier.name : "N/A"

    html += `
            <tr>
                <td><strong>${product.sku}</strong></td>
                <td>${product.name}</td>
                <td>${categoryName}</td>
                <td>${supplierName}</td>
                <td>$${Number.parseFloat(product.price).toFixed(2)}</td>
                <td>${product.quantity}</td>
                <td><span class="badge ${status.class}">${status.text}</span></td>
                <td>
                    <div class="btn-group btn-group-sm">
                        <button class="btn btn-outline-primary" onclick="editProduct(${product.id})" title="Edit">
                            <i class="fas fa-edit"></i>
                        </button>
                        <button class="btn btn-outline-success" onclick="updateProductStock(${product.id})" title="Update Stock">
                            <i class="fas fa-boxes"></i>
                        </button>
                        <button class="btn btn-outline-danger" onclick="deleteProduct(${product.id})" title="Delete">
                            <i class="fas fa-trash"></i>
                        </button>
                    </div>
                </td>
            </tr>
        `
  })

  tbody.innerHTML = html
}

/**
 * Get stock status for a product
 * @param {Object} product - Product object
 * @returns {Object} Status object with class and text
 */
function getStockStatus(product) {
  if (product.quantity === 0) {
    return { class: "status-out-of-stock", text: "Out of Stock" }
  } else if (product.quantity <= product.minStockLevel) {
    return { class: "status-low-stock", text: "Low Stock" }
  } else {
    return { class: "status-in-stock", text: "In Stock" }
  }
}

/**
 * Search products by name or SKU
 */
async function searchProducts() {
  const searchTerm = document.getElementById("productSearch").value.trim()

  if (searchTerm === "") {
    displayProducts(products)
    return
  }

  try {
    showLoading()

    const response = await fetch(`${API_BASE_URL}/products/search?q=${encodeURIComponent(searchTerm)}`)
    if (!response.ok) throw new Error("Search failed")

    const searchResults = await response.json()
    displayProducts(searchResults.content || searchResults)
  } catch (error) {
    console.error("Error searching products:", error)
    showError("Search failed")
  } finally {
    hideLoading()
  }
}

/**
 * Filter products by category
 */
function filterProducts() {
  const categoryId = document.getElementById("categoryFilter").value

  if (categoryId === "") {
    displayProducts(products)
    return
  }

  const filteredProducts = products.filter((product) => product.category && product.category.id == categoryId)

  displayProducts(filteredProducts)
}

/**
 * Show add product modal
 */
function showAddProductModal() {
  currentEditingProduct = null
  document.getElementById("productModalTitle").textContent = "Add Product"
  document.getElementById("productForm").reset()
  document.getElementById("productId").value = ""

  const modal = new bootstrap.Modal(document.getElementById("productModal"))
  modal.show()
}

/**
 * Edit product
 * @param {number} productId - ID of the product to edit
 */
async function editProduct(productId) {
  try {
    const response = await fetch(`${API_BASE_URL}/products/${productId}`)
    if (!response.ok) throw new Error("Failed to load product")

    const product = await response.json()
    currentEditingProduct = product

    // Populate form
    document.getElementById("productModalTitle").textContent = "Edit Product"
    document.getElementById("productId").value = product.id
    document.getElementById("productName").value = product.name
    document.getElementById("productSku").value = product.sku
    document.getElementById("productDescription").value = product.description || ""
    document.getElementById("productPrice").value = product.price
    document.getElementById("productQuantity").value = product.quantity
    document.getElementById("productMinStock").value = product.minStockLevel || 10
    document.getElementById("productCategory").value = product.category ? product.category.id : ""
    document.getElementById("productSupplier").value = product.supplier ? product.supplier.id : ""

    const modal = new bootstrap.Modal(document.getElementById("productModal"))
    modal.show()
  } catch (error) {
    console.error("Error loading product:", error)
    showError("Failed to load product details")
  }
}

/**
 * Save product (create or update)
 */
async function saveProduct() {
  try {
    const productData = {
      name: document.getElementById("productName").value.trim(),
      sku: document.getElementById("productSku").value.trim(),
      description: document.getElementById("productDescription").value.trim(),
      price: Number.parseFloat(document.getElementById("productPrice").value),
      quantity: Number.parseInt(document.getElementById("productQuantity").value),
      minStockLevel: Number.parseInt(document.getElementById("productMinStock").value) || 10,
    }

    // Add category if selected
    const categoryId = document.getElementById("productCategory").value
    if (categoryId) {
      productData.category = { id: Number.parseInt(categoryId) }
    }

    // Add supplier if selected
    const supplierId = document.getElementById("productSupplier").value
    if (supplierId) {
      productData.supplier = { id: Number.parseInt(supplierId) }
    }

    // Validate required fields
    if (!productData.name || !productData.sku || !productData.price || productData.quantity < 0) {
      showError("Please fill in all required fields correctly")
      return
    }

    showLoading()

    let response
    const productId = document.getElementById("productId").value

    if (productId) {
      // Update existing product
      response = await fetch(`${API_BASE_URL}/products/${productId}`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(productData),
      })
    } else {
      // Create new product
      response = await fetch(`${API_BASE_URL}/products`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(productData),
      })
    }

    if (!response.ok) {
      const errorText = await response.text()
      throw new Error(errorText || "Failed to save product")
    }

    showSuccess(productId ? "Product updated successfully!" : "Product created successfully!")

    // Close modal and refresh data
    bootstrap.Modal.getInstance(document.getElementById("productModal")).hide()
    await loadProducts()
    await loadDashboardData()
  } catch (error) {
    console.error("Error saving product:", error)
    showError(error.message || "Failed to save product")
  } finally {
    hideLoading()
  }
}

/**
 * Delete product
 * @param {number} productId - ID of the product to delete
 */
async function deleteProduct(productId) {
  if (!confirm("Are you sure you want to delete this product? This action cannot be undone.")) {
    return
  }

  try {
    showLoading()

    const response = await fetch(`${API_BASE_URL}/products/${productId}`, {
      method: "DELETE",
    })

    if (!response.ok) throw new Error("Failed to delete product")

    showSuccess("Product deleted successfully!")
    await loadProducts()
    await loadDashboardData()
  } catch (error) {
    console.error("Error deleting product:", error)
    showError("Failed to delete product")
  } finally {
    hideLoading()
  }
}

/**
 * Update product stock
 * @param {number} productId - ID of the product to update stock
 */
async function updateProductStock(productId) {
  const newQuantity = prompt("Enter new stock quantity:")

  if (newQuantity === null || newQuantity === "") return

  const quantity = Number.parseInt(newQuantity)
  if (isNaN(quantity) || quantity < 0) {
    showError("Please enter a valid quantity")
    return
  }

  try {
    showLoading()

    const response = await fetch(`${API_BASE_URL}/products/${productId}/stock?quantity=${quantity}`, {
      method: "PATCH",
    })

    if (!response.ok) throw new Error("Failed to update stock")

    showSuccess("Stock updated successfully!")
    await loadProducts()
    await loadDashboardData()
  } catch (error) {
    console.error("Error updating stock:", error)
    showError("Failed to update stock")
  } finally {
    hideLoading()
  }
}

// ==================== CATEGORY FUNCTIONS ====================

/**
 * Load all categories
 */
async function loadCategories() {
  try {
    showLoading()

    const response = await fetch(`${API_BASE_URL}/categories`)
    if (!response.ok) throw new Error("Failed to load categories")

    categories = await response.json()
    displayCategories(categories)
  } catch (error) {
    console.error("Error loading categories:", error)
    showError("Failed to load categories")
  } finally {
    hideLoading()
  }
}

/**
 * Load categories for dropdown
 */
async function loadCategoriesForDropdown() {
  try {
    const response = await fetch(`${API_BASE_URL}/categories`)
    if (!response.ok) throw new Error("Failed to load categories")

    const categories = await response.json()

    // Update product form dropdown
    const productCategorySelect = document.getElementById("productCategory")
    productCategorySelect.innerHTML = '<option value="">Select Category</option>'
    categories.forEach((category) => {
      productCategorySelect.innerHTML += `<option value="${category.id}">${category.name}</option>`
    })

    // Update filter dropdown
    const categoryFilter = document.getElementById("categoryFilter")
    categoryFilter.innerHTML = '<option value="">All Categories</option>'
    categories.forEach((category) => {
      categoryFilter.innerHTML += `<option value="${category.id}">${category.name}</option>`
    })
  } catch (error) {
    console.error("Error loading categories for dropdown:", error)
  }
}

/**
 * Display categories in table
 * @param {Array} categoriesToDisplay - Array of categories to display
 */
function displayCategories(categoriesToDisplay) {
  const tbody = document.getElementById("categoriesTableBody")

  if (categoriesToDisplay.length === 0) {
    tbody.innerHTML = '<tr><td colspan="5" class="text-center">No categories found</td></tr>'
    return
  }

  let html = ""
  categoriesToDisplay.forEach((category) => {
    html += `
            <tr>
                <td>${category.id}</td>
                <td><strong>${category.name}</strong></td>
                <td>${category.description || "N/A"}</td>
                <td><span class="badge bg-info">${category.products ? category.products.length : 0}</span></td>
                <td>
                    <div class="btn-group btn-group-sm">
                        <button class="btn btn-outline-primary" onclick="editCategory(${category.id})" title="Edit">
                            <i class="fas fa-edit"></i>
                        </button>
                        <button class="btn btn-outline-danger" onclick="deleteCategory(${category.id})" title="Delete">
                            <i class="fas fa-trash"></i>
                        </button>
                    </div>
                </td>
            </tr>
        `
  })

  tbody.innerHTML = html
}

/**
 * Show add category modal
 */
function showAddCategoryModal() {
  currentEditingCategory = null
  document.getElementById("categoryModalTitle").textContent = "Add Category"
  document.getElementById("categoryForm").reset()
  document.getElementById("categoryId").value = ""

  const modal = new bootstrap.Modal(document.getElementById("categoryModal"))
  modal.show()
}

/**
 * Edit category
 * @param {number} categoryId - ID of the category to edit
 */
async function editCategory(categoryId) {
  try {
    const response = await fetch(`${API_BASE_URL}/categories/${categoryId}`)
    if (!response.ok) throw new Error("Failed to load category")

    const category = await response.json()
    currentEditingCategory = category

    // Populate form
    document.getElementById("categoryModalTitle").textContent = "Edit Category"
    document.getElementById("categoryId").value = category.id
    document.getElementById("categoryName").value = category.name
    document.getElementById("categoryDescription").value = category.description || ""

    const modal = new bootstrap.Modal(document.getElementById("categoryModal"))
    modal.show()
  } catch (error) {
    console.error("Error loading category:", error)
    showError("Failed to load category details")
  }
}

/**
 * Save category (create or update)
 */
async function saveCategory() {
  try {
    const categoryData = {
      name: document.getElementById("categoryName").value.trim(),
      description: document.getElementById("categoryDescription").value.trim(),
    }

    // Validate required fields
    if (!categoryData.name) {
      showError("Category name is required")
      return
    }

    showLoading()

    let response
    const categoryId = document.getElementById("categoryId").value

    if (categoryId) {
      // Update existing category
      response = await fetch(`${API_BASE_URL}/categories/${categoryId}`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(categoryData),
      })
    } else {
      // Create new category
      response = await fetch(`${API_BASE_URL}/categories`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(categoryData),
      })
    }

    if (!response.ok) {
      const errorText = await response.text()
      throw new Error(errorText || "Failed to save category")
    }

    showSuccess(categoryId ? "Category updated successfully!" : "Category created successfully!")

    // Close modal and refresh data
    bootstrap.Modal.getInstance(document.getElementById("categoryModal")).hide()
    await loadCategories()
    await loadCategoriesForDropdown()
    await loadDashboardData()
  } catch (error) {
    console.error("Error saving category:", error)
    showError(error.message || "Failed to save category")
  } finally {
    hideLoading()
  }
}

/**
 * Delete category
 * @param {number} categoryId - ID of the category to delete
 */
async function deleteCategory(categoryId) {
  if (!confirm("Are you sure you want to delete this category? This action cannot be undone.")) {
    return
  }

  try {
    showLoading()

    const response = await fetch(`${API_BASE_URL}/categories/${categoryId}`, {
      method: "DELETE",
    })

    if (!response.ok) {
      const errorText = await response.text()
      throw new Error(errorText || "Failed to delete category")
    }

    showSuccess("Category deleted successfully!")
    await loadCategories()
    await loadCategoriesForDropdown()
    await loadDashboardData()
  } catch (error) {
    console.error("Error deleting category:", error)
    showError(error.message || "Failed to delete category")
  } finally {
    hideLoading()
  }
}

// ==================== SUPPLIER FUNCTIONS ====================

/**
 * Load all suppliers
 */
async function loadSuppliers() {
  try {
    showLoading()

    const response = await fetch(`${API_BASE_URL}/suppliers`)
    if (!response.ok) throw new Error("Failed to load suppliers")

    suppliers = await response.json()
    displaySuppliers(suppliers)
  } catch (error) {
    console.error("Error loading suppliers:", error)
    showError("Failed to load suppliers")
  } finally {
    hideLoading()
  }
}

/**
 * Load suppliers for dropdown
 */
async function loadSuppliersForDropdown() {
  try {
    const response = await fetch(`${API_BASE_URL}/suppliers`)
    if (!response.ok) throw new Error("Failed to load suppliers")

    const suppliers = await response.json()

    // Update product form dropdown
    const productSupplierSelect = document.getElementById("productSupplier")
    productSupplierSelect.innerHTML = '<option value="">Select Supplier</option>'
    suppliers.forEach((supplier) => {
      productSupplierSelect.innerHTML += `<option value="${supplier.id}">${supplier.name}</option>`
    })
  } catch (error) {
    console.error("Error loading suppliers for dropdown:", error)
  }
}

/**
 * Display suppliers in table
 * @param {Array} suppliersToDisplay - Array of suppliers to display
 */
function displaySuppliers(suppliersToDisplay) {
  const tbody = document.getElementById("suppliersTableBody")

  if (suppliersToDisplay.length === 0) {
    tbody.innerHTML = '<tr><td colspan="7" class="text-center">No suppliers found</td></tr>'
    return
  }

  let html = ""
  suppliersToDisplay.forEach((supplier) => {
    html += `
            <tr>
                <td>${supplier.id}</td>
                <td><strong>${supplier.name}</strong></td>
                <td>${supplier.contactPerson || "N/A"}</td>
                <td>${supplier.email || "N/A"}</td>
                <td>${supplier.phone || "N/A"}</td>
                <td><span class="badge bg-info">${supplier.products ? supplier.products.length : 0}</span></td>
                <td>
                    <div class="btn-group btn-group-sm">
                        <button class="btn btn-outline-primary" onclick="editSupplier(${supplier.id})" title="Edit">
                            <i class="fas fa-edit"></i>
                        </button>
                        <button class="btn btn-outline-danger" onclick="deleteSupplier(${supplier.id})" title="Delete">
                            <i class="fas fa-trash"></i>
                        </button>
                    </div>
                </td>
            </tr>
        `
  })

  tbody.innerHTML = html
}

/**
 * Show add supplier modal
 */
function showAddSupplierModal() {
  currentEditingSupplier = null
  document.getElementById("supplierModalTitle").textContent = "Add Supplier"
  document.getElementById("supplierForm").reset()
  document.getElementById("supplierId").value = ""

  const modal = new bootstrap.Modal(document.getElementById("supplierModal"))
  modal.show()
}

/**
 * Edit supplier
 * @param {number} supplierId - ID of the supplier to edit
 */
async function editSupplier(supplierId) {
  try {
    const response = await fetch(`${API_BASE_URL}/suppliers/${supplierId}`)
    if (!response.ok) throw new Error("Failed to load supplier")

    const supplier = await response.json()
    currentEditingSupplier = supplier

    // Populate form
    document.getElementById("supplierModalTitle").textContent = "Edit Supplier"
    document.getElementById("supplierId").value = supplier.id
    document.getElementById("supplierName").value = supplier.name
    document.getElementById("supplierContact").value = supplier.contactPerson || ""
    document.getElementById("supplierEmail").value = supplier.email || ""
    document.getElementById("supplierPhone").value = supplier.phone || ""
    document.getElementById("supplierAddress").value = supplier.address || ""

    const modal = new bootstrap.Modal(document.getElementById("supplierModal"))
    modal.show()
  } catch (error) {
    console.error("Error loading supplier:", error)
    showError("Failed to load supplier details")
  }
}

/**
 * Save supplier (create or update)
 */
async function saveSupplier() {
  try {
    const supplierData = {
      name: document.getElementById("supplierName").value.trim(),
      contactPerson: document.getElementById("supplierContact").value.trim(),
      email: document.getElementById("supplierEmail").value.trim(),
      phone: document.getElementById("supplierPhone").value.trim(),
      address: document.getElementById("supplierAddress").value.trim(),
    }

    // Validate required fields
    if (!supplierData.name) {
      showError("Supplier name is required")
      return
    }

    showLoading()

    let response
    const supplierId = document.getElementById("supplierId").value

    if (supplierId) {
      // Update existing supplier
      response = await fetch(`${API_BASE_URL}/suppliers/${supplierId}`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(supplierData),
      })
    } else {
      // Create new supplier
      response = await fetch(`${API_BASE_URL}/suppliers`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(supplierData),
      })
    }

    if (!response.ok) {
      const errorText = await response.text()
      throw new Error(errorText || "Failed to save supplier")
    }

    showSuccess(supplierId ? "Supplier updated successfully!" : "Supplier created successfully!")

    // Close modal and refresh data
    bootstrap.Modal.getInstance(document.getElementById("supplierModal")).hide()
    await loadSuppliers()
    await loadSuppliersForDropdown()
    await loadDashboardData()
  } catch (error) {
    console.error("Error saving supplier:", error)
    showError(error.message || "Failed to save supplier")
  } finally {
    hideLoading()
  }
}

/**
 * Delete supplier
 * @param {number} supplierId - ID of the supplier to delete
 */
async function deleteSupplier(supplierId) {
  if (!confirm("Are you sure you want to delete this supplier? This action cannot be undone.")) {
    return
  }

  try {
    showLoading()

    const response = await fetch(`${API_BASE_URL}/suppliers/${supplierId}`, {
      method: "DELETE",
    })

    if (!response.ok) {
      const errorText = await response.text()
      throw new Error(errorText || "Failed to delete supplier")
    }

    showSuccess("Supplier deleted successfully!")
    await loadSuppliers()
    await loadSuppliersForDropdown()
    await loadDashboardData()
  } catch (error) {
    console.error("Error deleting supplier:", error)
    showError(error.message || "Failed to delete supplier")
  } finally {
    hideLoading()
  }
}

// ==================== UTILITY FUNCTIONS ====================

/**
 * Format currency
 * @param {number} amount - Amount to format
 * @returns {string} Formatted currency string
 */
function formatCurrency(amount) {
  return new Intl.NumberFormat("en-US", {
    style: "currency",
    currency: "USD",
  }).format(amount)
}

/**
 * Format date
 * @param {string} dateString - Date string to format
 * @returns {string} Formatted date string
 */
function formatDate(dateString) {
  return new Date(dateString).toLocaleDateString("en-US", {
    year: "numeric",
    month: "short",
    day: "numeric",
  })
}

/**
 * Debounce function to limit API calls
 * @param {Function} func - Function to debounce
 * @param {number} wait - Wait time in milliseconds
 * @returns {Function} Debounced function
 */
function debounce(func, wait) {
  let timeout
  return function executedFunction(...args) {
    const later = () => {
      clearTimeout(timeout)
      func(...args)
    }
    clearTimeout(timeout)
    timeout = setTimeout(later, wait)
  }
}

// Export functions for testing (if needed)
if (typeof module !== "undefined" && module.exports) {
  module.exports = {
    showSection,
    loadDashboardData,
    loadProducts,
    searchProducts,
    saveProduct,
    deleteProduct,
  }
}
