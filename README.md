# HelpDesk Support Ticket System

A desktop-based HelpDesk Support Ticket System developed using Java Swing, Spring Boot, and MySQL. The application provides a centralized platform where users can create and track support tickets, while support agents can manage tickets, update their status and priority, and communicate through comments.

The system follows a client-server architecture, with a Java Swing desktop application communicating with a Spring Boot backend through REST APIs.

## Key Features

### 1. User Authentication

- User registration and login.
- Secure password storage using BCrypt.
- Role-based access control.
- USER and AGENT account roles.
- Active and inactive account handling.

### 2. Ticket Management

- Create support tickets.
- View ticket details.
- View personal tickets.
- Filter tickets by status and priority.
- Paginated ticket listing.
- Update ticket status and priority.
- Ticket ownership and access validation.

### 3. Comment Management

- Add comments to tickets.
- View ticket comments.
- Retrieve individual comments.
- Delete comments with authorization checks.

### 4. Ticket History

- Track ticket status changes.
- Track priority changes.
- Store previous and updated values.
- Record who performed each change.
- View ticket history.

### 5. Application Security

- Spring Security integration.
- BCrypt password encoding.
- HTTP Basic authentication.
- Role-based authorization.
- Authenticated user identity verification.
- Ticket and comment access validation.
- Stateless authentication.

## Technology Stack

| Technology      | Usage                             |
| --------------- | --------------------------------- |
| Java 17         | Application development           |
| Spring Boot 4   | Backend development               |
| Spring Security | Authentication and authorization  |
| Spring Data JPA | Database interaction              |
| Hibernate       | Object-relational mapping         |
| MySQL           | Database management               |
| Java Swing      | Desktop GUI                       |
| REST API        | Client-server communication       |
| Maven           | Project and dependency management |
| Jackson         | JSON processing                   |
| Eclipse IDE     | Development environment           |
| Postman         | API testing                       |

## System Architecture

The application follows a three-layer backend architecture with a desktop client.

```text
       JAVA SWING DESKTOP CLIENT
                  |
                  |
              REST APIs
                  |
                  v
        SPRING BOOT BACKEND
                  |
        ---------------------
        |         |         |
    Controller  Service  Security
        |         |         |
        ---------------------
                  |
                  v
             Repository
                  |
                  v
              MYSQL DB
```

### Working Process

1. Users interact with the Java Swing desktop application.
2. The desktop client sends HTTP requests to the Spring Boot backend.
3. Spring Security verifies authentication and authorization.
4. Controllers receive and process API requests.
5. Service classes handle business logic and validations.
6. Repository interfaces perform database operations.
7. MySQL stores application information.
8. The backend returns JSON responses to the Swing client.

## Project Structure

```text
helpdesk/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/nexora/helpdesk/
│   │   │
│   │   │       ├── HelpdeskApplication.java
│   │   │
│   │   │       ├── controller/
│   │   │       ├── dto/
│   │   │       ├── entity/
│   │   │       ├── enums/
│   │   │       ├── exception/
│   │   │       ├── repository/
│   │   │       ├── security/
│   │   │       ├── service/
│   │   │
│   │   │       └── desktop/
│   │   │           ├── Main.java
│   │   │           ├── api/
│   │   │           ├── model/
│   │   │           ├── ui/
│   │   │           └── util/
│   │   │
│   │   └── resources/
│   │       └── application.properties
│   │
│   └── test/
│
├── .gitignore
├── pom.xml
└── README.md
```

### Package Responsibilities

| Package       | Description                                      |
| ------------- | ------------------------------------------------ |
| controller    | Handles REST API requests                        |
| dto           | Request and response data objects                |
| entity        | Database entity classes                          |
| enums         | User roles, ticket priorities and statuses       |
| exception     | Custom exceptions and global exception handling  |
| repository    | Database access interfaces                       |
| security      | Spring Security configuration and authentication |
| service       | Business logic and validation                    |
| desktop.api   | REST API communication                           |
| desktop.model | Desktop application models                       |
| desktop.ui    | Java Swing screens                               |
| desktop.util  | Session and utility management                   |

## Database Configuration

The application uses MySQL as its relational database.

### 1. Create Database

Execute the following SQL command:

```sql
CREATE DATABASE helpdesk_db;
```

