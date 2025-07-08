# 📦 Inventory Management System

A complete, production-ready **Inventory Management System** built with **Java Spring Boot** backend and **HTML/CSS/JavaScript** frontend. This system provides comprehensive inventory tracking, product management, and supplier management capabilities.

## 🚀 Features

### ✅ **Complete Backend (Java Spring Boot)**
- **RESTful API** with full CRUD operations
- **MySQL Database** integration with Hibernate ORM
- **Data Validation** with Bean Validation
- **Transaction Management** for data consistency
- **Error Handling** with proper HTTP status codes
- **API Documentation** with Swagger/OpenAPI

### ✅ **Beautiful Frontend (HTML/CSS/JavaScript)**
- **Responsive Design** with Bootstrap 5
- **Interactive Dashboard** with real-time statistics
- **Product Management** with search and filtering
- **Category & Supplier Management**
- **Low Stock Alerts** and notifications
- **Modern UI/UX** with smooth animations

### ✅ **Core Functionality**
- **Product Management**: Add, edit, delete, and search products
- **Category Management**: Organize products into categories
- **Supplier Management**: Track supplier information
- **Stock Tracking**: Monitor inventory levels
- **Low Stock Alerts**: Automatic notifications for restocking
- **Dashboard Analytics**: Overview of inventory statistics

## 🛠️ Technology Stack

### Backend
- **Java 11+**
- **Spring Boot 2.7.14**
- **Spring Data JPA** (Hibernate)
- **MySQL 8.0+**
- **Maven** for dependency management
- **Bean Validation** for input validation

### Frontend
- **HTML5** with semantic markup
- **CSS3** with modern styling
- **JavaScript ES6+** for interactivity
- **Bootstrap 5** for responsive design
- **Font Awesome** for icons

## 📋 Prerequisites

Before running this application, make sure you have:

- **Java 11 or higher** installed
- **Maven 3.6+** installed
- **MySQL 8.0+** installed and running
- **Git** for cloning the repository

## 🚀 Quick Start

