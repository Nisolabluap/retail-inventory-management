# Retail Inventory System

## Description

The Retail Inventory System is a comprehensive software solution designed to streamline and optimize the management of inventory in a retail environment. This system facilitates efficient handling of various aspects, including Item creation, customer management, and order processing.

- [Technologies and frameworks used](#technologies-and-frameworks-used)
- [API Reference](#api-reference)
    * [Customer](#api-reference)
    * [Item](#api-reference)
    * [Order](#api-reference)

- [Features](#features)
- [Installation](#installation)
- [Authors](#authors)

 ## Technologies and Frameworks Used:
* Java 17
* SpringBoot 3.1.4
* PostMan
* Swagger
* PostgreSQL
* Thymeleaf
* Lombok
* Maven
* Spring Data JPA
* Hibernate
* Jackson
* JUnit

## API Reference

### Customer

| Parameter | Type     | 
| :-------- | :------- | 
| `firstName   ` | `string` | 
| `lastName   ` | `string` | 
| `email   ` | `string` | 
| `birthday  ` | `LocalDate` | 
| `address   ` | `string` |  

#### Get all customers
```http
  GET /api/customers
```
#### Create a new customer
```http
  POST/api/customers
```
#### Update customer
```http
  PUT/api/customers/{id}
```
#### Get a customer by ID
```http
  GET/api/customers/{id}
```
#### Delete a customer by ID
```http
  DELETE/api/customers/{id}
```
#### Get a customers favorites
```http
  GET/api/customer/{customerId}/favorites
```

### Item

| Parameter | Type     | 
| :-------- | :------- | 
| `name   ` | `string` | 
| `description   ` | `string` | 
| `availableQuantity   ` | `long` | 
| `productCategory  ` | `enum` | 
| `price  ` | `double` | 
| `isbn   ` | `string` |

#### Get a search
```http
  GET /api/items/search
```
#### Get all items
```http
  GET /api/items
```
#### Create a item
```http
  POST /api/items/{id}
```
#### Update a item by ID
```http
 PUT /api/items/{id}
```
#### Update a item by ISBN
```http
 PUT /api/items/{id}/{isbn}
```
#### Get a item by ID
```http
GET/api/items/{id}
```
#### Delete a item by ID
```http
 DELETE /api/items/{id}
```

### Order

| Parameter | Type     | 
| :-------- | :------- | 
| `customerID   ` | `long` | 
| `orderItems   ` | `long` | 
| `quantityPerItem  ` | `int` |  

#### Get order by ID
```http
  GET/api/orders/{orderId}
```
#### Create an order
```http
  POST/api/orders/
```
#### Update order status by ID
```http
  POST/api/orders/{orderId}/status"
```

## Features

- **Create products:** Users can create/update/delete/add to favorites products to sell.
- **Orders:** Customers can buy products.
- **Notifications:** Notification system for customers.



## Installation

Install RIM

```
 - Clone this repository: `[git clone https://github.com/Nisolabluap/traveling-booking-system.git](https://github.com/Nisolabluap/retail-inventory-management)`
```
## Authors
- [Paul Baloșin](https://github.com/Nisolabluap)
