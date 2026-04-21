# Kong POC - 3 Microservices with API Gateway

Complete API Gateway POC with Kong, JWT authentication, rate limiting, CORS, and 3 Spring Boot microservices.

## Quick Start (5 Minutes)

### Terminal 1: Users Service (Port 8080)
```powershell
cd services\users
mvn clean install -DskipTests
mvn spring-boot:run
```

### Terminal 2: Products Service (Port 8081)
```powershell
cd services\products
mvn clean install -DskipTests
mvn spring-boot:run
```

### Terminal 3: Orders Service (Port 8082)
```powershell
cd services\orders
mvn clean install -DskipTests
mvn spring-boot:run
```

### Terminal 4: Kong Gateway (Port 8000)
```powershell
docker-compose up
```

---

## Testing

### 1. Get JWT Token
```powershell
$response = Invoke-RestMethod -Uri "http://localhost:8080/api/v1/auth/token" -Method GET
$token = $response.token
```

### 2. Test Public Endpoint (No Token)
```powershell
curl http://localhost:8000/health
# Result: 200 OK ✅
```

### 3. Test Protected Endpoint Without Token (Should Fail)
```powershell
curl http://localhost:8000/users
# Result: 401 Unauthorized ✅
```

### 4. Test Protected Endpoint With Token (Should Succeed)
```powershell
$headers = @{"Authorization" = "Bearer $token"}
curl -H "Authorization: Bearer $token" http://localhost:8000/users
# Result: 200 OK with user list ✅
```

### 5. Create User
```powershell
$body = @{name="Alice"; email="alice@example.com"} | ConvertTo-Json
curl -X POST -H "Authorization: Bearer $token" -H "Content-Type: application/json" `
  -d $body http://localhost:8000/users
# Result: 201 Created ✅
```

### 6. Test All Services
```powershell
curl -H "Authorization: Bearer $token" http://localhost:8000/products
curl -H "Authorization: Bearer $token" http://localhost:8000/orders
```

### 7. Test Rate Limiting (10 req/min)
```powershell
# Send 12 requests - 11th will be blocked
for ($i = 1; $i -le 12; $i++) {
  curl -H "Authorization: Bearer $token" http://localhost:8000/users
}
# Results: 10 success (200), 2 limited (429) ✅
```

---

## Architecture

```
Client Request
    ↓
Kong Gateway (Port 8000)
├─ JWT Authentication (validates token)
├─ Rate Limiting (10 req/min)
├─ CORS (enabled)
├─ Request/Response Transformation (adds headers)
    ↓
Routes to Services:
├─ /users → Users Service (8080)
├─ /products → Products Service (8081)
├─ /orders → Orders Service (8082)
└─ /health → Public (no auth)
```

---

## Services & Endpoints

### Users Service (Port 8080)
| Method | Endpoint | Auth | Status |
|--------|----------|------|--------|
| GET | /api/v1/users/ | ✅ | 200 |
| GET | /api/v1/users/{id} | ✅ | 200 |
| POST | /api/v1/users/ | ✅ | 201 |
| PUT | /api/v1/users/{id} | ✅ | 200 |
| DELETE | /api/v1/users/{id} | ✅ | 204 |
| GET | /api/v1/auth/token | ❌ | 200 |
| GET | /api/v1/auth/token/{hours} | ❌ | 200 |

### Products Service (Port 8081)
| Method | Endpoint | Auth |
|--------|----------|------|
| GET | /api/v1/products/ | ✅ |
| GET | /api/v1/products/{id} | ✅ |
| POST | /api/v1/products/ | ✅ |
| PUT | /api/v1/products/{id} | ✅ |
| DELETE | /api/v1/products/{id} | ✅ |

### Orders Service (Port 8082)
| Method | Endpoint | Auth |
|--------|----------|------|
| GET | /api/v1/orders/ | ✅ |
| GET | /api/v1/orders/{id} | ✅ |
| POST | /api/v1/orders/ | ✅ |
| PUT | /api/v1/orders/{id} | ✅ |
| DELETE | /api/v1/orders/{id} | ✅ |

---

## Kong Plugins Configured

1. **JWT Authentication** (HS256)
   - Secret: `my-secret-key`
   - Consumer: `poc-user`
   - Applies to: `/users`, `/products`, `/orders`

2. **Rate Limiting**
   - Limit: 10 requests/minute per consumer
   - Response: 429 Too Many Requests when exceeded

3. **Request/Response Transformation**
   - Adds: `X-Service-Name` header
   - Adds: `X-Request-ID` header
   - Adds: `X-Response-Time` header

4. **CORS**
   - Allows: All origins
   - Methods: GET, POST, PUT, DELETE, OPTIONS
   - Credentials: Enabled

5. **HTTP Logging**
   - Logs all requests to stdout

---

## JWT Token

### Generate Token (Option A - API)
```powershell
# 24-hour token
Invoke-RestMethod -Uri "http://localhost:8080/api/v1/auth/token" -Method GET

