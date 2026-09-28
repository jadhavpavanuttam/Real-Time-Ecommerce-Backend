# Real-Time E-Commerce Backend — Java 21 + Spring Boot 4.1.1 + Microservices + MongoDB

Production-style reference implementation matching the requested architecture: API Gateway, Auth, User, Product, Cart, Order, Inventory, Payment, Notification, MongoDB database-per-service, Redis, Kafka, JWT, Resilience4j, WebSocket, Actuator/Micrometer, Prometheus/Grafana, Docker and GitHub Actions.

Spring Boot 4.1.1 is the current stable line used here; Spring Cloud 2025.1.3 is the matching Oakwood release train for Boot 4.1.x. Verify the Spring release train before upgrades. 

## Open in IntelliJ
1. Extract the ZIP.
2. Open the root `pom.xml` as a Maven project.
3. Set Project SDK to Java 21.
4. Start infrastructure: `docker compose up -d mongo redis kafka`.
5. Build: `./mvnw clean verify` (or use IntelliJ Maven).
6. Start services with Docker after building jars: `docker compose up --build`.
7. Gateway: http://localhost:8080
8. Prometheus: http://localhost:9090
9. Grafana: http://localhost:3000

## Important production note
This is a deployable production-style foundation, not a claim that a payment provider, cloud networking, secrets, TLS certificates, DNS, WAF, SRE alerts, backup policy, or regulatory controls are already configured. Those depend on your chosen cloud/provider. The payment service intentionally uses a mock provider outcome so the architecture can run locally without real money movement.

## Service ports
8080 gateway, 8081 auth, 8082 user, 8083 product, 8084 cart, 8085 order, 8086 inventory, 8087 payment, 8088 notification.

## Architecture
API Gateway -> services -> service-owned MongoDB. Kafka handles order/inventory/payment events. Notification consumes events and pushes WebSocket updates. Redis is available to cart for distributed cache/rate-limit patterns.

## GitHub
`git init && git add . && git commit -m "Initial production-style e-commerce microservices" && git branch -M main && git remote add origin <YOUR_GITHUB_REPO> && git push -u origin main`

Never commit `.env`, real JWT secrets, provider API keys, Mongo credentials, Kafka credentials, or cloud credentials.
