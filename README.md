# Capstone 3 — E-Commerce API and Video Game Store

## Overview

This project is a Spring Boot backend API for an e-commerce web application. The application supports product browsing, category management, user authentication, shopping cart persistence, and user profile management. The backend connects to a MySQL database and is designed to support the provided Video Game Store frontend.

The main goal of this capstone was to work as a backend developer on an existing e-commerce application by fixing product-related bugs, implementing missing controller/service logic, and adding new API features using a layered Spring Boot architecture.

## Project Features

### Authentication

- Register a new user account.
- Log in with existing credentials.
- Receive a JWT token after login.
- Use the JWT token to access protected API endpoints.

### Product API

- View all products.
- View a product by ID.
- Search and filter products by:
  - Category
  - Minimum price
  - Maximum price
  - Subcategory
  - Featured status
- Admin users can create, update, and delete products.
- Fixed product update logic so stock changes are saved correctly.

### Category API

- View all categories.
- View a category by ID.
- View products by category.
- Admin users can create, update, and delete categories.

### Shopping Cart API

- Logged-in users can view their shopping cart.
- Logged-in users can add products to their cart.
- If a product already exists in the cart, the quantity increases by 1.
- Logged-in users can update the quantity of an existing cart item.
- Logged-in users can clear all items from their cart.
- Cart items are saved in the database, so the cart persists after logout.

### User Profile API

- Logged-in users can view their profile.
- Logged-in users can update profile fields such as name, phone, email, address, city, state, and zip.

## Technologies Used

- Java 17
- Spring Boot
- Spring Web MVC
- Spring Security
- JWT Authentication
- Spring Data JPA
- Hibernate
- MySQL
- Maven
- Insomnia
- Git and GitHub

## Project Structure

```text
capstone-3/
├── backend/
│   ├── database/
│   ├── src/main/java/org/yearup/
│   │   ├── controllers/
│   │   ├── models/
│   │   ├── repository/
│   │   ├── security/
│   │   └── service/
│   ├── pom.xml
│   └── openapi.yaml
├── frontend/
│   └── capstone-client-videogamestore/
└── capstone-insomnia-collection.yaml
```

## Setup Instructions

### 1. Clone the repository

```bash
git clone https://github.com/luicho1228/capstone-3.git
cd capstone-3/backend
```

### 2. Set up the database

Open MySQL Workbench and run the appropriate SQL script from the `backend/database` folder. This will create the e-commerce database, sample products, categories, users, profiles, shopping cart table, orders table, and order line item table.

The starter database includes sample users. The password for the demo users is:

```text
password
```

Recommended demo users:

```text
user / password
admin / password
george / password
```

### 3. Configure database connection

Update the backend application configuration with your local MySQL username and password.

