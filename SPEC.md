You are acting as a senior Java architect and backend lead.

Your mission is to design and implement a production-grade Two-Factor Authentication (2FA) platform using Java and Spring Boot.

====================
HIGH-LEVEL GOAL
====================
Build a microservices-based 2FA system that:
- Generates one-time passwords (OTP)
- Sends OTPs asynchronously
- Validates OTPs
- Is event-driven
- Respects Hexagonal Architecture (Ports & Adapters)
- Is fully containerized with Docker

This project must be structured as a professional codebase, not a tutorial.

====================
ARCHITECTURE RULES (MANDATORY)
====================
1. Each microservice MUST follow Hexagonal Architecture:
    - domain (pure Java, no frameworks)
    - application (use cases)
    - ports (in/out interfaces)
    - adapters (REST, Kafka, Redis, PostgreSQL)

2. The domain layer:
    - MUST NOT depend on Spring, JPA, Kafka, Redis, or Docker
    - MUST contain only business entities and rules
    - MUST be fully unit-testable

3. The application layer:
    - Implements use cases
    - Depends only on domain and ports
    - Contains no framework-specific annotations

4. Adapters:
    - REST controllers go in adapters/in
    - Kafka producers/consumers go in adapters/out
    - Redis and PostgreSQL implementations go in adapters/out
    - No business logic is allowed in adapters

5. Spring Boot annotations are ONLY allowed in adapters and configuration classes.

====================
MICROSERVICES
====================
1. auth-service
    - Generates OTP codes
    - Stores OTPs in Redis with TTL
    - Validates OTPs
    - Publishes OTP events to Kafka

2. notification-service
    - Consumes OTP events from Kafka
    - Simulates sending OTP (log output)

====================
TECHNOLOGY STACK
====================
- Java 17+
- Spring Boot
- Kafka (KRaft mode, no ZooKeeper)
- Redis (OTP storage, TTL)
- PostgreSQL (audit and history)
- Docker & Docker Compose
- JUnit 5
- Mockito
- Testcontainers
- PIT (PiTest) for mutation testing

====================
FUNCTIONAL REQUIREMENTS
====================
1. OTP generation:
    - 6-digit numeric code
    - Time-based expiration (TTL via Redis)
    - One-time usage only

2. OTP validation:
    - Reject expired or invalid codes
    - Remove OTP after successful validation
    - Limit validation attempts

3. Event-driven communication:
    - Publish OtpGenerated event
    - Consume event in notification-service

====================
API CONTRACT (auth-service)
====================
POST /api/2fa/request
Request:
{
"userId": "string"
}

POST /api/2fa/validate
Request:
{
"userId": "string",
"code": "string"
}

====================
TESTING REQUIREMENTS (MANDATORY)
====================
1. Unit tests:
    - Domain layer: 100% coverage
    - Application layer: near 100%

2. Integration tests:
    - Redis via Testcontainers
    - Kafka via Testcontainers
    - PostgreSQL via Testcontainers

3. Mutation testing:
    - Configure PIT
    - Minimum mutation score: 80%
    - No excluded packages except adapters configuration

4. Tests must be fast, deterministic, and isolated.

====================
QUALITY RULES
====================
- No logic in controllers
- No field injection
- Constructor injection only
- No static utility classes
- Meaningful domain naming
- Clean package structure
- Clear separation of concerns

====================
DELIVERABLES
====================
- Complete source code for both microservices
- Dockerfiles for each service
- docker-compose.yml to run the entire system
- README with architecture explanation
- Test suite and PIT configuration

Proceed incrementally:
1. Start with auth-service domain model
2. Then application use cases
3. Then ports
4. Then adapters
5. Then tests
6. Then infrastructure (Docker, Kafka, Redis)

At each step, ensure the architecture rules are strictly respected.
