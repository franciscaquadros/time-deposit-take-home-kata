# Time Deposit Refactoring Kata - Take home assignment
## XA bank time deposit

# Time Deposit Java Application

This is a Spring Boot application built with Maven. This guide explains how to run the application from the command line.

---

## 📦 Prerequisites

Before you begin, ensure you have the following installed:

- Java 17 (check with `java -version`)
- Maven 3.6+ (check with `mvn -version`)

---

## 🚀 Building and Running the Application

The simplest way to run the application is to use Maven. Navigate to the main folder of the project and run the following command:

```bash
mvn spring-boot:run
```

After that, you can request the following endpoints using curl or Postman:

## 🌐 API Endpoints

### 1. Get All Time Deposits
- **Endpoint**: `GET /api/time-deposits`
- **Description**: Returns a list of all time deposits.
- **Response Body**:
```json 
  [
  {
    "id": 1,
    "planType": "basic",
    "balance": 1000.0,
    "days": 45,
    "withdrawals": [
      {
        "id": 1,
        "amount": 100.0,
        "date": "2025-07-23"
      }
    ]
  },
  {
    "id": 2,
    "planType": "student",
    "balance": 1000.0,
    "days": 60,
    "withdrawals": [
      {
        "id": 2,
        "amount": 50.0,
        "date": "2025-07-23"
      }
    ]
  },
  {
    "id": 3,
    "planType": "premium",
    "balance": 1000.0,
    "days": 120,
    "withdrawals": [
      {
        "id": 3,
        "amount": 25.0,
        "date": "2025-07-23"
      }
    ]
  }
]
```
- **Response**: 200 OK

### 2. Update All Time Deposits
- **Endpoint**: `PUT /api/time-deposits/balances`
- **Description**: Update All Time Deposits with the correspondent interest calculator.
- **Response**: 200 OK (Empty Response)

## 📝 Considerations

- The application uses an in-memory database `(H2)` for simplicity. In a production environment, we should consider using a persistent database.
- The application is built with Spring Boot.
- Swagger could be used to better document the API, but due to time constraints, it was not implemented.
- The class `DataInitializer` was created just to add some `TimeDeposits` and `Withdrawals` to the database to allow testing the endpoints. This is due to the fact that no endpoint is exposed to insert this data.
- `TestContainers` were used to test the application with a postgres database. This is a good practice to ensure that the application works with a real database in a CI/CD pipeline. To call the endpoints, `TestRestTemplate`was used.
- Separate `strategy` classes were created to handle the interest calculation for each plan type. This allows for easy extension in the future if new plan types are added.
- The development of this application was done having the hexagonal architecture in mind:
  - The domain contains the business rules - like definition of our `TimeDeposit` and `Withdrawal` domain entities, the calculation of interest values and a port to the data layer (repository).
  - The application contains the use cases (like `UpdateBalanceUseCase` and `GetAllTimeDepositsUseCase`)
  - The adapter connects the application to the outside world (like the REST controller and the data layer). For persistence the Jpa framework was chosen and for web REST was the protocol chosen to expose the endpoints.