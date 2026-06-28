# Redis-Based Rate Limiter

A backend service implementing API rate limiting using Redis atomic counters — preventing abuse/overload by capping requests per user within a time window.

## Problem

Without rate limiting, a single user or bot can flood an API with requests, degrading service for everyone. This project demonstrates a production-style fix.

## Tech Stack

- Java 17, Spring Boot
- Redis (atomic counters, TTL-based windows)
- Docker

## How It Works

Uses Redis's atomic `INCR` operation to count requests per user within a fixed time window:

1. First request for a user → counter set to 1, with a 60-second expiry (TTL)
2. Each subsequent request increments the counter atomically (race-condition safe, even under concurrent load)
3. If count exceeds the limit (5 requests/60s) → returns `429 Too Many Requests`
4. After the window expires, Redis auto-deletes the key, resetting the count

This is the **Fixed Window Counter** algorithm — chosen for simplicity and atomicity guarantees via Redis.

## Proof

Sequential requests for the same user:
## API

**GET /api/resource?userId={userId}**

Response (within limit):
```json
"Request successful. Count: 3/5"
```

Response (limit exceeded):
```json
"Rate limit exceeded. Try again later."
```

## Run Locally

```bash
docker run --name rate-redis -p 6379:6379 -d redis
./mvnw spring-boot:run
```

## What This Demonstrates

- Atomic operations in distributed systems (Redis INCR)
- Rate limiting algorithms (Fixed Window)
- TTL-based key expiry for automatic state reset
- API abuse prevention, a real production concern at scale
