# RESTful API Design Guidelines

---

## 1. URL Naming Conventions
- Use plural nouns for resources: `/api/v1/documents`, `/api/v1/users`.
- Use sub-resources for relations: `/api/v1/documents/{docId}/versions`.
- Use HTTP verbs properly:
  - `GET`: Idempotent read.
  - `POST`: Create resource or execute action.
  - `PUT`: Idempotent full replacement.
  - `PATCH`: Idempotent partial update.
  - `DELETE`: Remove resource.

## 2. Response Wrapping Standard
All API endpoints must return a unified envelope structure:

```json
{
  "status": "SUCCESS",
  "data": { ... },
  "error": null,
  "timestamp": "2026-07-27T20:30:00Z"
}
```

## 3. Error Code Hierarchy
| Error Code | HTTP Status | Meaning |
|---|---|---|
| `BAD_REQUEST` | 400 | Payload or header validation failure |
| `UNAUTHORIZED` | 401 | Missing or invalid auth token |
| `TOO_MANY_REQUESTS` | 429 | Rate limit exceeded |
| `INTERNAL_ERROR` | 500 | Unhandled server exception |
