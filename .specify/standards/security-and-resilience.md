# Security & Resilience Engineering Guidelines

---

## 1. Resilience Patterns
- **Circuit Breaker**: Protect downstream dependencies using circuit breakers (e.g. Resilience4j / Sentinel) with fallback mechanisms.
- **Rate Limiting**: Protect endpoints using Token Bucket or Sliding Window Log algorithms. Return `429 Too Many Requests` when limits are exceeded.
- **Retries with Exponential Backoff & Jitter**: Never perform immediate tight-loop retries on network failures. Always apply jitter (`base * 2^attempt + randomJitter`).

## 2. Security Standards
- **Input Sanitization**: Strip HTML/XSS scripts and parameterize SQL queries to prevent injection attacks.
- **Authentication & Authorization**: Enforce JWT / OAuth2 bearer token verification on API gateways.
- **Data Protection**: Encrypt sensitive data at rest (AES-256) and in transit (TLS 1.3).
