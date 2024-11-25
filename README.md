# CashCard REST Service

This project implements a RESTful API for managing CashCard resources. It provides endpoints for creating, retrieving, updating, and deleting CashCards with proper security, pagination, and sorting support.

## Endpoints

### GET /cashcards/{id}
Retrieves a specific CashCard by its ID. Requires authentication and authorization (user must own the card).

### POST /cashcards
Creates a new CashCard resource. Requires authentication. The new CashCard is associated with the authenticated user.

### GET /cashcards
Retrieves a paginated list of CashCards owned by the authenticated user. Supports sorting by amount. 

### PUT /cashcards/{id}
Updates an existing CashCard's amount. Requires authentication and authorization (user must own the card). The ID and owner fields cannot be modified.

### DELETE /cashcards/{id} 
Deletes a CashCard resource. Requires authentication and authorization (user must own the card).

## Security
The API uses Spring Security for authentication and authorization:
- All endpoints require authentication (Basic Auth)
- Only users with the CARD-OWNER role can access /cashcards resources
- Users can only access and modify their own CashCards
- Passwords are securely hashed using BCrypt

## Testing
The project includes a comprehensive test suite (CashcardApplicationTests) that covers:
- CRUD operations
- Pagination and sorting
- Authentication and authorization
- Error handling and edge cases

The tests are implemented using Spring Boot's testing support and RestTemplate.

## Postman Collection
A Postman collection is available for testing the API endpoints. It includes requests for all CRUD operations, with tests that mirror the CashcardApplicationTests.

## Building and Running

### Prerequisites
- Java 17 or later
- Gradle 8.6 

### VS Code Setup
1. Install the Java Extension Pack in VS Code
2. Open the project folder in VS Code
3. VS Code will automatically detect the Gradle project and download dependencies

### Building
To build the project, run:
```
./gradlew build
```
 
This will:
- Compile the source code
- Run all tests
- Create an executable JAR in `build/libs/cashcard-0.0.1-SNAPSHOT.jar`

### Running Tests
Run all tests:   
```
./gradlew test
```

Run specific test class:
```
./gradlew test --tests dev.ionelivi.cashcard.CashcardApplicationTests
```


View test results:
- HTML report: `build/reports/tests/test/index.html`
- XML report: `build/test-results/test/`

### Running the Application
1. Using Gradle:
```
./gradlew bootRun
```
2. Using the JAR directly:
```
java -jar build/libs/cashcard-0.0.1-SNAPSHOT.jar
```
The service will start on `http://localhost:8080`

### Available Profiles
- `dev` (default): Uses H2 in-memory database
- `prod`: For production deployment

To run with a specific profile:
```
./gradlew bootRun --args='--spring.profiles.active=prod'
```

### Test Users
The application comes with pre-configured users:
- `sarah1` / `abc123` (CARD-OWNER role)
- `kumar2` / `xyz789` (CARD-OWNER role)
- `hank-owns-no-cards` / `qrs456` (NON-OWNER role)

### Using IDE Gradle Extensions

#### VS Code
With the Gradle for Java extension installed:
1. Open the Gradle sidebar (View -> Command Palette -> 'Gradle: Focus on Gradle Tasks View')
2. Navigate through the task tree:
   - `cashcard` (root project)
     - `application` (for running the app)
     - `build` (for building tasks)
     - `verification` (for testing tasks)

Common tasks:
- `application` -> `bootRun`: Run the application
- `verification` -> `test`: Run all tests
- `build` -> `build`: Full project build
- `build` -> `clean`: Clean build directories

#### IntelliJ IDEA
With the built-in Gradle support:
1. Open the Gradle toolbar (View -> Tool Windows -> Gradle)
2. Expand the `cashcard` project
3. Double-click tasks or right-click for more options

Quick access:
- Click the Gradle refresh icon to reload the project
- Use the 'Execute Gradle Task' button (elephant icon) in the toolbar
- Right-click on `build.gradle` for context-specific tasks

#### Eclipse
With Buildship Gradle Integration:
1. Open the Gradle Tasks view (Window -> Show View -> Other -> Gradle -> Gradle Tasks)
2. Double-click tasks to execute them
3. Use the Gradle Executions view to monitor running tasks

Tips:
- Use the refresh button to update Gradle project configuration
- Configure run configurations for frequently used tasks
- View console output in the Gradle Console view

### Debugging Gradle Tasks
When using IDE extensions, you can:
- Set breakpoints in build scripts
- Debug test execution
- View detailed task execution logs
- Monitor dependency resolution


