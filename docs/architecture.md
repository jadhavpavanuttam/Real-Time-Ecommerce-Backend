# Runtime architecture

Client -> API Gateway -> domain service. MongoDB is owned per service. Kafka carries business events. Redis is cache/rate-limit state. Order saga is choreography: ORDER_CREATED -> INVENTORY_RESERVED/FAILED -> PAYMENT_SUCCESS/FAILED. Payment failure is a compensation trigger for inventory release.

## Topics
- order.events
- inventory.events
- payment.events

## Idempotency
- REST order creation: Idempotency-Key + unique lookup
- Kafka consumers: processed_events collection
- Payment: unique payment idempotency key

## Production hardening still required before public internet
- Replace demo JWT secret with 256+ bit secret from a secret manager.
- Replace mock payment provider adapter with a real provider SDK/webhook verifier.
- Use TLS/mTLS between services and Kafka/Mongo/Redis where appropriate.
- Use managed HA MongoDB/Kafka/Redis and backups.
- Configure CORS to the exact frontend origins.
- Add WAF, centralized log retention, alerting, SLOs, disaster recovery and load tests.
- Use a real email/SMS provider and verified WebSocket authorization.

## Redis and resilience
Cart GET uses cache-aside with a 10-minute TTL; MongoDB remains the source of truth. The cart module also carries Resilience4j configuration and a RestClient hook for protected product-service calls; in production, validate product price/availability at checkout with timeout + retry + circuit breaker.