### 2. Configure Database Connection

Update `src/main/resources/application.properties`:

```properties
spring.application.name=HelpDeskSupportSystem

spring.datasource.url=jdbc:mysql://localhost:3306/helpdesk_db
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
```

Configure `DB_USERNAME` and `DB_PASSWORD` as environment variables.


## Installation and Setup

### Prerequisites

- Java JDK 17
- Eclipse IDE
- MySQL Server
- Maven
- Postman (optional, for API testing)

### Step 1: Clone the Repository

```bash
git clone YOUR_REPOSITORY_URL
```

### Step 2: Import Project into Eclipse

1. Open Eclipse IDE.
2. Select File → Import.
3. Choose Existing Maven Projects.
4. Browse to the project directory.
5. Finish the import.

### Step 3: Configure MySQL

1. Start MySQL Server.
2. Create the `helpdesk_db` database.
3. Configure database environment variables.
4. Verify the database connection.

### Step 4: Start Backend

Run the following class:

`HelpdeskApplication.java`

The backend starts at:

`http://localhost:8080`

### Step 5: Start Desktop Application

Run:

`com.nexora.helpdesk.desktop.Main`

Ensure that the Spring Boot backend is running before starting the Swing application.

## REST API Modules

The backend provides REST APIs for the following modules:

| Module            | Endpoint                                | HTTP Method        |
| ----------------- | --------------------------------------- | ------------------ |
| Authentication    | `/api/auth/register`                    | POST               |
| Authentication    | `/api/auth/login`                       | POST               |
| User Management   | `/api/users/**`                         | GET / PUT / DELETE |
| Ticket Management | `/api/tickets`                          | GET / POST         |
| Ticket Details    | `/api/tickets/{ticketId}`               | GET                |
| Ticket Status     | `/api/tickets/{ticketId}/status`        | PUT                |
| Ticket Priority   | `/api/tickets/{ticketId}/priority`      | PUT                |
| Comments          | `/api/comments/ticket/{ticketId}`       | GET / POST         |
| Comments          | `/api/comments/{commentId}`             | GET / DELETE       |
| Ticket History    | `/api/ticket-history/ticket/{ticketId}` | GET                |
| Ticket History    | `/api/ticket-history/{historyId}`       | GET                |

Protected endpoints require HTTP Basic authentication. Actual access also depends on the user's role and resource ownership.

## User Roles and Permissions

### USER

- Register and log in.
- Create support tickets.
- View personal tickets.
- Add comments to accessible tickets.
- View accessible ticket history.

### AGENT

- View tickets from different users.
- Update ticket status.
- Update ticket priority.
- Access user and ticket information according to permissions.
- Manage comments according to authorization rules.

## Ticket Status Workflow

```text
       OPEN
         |
         v
    IN_PROGRESS
         |
         v
      CLOSED
```

The backend validates status transitions before saving changes.

## Testing

The application can be tested using Postman and the Java Swing desktop client.

Important test cases include:

- User registration and login.
- Invalid login credentials.
- Unauthorized API access.
- USER and AGENT permissions.
- Ticket creation and retrieval.
- Ticket ownership validation.
- Ticket status and priority updates.
- Comment operations.
- Ticket history retrieval.
- Logout and session management.
- Database connectivity.

A separate testing report will document test cases, actual results, and bugs fixed.

## Application Screenshots

Create a `screenshots` folder in the project and add actual screenshots of the desktop application.

Suggested screenshots:

1. Login Screen
2. Registration Screen
3. User Dashboard
4. Create Ticket Screen
5. Ticket List Screen
6. Ticket Details Screen
7. Agent Dashboard
8. Ticket History Screen

## Future Enhancements

- Ticket assignment to support agents.
- Email notifications.
- Advanced ticket search.
- Improved reporting and analytics.
- Additional automated tests.
- Enhanced desktop user experience.

## Developer Information

**Developer:** Nandini Mali 
**Project Name:** HelpDesk Support Ticket System 
**Project Type:** Java Desktop Application with REST API Backend

**Technologies:** Java, Spring Boot, Spring Security, Java Swing, MySQL, Spring Data JPA, Hibernate, Maven and REST APIs.

---

*Developed as part of practical Java development experience, focusing on desktop application development, backend integration, database management, and application security.*