Example:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/easyshop
spring.datasource.username=root
spring.datasource.password=your_password
```

Use the database name that matches the SQL script you selected.

### 4. Run the backend API

From the `backend` folder, run:

```bash
./mvnw spring-boot:run
```

On Windows:

```bash
mvnw.cmd spring-boot:run
```

The API should start at:

```text
http://localhost:8080
```

### 5. Test with Insomnia

Import the included Insomnia collection:

```text
capstone-insomnia-collection.yaml
```

Use `/login` first to get a JWT token, then add the token to requests that require authentication.

## Authentication Endpoints

| Method | Endpoint | Access | Description |
|---|---|---|---|
| POST | `/register` | Public | Register a new user |
| POST | `/login` | Public | Log in and receive JWT token |

Example login body:

```json
{
  "username": "user",
  "password": "password"
}
```

## Product Endpoints

| Method | Endpoint | Access | Description |
|---|---|---|---|
| GET | `/products` | Public | Get all products or search/filter products |
| GET | `/products/{id}` | Public | Get one product by ID |
| POST | `/products` | Admin | Create a product |
| PUT | `/products/{id}` | Admin | Update a product |
| DELETE | `/products/{id}` | Admin | Delete a product |

Example product search URLs:

```text
GET /products
GET /products?cat=1
GET /products?cat=1&subCategory=Black
GET /products?minPrice=25
GET /products?minPrice=25&maxPrice=100
```

## Category Endpoints

| Method | Endpoint | Access | Description |
|---|---|---|---|
| GET | `/categories` | Public | Get all categories |
| GET | `/categories/{id}` | Public | Get one category by ID |
| GET | `/categories/{id}/products` | Public | Get all products in a category |
| POST | `/categories` | Admin | Create a category |
| PUT | `/categories/{id}` | Admin | Update a category |
| DELETE | `/categories/{id}` | Admin | Delete a category |

Example category body:

```json
{
  "name": "Accessories",
  "description": "Gaming accessories and equipment"
}
```

## Shopping Cart Endpoints

Shopping cart endpoints require a logged-in user with the `ROLE_USER` role.

| Method | Endpoint | Access | Description |
|---|---|---|---|
| GET | `/cart` | User | Get current user's shopping cart |
| POST | `/cart/products/{productId}` | User | Add product to cart |
| PUT | `/cart/products/{productId}` | User | Update quantity for an existing cart item |
| DELETE | `/cart` | User | Clear current user's cart |

Example add-to-cart request:

```text
POST /cart/products/15
```

Example update-cart body:

```json
{
  "quantity": 3
}
```

Example shopping cart response:

```json
{
  "items": {
    "15": {
      "product": {
        "productId": 15,
        "name": "External Hard Drive",
        "price": 129.99,
        "categoryId": 1,
        "description": "Expand your storage capacity and backup your important files.",
        "subCategory": "Gray",
        "stock": 25,
        "featured": true,
        "imageUrl": "external-hard-drive.jpg"
      },
      "quantity": 1,
      "discountPercent": 0,
      "lineTotal": 129.99
    }
  },
  "total": 129.99
}
```

## Profile Endpoints

Profile endpoints require a logged-in user with the `ROLE_USER` role.

| Method | Endpoint | Access | Description |
|---|---|---|---|
| GET | `/profile` | User | Get current user's profile |
| PUT | `/profile` | User | Update current user's profile |

Example profile update body:

```json
{
  "firstName": "Luis",
  "lastName": "Vasquez",
  "phone": "555-555-5555",
  "email": "student@example.com",
  "address": "123 Main St",
  "city": "New York",
  "state": "NY",
  "zip": "10001"
}
```

## Interesting Code Highlight

One important feature I implemented was the shopping cart service logic. The shopping cart table stores only the `userId`, `productId`, and `quantity`, but the API response needs full product details. To solve this, the service loads the user's cart rows, finds each product, creates a `ShoppingCartItem`, and adds it to the final `ShoppingCart` response.

```java
public ShoppingCart getByUserId(int userId) {
    ShoppingCart cart = new ShoppingCart();
    List<CartItem> cartItems = shoppingCartRepository.findByUserId(userId);

    for (CartItem cartItem : cartItems) {
        Product product = productService.getById(cartItem.getProductId());

        ShoppingCartItem shoppingCartItem = new ShoppingCartItem();
        shoppingCartItem.setProduct(product);
        shoppingCartItem.setQuantity(cartItem.getQuantity());

        cart.add(shoppingCartItem);
    }

    return cart;
}
```

This keeps the database design simple while still returning a complete cart response to the frontend.

## Screenshots

Add screenshots to a folder like `docs/images/` and update the image paths below.

### Product Listing Page

![Product Listing Page](docs/images/product-listing.png)

### Product Search or Filter

![Product Search or Filter](docs/images/product-search.png)

### Shopping Cart Page

![Shopping Cart Page](docs/images/shopping-cart.png)

### Insomnia API Test

![Insomnia API Test](docs/images/insomnia-cart-test.png)

## Bugs Fixed

### Product Search Bug

The product search functionality was returning incomplete or incorrect results. I updated the search logic so that products are loaded correctly and filtered by category, price range, subcategory, and featured status.

### Product Update Bug

Product updates were not fully saving all fields. I fixed the update logic so that stock changes are saved correctly when an admin updates a product.

## Future Improvements

- Add checkout functionality that converts a shopping cart into an order.
- Add order history for users.
- Improve cart validation for invalid or negative quantities.
- Add more unit and integration tests for controllers and services.
- Improve frontend styling and error messages.
- Add admin dashboard functionality.

## Author

Luis V.G.

Capstone 3 — E-Commerce API and Site
