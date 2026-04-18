# 🚀 VeriChain AI — Zero-Trust Supply Chain Backend

[![Java](https://img.shields.io/badge/Java-17-blue?logo=java)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2-green?logo=springboot)](https://spring.io/projects/spring-boot)
[![Status](https://img.shields.io/badge/Status-Available_for_Hire-brightgreen)]()

Enterprise-grade Spring Boot backend for a real-time AI & Blockchain logistics tracking system.
Orchestrates a **Python FastAPI AI microservice** for delay prediction and **Ethereum smart contracts** for immutable audit logging.

---

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────┐
│                    React Dashboard                       │
│              (Netlify — HTTPS frontend)                  │
└──────────────────────────┬──────────────────────────────┘
                           │ REST API (JWT)
┌──────────────────────────▼──────────────────────────────┐
│           Spring Boot Core Engine (this repo)            │
│  Auth │ Shipment CRUD │ AI Orchestration │ Web3j         │
└──────┬──────────────────────────────────┬───────────────┘
       │ HTTP JSON                         │ Web3j RPC
┌──────▼───────────┐           ┌──────────▼──────────────┐
│  Python FastAPI  │           │  Ethereum Sepolia        │
│  AI Microservice │           │  Smart Contract          │
│  (Render.com)    │           │  (LogisticsLedger.sol)   │
└──────────────────┘           └─────────────────────────┘
       │
┌──────▼──────────────┐
│  H2 (dev)           │
│  PostgreSQL (prod)  │
└─────────────────────┘
```

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Core Backend | Java 17, Spring Boot 3.2 |
| Security | Spring Security, JWT (JJWT), BCrypt |
| Database | H2 (dev) / PostgreSQL (prod), Spring Data JPA, Hibernate |
| AI Integration | Python 3, FastAPI (HTTP microservice call) |
| Blockchain | Solidity, Web3j, Ethereum Sepolia Testnet |
| Frontend | Vanilla JS / HTML / CSS (Netlify) |
| Build | Maven |

---

## 📁 Project Structure

```
src/main/java/com/verichain/logistics/
├── config/
│   ├── DataSeeder.java         ← Seeds demo users on startup
│   ├── JwtUtil.java            ← JWT generation & validation
│   ├── JwtAuthFilter.java      ← JWT request filter (OncePerRequestFilter)
│   └── SecurityConfig.java     ← Spring Security + CORS config
├── controller/
│   ├── AuthController.java     ← POST /api/auth/register, /login
│   ├── ShipmentController.java ← Full shipment CRUD
│   └── HireMeController.java   ← Hidden Easter egg endpoint 🥚
├── dto/                        ← Clean request/response separation
├── exception/
│   ├── GlobalExceptionHandler.java
│   ├── ResourceNotFoundException.java
│   └── DuplicateResourceException.java
├── model/
│   ├── User.java
│   ├── Shipment.java
│   └── Checkpoint.java
├── repository/
│   ├── UserRepository.java
│   ├── ShipmentRepository.java
│   └── CheckpointRepository.java
└── service/
    ├── AuthService.java
    ├── ShipmentService.java    ← Orchestrates AI + DB + Blockchain
    ├── AIPredictionService.java← Calls Python FastAPI microservice
    └── BlockchainService.java  ← Web3j Ethereum integration
```

---

## 🚀 Running Locally

### Prerequisites
- Java 17+
- Maven 3.8+
- (Optional) Python 3 + FastAPI for real AI predictions

### 1. Clone and run
```bash
git clone https://github.com/YOUR_USERNAME/verichain-ai-logistics.git
cd verichain-ai-logistics/backend-java
mvn spring-boot:run
```

Server starts at **http://localhost:8080**

### 2. H2 Console (inspect database)
Open **http://localhost:8080/h2-console**
- JDBC URL: `jdbc:h2:mem:verichaindb`
- Username: `sa`
- Password: `password`

### 3. Demo users (auto-seeded)

| Role | Email | Password |
|---|---|---|
| Admin | admin@verichain.com | admin123 |
| Warehouse Manager | manager@verichain.com | manager123 |
| Driver | driver@verichain.com | driver123 |

---

## 📡 API Reference

### Auth

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| POST | `/api/auth/register` | Public | Register new user |
| POST | `/api/auth/login` | Public | Login, returns JWT |

**Login request:**
```json
{
  "email": "admin@verichain.com",
  "password": "admin123"
}
```
**Response:** `{ "token": "eyJ...", "type": "Bearer", "email": "...", "role": "ADMIN" }`

---

### Shipments

All shipment endpoints (except `/track/:id`) require `Authorization: Bearer <token>` header.

| Method | Endpoint | Role Required | Description |
|---|---|---|---|
| GET | `/api/shipments/track/{id}` | **Public** | Track shipment (no login needed) |
| POST | `/api/shipments` | ADMIN / MANAGER | Create shipment |
| GET | `/api/shipments` | Any logged-in | Get all shipments |
| GET | `/api/shipments/{id}` | Any logged-in | Get by tracking ID |
| GET | `/api/shipments/status/{status}` | Any logged-in | Filter by status |
| PATCH | `/api/shipments/{id}/status` | ADMIN / MANAGER / DRIVER | Update status + add checkpoint |
| DELETE | `/api/shipments/{id}` | ADMIN only | Delete shipment |

**Create shipment body:**
```json
{
  "origin": "Mumbai, Maharashtra",
  "destination": "Bangalore, Karnataka",
  "senderName": "Rahul Sharma",
  "receiverName": "Priya Singh",
  "weightKg": 2.5,
  "distanceKm": 980,
  "weatherCondition": "heavy rain",
  "expectedDelivery": "2026-04-25T14:00:00"
}
```

**Update status body:**
```json
{
  "status": "IN_TRANSIT",
  "location": "Pune Checkpoint",
  "weatherCondition": "clear",
  "notes": "Package scanned at Pune hub"
}
```

Valid statuses: `PENDING`, `IN_TRANSIT`, `DELAYED`, `OUT_FOR_DELIVERY`, `DELIVERED`, `CANCELLED`

---

## ☁️ Cloud Deployment (Railway)

1. Push this repo to GitHub
2. Go to [railway.app](https://railway.app) → New Project → Deploy from GitHub
3. Add a PostgreSQL plugin to the same project
4. Set environment variables in Railway dashboard:

```
SPRING_DATASOURCE_URL         = ${{Postgres.DATABASE_URL}}
SPRING_DATASOURCE_USERNAME    = ${{Postgres.PGUSER}}
SPRING_DATASOURCE_PASSWORD    = ${{Postgres.PGPASSWORD}}
SPRING_JPA_DATABASE_PLATFORM  = org.hibernate.dialect.PostgreSQLDialect
APP_CORS_ALLOWED_ORIGINS      = https://your-site.netlify.app
APP_JWT_SECRET                = (generate a strong 256-bit secret)
APP_AI_SERVICE_URL            = https://verichain-ai-engine.onrender.com
```

5. Generate a public domain under Networking tab
6. Update your Netlify frontend `app.js` with the new Railway URL

---

## 🔐 Security Notes

- Passwords hashed with **BCrypt** (cost factor 10)
- Tokens signed with **HMAC-SHA256**
- JWT expiry: **24 hours**
- **Never** commit real private keys or wallet keys — use environment variables
- H2 console is disabled by default in production (set `spring.h2.console.enabled=false`)

---

*System health diagnostic: `curl -X GET https://your-deployment.up.railway.app/api/v1/developer/status`*