# Custom expiration (e.g., 48 hours)
Invoke-RestMethod -Uri "http://localhost:8080/api/v1/auth/token/48" -Method GET
```

### Generate Token (Option B - jwt.io)
1. Go to https://jwt.io
2. Set Secret: `my-secret-key`
3. Set Payload:
```json
{
  "iss": "kong",
  "sub": "poc-user",
  "aud": "kong",
  "iat": 1704153600,
  "exp": 2000000000
}
```
4. Copy encoded token

### Token Claims
```json
{
  "iss": "kong",        // Issuer
  "sub": "poc-user",    // Subject (consumer)
  "aud": "kong",        // Audience
  "iat": 1704153600,    // Issued at (seconds)
  "exp": 1735689600     // Expires (seconds)
}
```

---

## Plugin Scope Explanation

**Plugins configured at SERVICE level apply to ALL routes:**

When you define plugins in `config/kong.yml` at the service level, they protect all endpoints:

```
users-service (plugins apply to all routes below)
├─ GET /users ← JWT, rate-limit, transform ✅
├─ GET /users/{id} ← JWT, rate-limit, transform ✅
├─ POST /users ← JWT, rate-limit, transform ✅
├─ PUT /users/{id} ← JWT, rate-limit, transform ✅
└─ DELETE /users/{id} ← JWT, rate-limit, transform ✅
```

---

## URLs

| Component | URL | Purpose |
|-----------|-----|---------|
| Kong Proxy | http://localhost:8000 | API Gateway |
| Kong Admin | http://localhost:8001 | Manage services/routes |
| Kong Manager | http://localhost:8002 | Web UI |
| Users Service | http://localhost:8080 | Microservice |
| Products Service | http://localhost:8081 | Microservice |
| Orders Service | http://localhost:8082 | Microservice |

---

## Files

```
KongPOC/
├── config/kong.yml                              # Kong config (routes, plugins)
├── docker-compose.yaml                          # Kong container
├── services/
│   ├── users/
│   │   ├── src/main/java/com/example/users/
│   │   │   ├── controller/
│   │   │   │   ├── users.java                  # User CRUD
│   │   │   │   └── AuthController.java         # JWT token generation
│   │   │   ├── model/User.java
│   │   │   ├── service/JwtService.java         # Token creation logic
│   │   │   └── UsersApplication.java
│   │   └── pom.xml
│   ├── products/
│   │   ├── src/main/java/com/example/products/
│   │   │   ├── controller/ProductsController.java
│   │   │   ├── model/Product.java
│   │   │   └── ProductsApplication.java
│   │   └── pom.xml
│   └── orders/
│       ├── src/main/java/com/example/orders/
│       │   ├── controller/OrdersController.java
│       │   ├── model/Order.java
│       │   └── OrdersApplication.java
│       └── pom.xml
├── test_jwt.ps1                                 # PowerShell test script
└── README.md                                    # This file
```

---

## Technologies

- Kong API Gateway (Docker)
- Spring Boot 3.5.13 (Users Service)
- Spring Boot 3.3.5 (Products & Orders Services)
- Java 25/21
- Maven
- JJWT (JWT library)
- Docker & Docker Compose

---

## How Kong JWT Works

**5-Step Validation Process:**

1. Extract JWT from `Authorization: Bearer <token>` header
2. Decode JWT (base64 decode each part)
3. Verify signature using secret `my-secret-key` with HS256
4. Check expiration (`exp` claim must be in future)
5. Verify consumer exists (`sub` claim must match Kong consumer)

**Result:**
- ✅ All checks pass → Forward to service
- ❌ Any check fails → Return 401 Unauthorized

---

## Troubleshooting

### Services won't start
```powershell
# Verify Java/Maven installed
java -version
mvn -version

# Clean rebuild
mvn clean install -DskipTests
```

### Kong won't start
```powershell
# Check Docker running
docker ps

# Check port 8000 available
netstat -ano | findstr :8000
```

### Token works but Kong returns 401
- Verify Authorization header format: `Authorization: Bearer <token>`
- Token not expired: Generate new token with `/api/v1/auth/token`
- Check secret matches: `my-secret-key` in both JwtService.java and kong.yml

### Can't access services through Kong
```powershell
# Verify services running
curl http://localhost:8080/api/v1/auth/health
curl http://localhost:8081/api/v1/products/
curl http://localhost:8082/api/v1/orders/

# Check Kong can reach services
docker exec kong-gateway curl http://host.docker.internal:8080/api/v1/auth/health
```

---

## Next Steps

1. **Modify Plugins:** Update `config/kong.yml` to add/remove plugins
2. **Dynamic Config:** Use Kong Admin API to manage routes without restart
3. **Add Services:** Create new Spring Boot services and add to Kong routing
4. **Database:** Replace in-memory storage with actual database
5. **Production:** Deploy to Kubernetes, add monitoring/logging