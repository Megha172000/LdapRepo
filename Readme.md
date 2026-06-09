# LDAP User & Group Management API

## Overview

This project is a Spring Boot application that provides REST APIs for managing LDAP users and groups using Spring LDAP.

The application supports:

* Creating LDAP users
* Creating LDAP groups
* Adding users to LDAP groups
* LDAP integration using Spring LDAP (`LdapTemplate`)
* Request/Response logging using SLF4J
* Exception handling for LDAP operations

---

## Technologies Used

* Java 17
* Spring Boot
* Spring LDAP
* Maven
* Lombok
* OpenLDAP

---

## Project Structure

```text
src/main/java
├── controller
│   ├── UserController
│   └── GroupController
│
├── service
│   ├── UserService
│   └── GroupService
│
├── dto
│   ├── UserDto
│   ├── GroupDto
│   ├── CreateGroupRequestDto
│   ├── AddUserToGroupRequest
│   └── GroupResponseDto
│
└── config
```

---

## LDAP Structure

```text
dc=example,dc=com
│
├── ou=users
│   ├── uid=john
│   ├── uid=alice
│   └── uid=testuser
│
└── ou=groups
    ├── cn=developers
    ├── cn=admins
    └── cn=test-group
```

---

# API Endpoints

## 1. Create LDAP User

### Endpoint

```http
POST /create-user
```

### Request

```json
{
  "uid": "john",
  "firstName": "John",
  "lastName": "Doe",
  "email": "john@example.com",
  "mobileNumber": "9876543210",
  "department": "Engineering",
  "designation": "Software Engineer",
  "password": "password123"
}
```

### Response

```json
{
  "uid": "john",
  "firstName": "John Doe",
  "email": "john@example.com",
  "mobileNumber": "9876543210",
  "department": "Engineering",
  "designation": "Software Engineer"
}
```

---

## 2. Create LDAP Group

### Endpoint

```http
POST /groups/create-group
```

### Request

```json
{
  "groupName": "developers",
  "description": "Development Team"
}
```

### Response

```json
{
  "groupName": "developers",
  "description": "Development Team",
  "message": "LDAP group created successfully"
}
```

---

## 3. Add User To Group

### Endpoint

```http
POST /groups/add-user-to-group
```

### Request

```json
{
  "username": "john",
  "groupName": "developers"
}
```

### Response

```json
{
  "username": "john",
  "groupName": "developers",
  "message": "User added to LDAP group successfully"
}
```

---

## LDAP Configuration

Example application.properties

```properties
spring.ldap.urls=ldap://localhost:389
spring.ldap.base=dc=example,dc=com
spring.ldap.username=cn=admin,dc=example,dc=com
spring.ldap.password=admin
```

---

## Running the Application

### Clone Repository

```bash
git clone https://github.com/your-username/ldap-manager.git
```

### Navigate to Project

```bash
cd ldap-manager
```

### Build Project

```bash
mvn clean install
```

### Run Application

```bash
mvn spring-boot:run
```

Application will start on:

```text
http://localhost:8080
```

---

## Logging

The application uses SLF4J logging for:

* User creation requests
* Group creation requests
* Group membership updates
* LDAP operation success logs
* LDAP operation failure logs

Example:

```text
INFO  Creating LDAP user with uid [john]
INFO  LDAP user created successfully [john]

INFO  Creating LDAP group: developers
INFO  LDAP group created successfully: developers

INFO  Adding user [john] to group [developers]
INFO  User [john] successfully added to group [developers]
```

---

## Future Enhancements

* Update LDAP User
* Delete LDAP User
* Delete LDAP Group
* Remove User From Group
* Search Users
* Search Groups
* Global Exception Handling
* Swagger/OpenAPI Documentation
* Unit & Integration Tests

---

## Author

LDAP User & Group Management API built using Spring Boot and Spring LDAP.
