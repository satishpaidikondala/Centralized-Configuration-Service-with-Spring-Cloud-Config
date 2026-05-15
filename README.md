# Centralized Configuration Service with Spring Cloud Config

A centralized, Git-backed configuration server using Spring Cloud Config and a client microservice (`inventory-service`) that consumes configuration dynamically.

## Project Structure

```
├── config-repo/              # Local Git repository with configuration files
│   ├── inventory-service-dev.yml
│   └── inventory-service-prod.yml
├── config-server/            # Spring Cloud Config Server
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/main/java/com/configserver/
├── inventory-service/        # Client microservice
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/main/java/com/inventory/
├── docker-compose.yml        # Docker Compose orchestration
├── .env.example              # Environment variables documentation
└── README.md
```

## Prerequisites

- Docker & Docker Compose
- Java 17+ (for local development)
- Git

## Quick Start

1. **Build and run all services:**

   ```bash
   docker-compose up --build
   ```

   This starts both the `config-server` (port 8888) and `inventory-service` (port 8081 with `dev` profile).

2. **Verify the Config Server:**

   ```bash
   curl http://localhost:8888/inventory-service/dev
   ```

3. **Verify the Inventory Service:**

   ```bash
   curl http://localhost:8081/api/inventory/config
   ```

   Expected response:
   ```json
   {"profile": "dev", "maxStock": 100, "replenishThreshold": 10}
   ```

4. **Check custom health endpoint:**

   ```bash
   curl http://localhost:8081/api/inventory/health
   ```

   Expected response:
   ```json
   {"status": "UP", "configServer": "connected"}
   ```

## Dynamic Configuration Refresh

1. Modify `config-repo/inventory-service-dev.yml` (e.g., change `maxStock` to 250).
2. Commit the change:
   ```bash
   cd config-repo
   git add .
   git commit -m "Update maxStock"
   ```
3. Trigger refresh on the inventory-service:
   ```bash
   curl -X POST http://localhost:8081/actuator/refresh
   ```
4. Verify the updated config:
   ```bash
   curl http://localhost:8081/api/inventory/config
   ```

## Switching to Prod Profile

Update `docker-compose.yml`:
```yaml
environment:
  - SPRING_PROFILES_ACTIVE=prod
```
Change port mapping to `8082:8082`, then run `docker-compose up --build`.

## Environment Variables

See `.env.example` for all configurable environment variables.

## Endpoints

| Service           | Endpoint                          | Description                          |
|-------------------|-----------------------------------|--------------------------------------|
| Config Server     | `GET /inventory-service/dev`      | Dev configuration                    |
| Config Server     | `GET /inventory-service/prod`     | Prod configuration                   |
| Config Server     | `GET /actuator/health`            | Config Server health                 |
| Inventory Service | `GET /api/inventory/config`       | Active configuration values          |
| Inventory Service | `GET /api/inventory/health`       | Custom health with config server status |
| Inventory Service | `POST /actuator/refresh`          | Refresh configuration dynamically    |
