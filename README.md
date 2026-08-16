# TalkFriendly API

Spring Boot 3.5 / Java 21 backend for the TalkFriendly frontend. It uses **Supabase PostgreSQL** as its managed database. The first increment provides a stateless JWT authentication API and is deliberately organised for future mood, journal, community, listener, and subscription modules.

## Run locally

1. Create a Supabase project and obtain the **transaction pooler** connection details from **Project Settings → Database**. Ensure its pooler IP range is permitted if you have network restrictions.
2. Copy `.env.example` to `.env`; fill the `SUPABASE_DB_*` values and set a unique Base64-encoded 256-bit `JWT_SECRET`. Export them to your shell or configure them in your IDE.
3. Install Java 21 and Maven 3.6.3+.
4. Run `mvn spring-boot:run`. Flyway automatically creates the application's `users` table on the Supabase database.

The API starts at `http://localhost:8080`. Health is available at `/actuator/health`.

## Authentication contract

| Method | Path | Purpose |
| --- | --- | --- |
| POST | `/api/v1/auth/register` | Create an account and receive a JWT |
| POST | `/api/v1/auth/login` | Sign in and receive a JWT |
| GET | `/api/v1/auth/me` | Return the authenticated user |

Send `Authorization: Bearer <accessToken>` to protected endpoints. Example registration body:

```json
{"email":"person@example.com","password":"a-secure-password","displayName":"Sam"}
```

Passwords are BCrypt-hashed and are never returned by the API. The JWT signing secret is mandatory outside the development default; set it before any deployed environment. This service owns its `users` table; it does not use Supabase Auth. If you later want Supabase Auth, we can replace the local registration/login flow with verification of Supabase-issued access tokens.
