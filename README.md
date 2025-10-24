# Employee Management Microservice (Spring Boot + MySQL)

Features:
- Employee entity with columns:
  - EmployeeID (Primary Key)
  - EmployeeNo (Unique business key: MMddyyyy + 5-digit zero-padded employeeId + first2(firstName) + first2(lastName))
  - FirstName
  - LastName
  - DOB (Date of Birth)
  - Salary
  - DOJ (Date of Joining)

- CRUD REST APIs:
  - GET /api/employees
  - GET /api/employees/{id}
  - POST /api/employees
  - PUT /api/employees/{id}
  - DELETE /api/employees/{id}

Validation & business rules (implemented):
1. On creation, check if any existing employee has same firstName, lastName and DOB -> return 503 (Employee Already Exists).
2. Employee must be at least 18 years old on DOJ -> return 503 (Employee Underage).
3. EmployeeNo is generated server-side before insert.
4. If employee not found -> return 404.

How to run:
1. Configure MySQL datasource in src/main/resources/application.properties (username, password, url).
2. Build and run:
   mvn clean package
   java -jar target/employee-management-ms-0.0.1-SNAPSHOT.jar

Notes:
- No native SQL is used; Spring Data JPA repositories are used.
- Centralized exception handling implemented with @ControllerAdvice.
