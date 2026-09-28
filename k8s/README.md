Kubernetes production template

The Docker Compose stack is the local/reference environment. For Kubernetes, create one Deployment + Service per application and inject secrets through Kubernetes Secrets or an external secret manager. MongoDB, Kafka, and Redis should normally be managed services or dedicated stateful platforms rather than the demo Compose images.

Required environment variables per service: MONGODB_URI, JWT_SECRET, KAFKA_BOOTSTRAP_SERVERS, REDIS_HOST where applicable.
