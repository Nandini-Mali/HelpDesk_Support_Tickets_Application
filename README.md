# Help Desk and Support Ticket Application

## Project Overview

The Help Desk and Support Ticket Application is a backend REST API developed using Spring Boot and MySQL. It helps users raise support tickets and allows agents to manage, track, and resolve those tickets.

The application supports user management, ticket creation, comments, ticket history, authentication, and role-based authorization.

## Objectives

- Develop REST APIs using Spring Boot.
- Manage users and support tickets.
- Implement USER and AGENT roles.
- Track ticket status and priority changes.
- Maintain ticket history.
- Handle exceptions with meaningful error messages.
- Secure APIs using Spring Security.

## Technologies Used

| Technology | Purpose |
|---|---|
| Java 17 | Programming Language |
| Spring Boot 4.0.0 | Backend Framework |
| Spring Data JPA | Database Operations |
| Hibernate | ORM |
| MySQL | Database |
| Spring Security | Authentication and Authorization |
| Maven | Dependency Management |
| Postman | API Testing |
| Eclipse IDE | Development Environment |
| Git & GitHub | Version Control |

## Key Features

### 1. User Management
- User registration.
- User login and credential verification.
- Get user details by ID and email.
- Update user information.
- Soft delete user accounts.
- Duplicate email validation.

### 2. Ticket Management
- Create support tickets.
- Retrieve tickets.
- View tickets based on user access.
- Update ticket status.
- Update ticket priority.
- Filter tickets by status and priority.
- Support pagination.

### 3. Comment Management
- Add comments to tickets.
- Retrieve comments.
- View individual comments.
- Manage comments based on user roles and access permissions.

### 4. Ticket History
- Track ticket status changes.
- Track priority changes.
- Store previous and updated values.
- Record who made the changes and when.

### 5. Security
- Spring Security configuration.
- HTTP Basic Authentication.
- BCrypt password encoding.
- Role-based authorization for USER and AGENT.
- Protected REST API endpoints.

### 6. Exception Handling
- Centralized exception handling.
- Resource not found handling.
- Duplicate resource handling.
- Validation error handling.
- Meaningful API error responses.

## User Roles

| Role | Permissions |
|---|---|
| USER | Create tickets, view own tickets, and add comments. |
| AGENT | View and manage tickets, update status and priority, and access user management APIs. |

## Ticket Status Flow

Tickets follow this status flow:

`OPEN → IN_PROGRESS → CLOSED`

Invalid status transitions are rejected.

## Project Structure

```text
helpdesk/
│
├── src/main/java/com/nexora/helpdesk/
│   │
│   ├── controller/
│   ├── service/
│   ├── repository/
│   ├── entity/
│   ├── dto/
│   ├── enums/
│   ├── security/
│   └── exception/
│
├── src/main/resources/
│   └── application.properties
│
├── src/test/
│
├── pom.xml
└── README.md
```

## Database Configuration

The application uses MySQL.

### Step 1: Create Database

```sql
CREATE DATABASE helpdesk_db;
```

### Step 2: Configure application.properties

Update your database configuration in:

`src/main/resources/application.properties`

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/helpdesk_db
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD

spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

Replace `YOUR_MYSQL_PASSWORD` with your local MySQL password.

## How to Run the Project

### Prerequisites

- Java 17
- MySQL Server
- Maven
- Eclipse IDE or another Java IDE
- Postman

### Steps

1. Clone the repository:

```bash
git clone https://github.com/Nandini-Mali/HelpDesk_Support_Tickets_Application.git
```

2. Open the project in Eclipse as an existing Maven project.

3. Create the MySQL database:

```sql
CREATE DATABASE helpdesk_db;
```

4. Configure your MySQL username and password in `application.properties`.

5. Update Maven dependencies.

6. Run the main application class:

`HelpdeskApplication.java`

7. The application will start at:

`http://localhost:8080`

## API Endpoints

The following is the planned/current API endpoint reference. Confirm each mapping against the controller before using it.

### Authentication APIs

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/auth/register` | Register a new user |
| POST | `/api/auth/login` | Login and verify credentials |

### User APIs

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/users` | Get all users |
| GET | `/api/users/{id}` | Get user by ID |
| GET | `/api/users/email/{email}` | Get user by email |
| PUT | `/api/users/{id}` | Update user |
| DELETE | `/api/users/{id}` | Soft delete user |

### Ticket APIs

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/tickets` | Create a ticket |
| GET | `/api/tickets` | Get tickets |
| GET | `/api/tickets/{ticketId}` | Get ticket by ID |
| GET | `/api/tickets/user/{userId}` | Get tickets by user |
| PUT | `/api/tickets/{ticketId}/status` | Update ticket status |
| PUT | `/api/tickets/{ticketId}/priority` | Update ticket priority |
| GET | `/api/tickets/status/{status}` | Filter tickets by status |
| GET | `/api/tickets/priority/{priority}` | Filter tickets by priority |

### Comment APIs

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/comments/ticket/{ticketId}/user/{userId}` | Add comment |
| GET | `/api/comments/ticket/{ticketId}` | Get ticket comments |
| GET | `/api/comments/{commentId}` | Get comment by ID |
| DELETE | `/api/comments/{commentId}/user/{userId}` | Delete comment |

### Ticket History APIs

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/ticket-history/ticket/{ticketId}/user/{userId}` | Get ticket history |
| GET | `/api/ticket-history/{historyId}/user/{userId}` | Get history by ID |

## Authentication and API Testing

Use Postman to test REST API endpoints.

For protected APIs:

1. Open the Authorization tab in Postman.
2. Select Basic Auth.
3. Enter the registered user's email as the username.
4. Enter the original registered password.
5. Send the API request.

The application uses BCrypt to store encoded passwords in the database.

## Testing

API testing is being performed using Postman.

Testing includes:

- User registration and login.
- Duplicate email validation.
- User management APIs.
- Ticket creation and retrieval.
- Ticket status and priority updates.
- Comment APIs.
- Ticket history APIs.
- Authentication and role-based authorization.
- Exception handling and validation.

## Future Enhancements

- Complete API testing.
- Add JUnit 5 test cases.
- Improve authentication using JWT.
- Implement average ticket closing time reports.
- Improve filtering and pagination.
- Prepare a Postman collection.
- Add an Entity Relationship Diagram (ER Diagram).

## Developer

**Nandini Mali**

GitHub: https://github.com/Nandini-Mali

## Project Status

Backend development and security configuration are implemented. REST API testing and final verification are in progress.