### 1. Clone the Repository
\`\`\`bash
git clone https://github.com/your-username/inventory-management-system.git
cd inventory-management-system
\`\`\`

### 2. Database Setup
Create a MySQL database:
\`\`\`sql
CREATE DATABASE inventory_management;
\`\`\`

### 3. Configure Database Connection
Update \`src/main/resources/application.properties\`:
\`\`\`properties
spring.datasource.url=jdbc:mysql://localhost:3306/inventory_management
spring.datasource.username=your_username
spring.datasource.password=your_password
\`\`\`

### 4. Build and Run
\`\`\`bash
# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
\`\`\`

### 5. Access the Application
- **Frontend**: http://localhost:8080
- **API Documentation**: http://localhost:8080/swagger-ui.html
- **H2 Console** (if using H2): http://localhost:8080/h2-console

## 📁 Project Structure

\`\`\`
inventory-management-system/
├── src/
│   ├── main/
│   │   ├── java/com/inventory/
│   │   │   ├── InventoryManagementApplication.java
│   │   │   ├── controller/          # REST Controllers
│   │   │   ├── model/              # Entity Classes
│   │   │   ├── repository/         # Data Access Layer
│   │   │   └── service/            # Business Logic Layer
│   │   └── resources/
│   │       ├── static/             # Frontend Files
│   │       │   ├── css/
│   │       │   ├── js/
│   │       │   └── index.html
│   │       └── application.properties
│   └── test/                       # Test Files
├── pom.xml                         # Maven Configuration
└── README.md
\`\`\`

## 🔧 Configuration

### Database Profiles

The application supports multiple profiles:

#### Development (default)
\`\`\`properties
spring.profiles.active=dev
# Uses MySQL with detailed logging
\`\`\`

#### Production
\`\`\`properties
spring.profiles.active=prod
# Optimized for production with minimal logging
\`\`\`

#### Testing
\`\`\`properties
spring.profiles.active=test
# Uses H2 in-memory database
\`\`\`

### Environment Variables

For production deployment, use environment variables:
\`\`\`bash
export DB_USERNAME=your_db_username
export DB_PASSWORD=your_db_password
\`\`\`

## 📊 API Endpoints

### Products
- \`GET /api/products\` - Get all products (paginated)
- \`GET /api/products/{id}\` - Get product by ID
- \`POST /api/products\` - Create new product
- \`PUT /api/products/{id}\` - Update product
- \`DELETE /api/products/{id}\` - Delete product
- \`GET /api/products/search?q={term}\` - Search products
- \`GET /api/products/low-stock\` - Get low stock products

### Categories
- \`GET /api/categories\` - Get all categories
- \`POST /api/categories\` - Create new category
- \`PUT /api/categories/{id}\` - Update category
- \`DELETE /api/categories/{id}\` - Delete category

### Suppliers
- \`GET /api/suppliers\` - Get all suppliers
- \`POST /api/suppliers\` - Create new supplier
- \`PUT /api/suppliers/{id}\` - Update supplier
- \`DELETE /api/suppliers/{id}\` - Delete supplier

### Dashboard
- \`GET /api/dashboard/stats\` - Get dashboard statistics
- \`GET /api/dashboard/health\` - Get system health

## 🧪 Testing

### Run Unit Tests
\`\`\`bash
mvn test
\`\`\`

### Run Integration Tests
\`\`\`bash
mvn verify
\`\`\`

### Generate Test Coverage Report
\`\`\`bash
mvn jacoco:report
# Report available at target/site/jacoco/index.html
\`\`\`

## 🚀 Deployment

### JAR Deployment
\`\`\`bash
# Build JAR file
mvn clean package -Pprod

# Run JAR file
java -jar target/inventory-management-system-1.0.0.jar
\`\`\`

### Docker Deployment
\`\`\`dockerfile
FROM openjdk:11-jre-slim
COPY target/inventory-management-system-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
\`\`\`

### Cloud Deployment
The application is ready for deployment on:
- **AWS EC2** with RDS MySQL
- **Google Cloud Platform**
- **Microsoft Azure**
- **Heroku** (with ClearDB MySQL)

## 🔒 Security Considerations

For production deployment:

1. **Change default passwords**
2. **Enable HTTPS/SSL**
3. **Configure CORS properly**
4. **Use environment variables for secrets**
5. **Enable Spring Security** (if needed)
6. **Set up database connection pooling**

## 📈 Performance Optimization

- **Database Indexing**: Indexes on frequently queried columns
- **Connection Pooling**: HikariCP for optimal database connections
- **Caching**: Consider adding Redis for frequently accessed data
- **Pagination**: All list endpoints support pagination
- **Lazy Loading**: JPA entities use lazy loading where appropriate

## 🐛 Troubleshooting

### Common Issues

1. **Database Connection Error**
   - Check MySQL is running
   - Verify credentials in application.properties
   - Ensure database exists

2. **Port Already in Use**
   - Change port in application.properties: \`server.port=8081\`

3. **Build Failures**
   - Ensure Java 11+ is installed
   - Run \`mvn clean install\` to refresh dependencies

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (\`git checkout -b feature/amazing-feature\`)
3. Commit your changes (\`git commit -m 'Add amazing feature'\`)
4. Push to the branch (\`git push origin feature/amazing-feature\`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👥 Authors

- **Your Name** - *Initial work* - [YourGitHub](https://github.com/yourusername)

## 🙏 Acknowledgments

- Spring Boot team for the excellent framework
- Bootstrap team for the responsive CSS framework
- MySQL team for the reliable database system
- All contributors who helped improve this project

## 📞 Support

If you have any questions or need help:

1. **Check the documentation** above
2. **Search existing issues** on GitHub
3. **Create a new issue** with detailed information
4. **Contact the maintainers**

---

## 🎯 Interview Ready Features

This project demonstrates:

✅ **Full-Stack Development** - Complete backend and frontend
✅ **RESTful API Design** - Proper HTTP methods and status codes
✅ **Database Design** - Normalized schema with relationships
✅ **Error Handling** - Comprehensive error management
✅ **Input Validation** - Server-side and client-side validation
✅ **Code Documentation** - Extensive comments and documentation
✅ **Testing** - Unit and integration tests
✅ **Security** - Input sanitization and validation
✅ **Performance** - Optimized queries and caching strategies
✅ **Deployment** - Production-ready configuration

**Perfect for demonstrating your skills in technical interviews!** 🚀

---

*Made with ❤️ for learning and professional development*
