# Core API examples

POST /api/auth/register
POST /api/auth/login
GET /api/products?page=0&size=20&category=electronics
POST /api/cart/items
GET /api/cart
POST /api/orders with Idempotency-Key header
GET /api/orders
GET /api/notifications
GET /api/inventory/{productId}
GET /api/payments/{id}
WebSocket endpoint: /ws, subscribe /topic/orders/{userId}
