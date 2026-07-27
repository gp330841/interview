# API Specification: [Endpoint / Resource Name]

**API ID**: SPEC-API-XXX  
**Protocol**: REST / OpenAPI 3.0  

---

## 1. Endpoint Summary

### `POST /api/v1/resource`
Creates a new domain resource with input validation and rate limiting.

#### Request Headers
| Header Name | Type | Required | Description |
|---|---|---|---|
| `Content-Type` | String | Yes | Must be `application/json` |
| `X-Client-ID` | String | Yes | Client ID for rate limiting |

#### Request Body
```json
{
  "name": "Sample Item",
  "category": "ENGINEERING",
  "tags": ["sdd", "java"]
}
```

#### Response (201 Created)
```json
{
  "status": "SUCCESS",
  "data": {
    "id": "res_982341",
    "name": "Sample Item",
    "createdAt": "2026-07-27T20:30:00Z"
  },
  "error": null
}
```

#### Error Response (400 Bad Request)
```json
{
  "status": "ERROR",
  "data": null,
  "error": {
    "code": "INVALID_PAYLOAD",
    "message": "Field 'name' cannot be blank."
  }
}
```
