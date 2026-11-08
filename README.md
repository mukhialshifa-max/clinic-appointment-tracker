# Clinic Appointment Tracker

![CI](https://github.com/mukhialshifa-max/clinic-appointment-tracker/actions/workflows/ci.yml/badge.svg)

A full-stack web app for clinic front-desk staff to book, track and manage patient
appointments. Built with **Java 17, Spring Boot 3, Spring Security, Spring Data JPA,
React 18, SQL (H2 / MySQL) and Maven**, with JUnit 5 + Mockito tests and GitHub Actions CI.

The idea comes from my work supporting Oracle Health (Cerner) EHR go-lives at
Nova Scotia Health, where scheduling and check-in workflows are central.

## Features

- Book, edit, check in, complete and cancel appointments
- Stops a provider from being double-booked
- Filter the list by status
- Login with role-based access: `STAFF` can manage appointments, only `ADMIN` can delete
- Input validation with clear error messages
- REST API with consistent JSON error responses (RFC 7807)

## Architecture

```
React (Vite)  ──HTTP/JSON──▶  Spring Boot REST API  ──JPA──▶  H2 or MySQL
  components/                   AppointmentController   (web layer)
  api.js                        AppointmentService      (business rules)
                                AppointmentRepository   (data access)
                                SecurityConfig          (auth + roles)
                                GlobalExceptionHandler  (error responses)
```

Layered design: controller → service → repository, with request/response DTOs
(Java records) kept separate from the JPA entity.

## How it was built

I used **spec-driven development**: I wrote [`docs/SPEC.md`](docs/SPEC.md) first
(rules, roles, API contract), then implemented it with help from an AI code
assistant (Claude), and checked each rule with a unit or web-layer test.

## Run it locally

You need **Java 17+**, **Maven 3.9+** and **Node.js 20+**.

**1. Start the back end** (port 8080, sample data included):

```bash
cd backend
mvn spring-boot:run
```

**2. Start the front end** (port 5173) in a second terminal:

```bash
cd frontend
npm install
npm run dev
```

Open http://localhost:5173 and sign in with `staff` / `staff123` or `admin` / `admin123`.

**Run the tests:**

```bash
cd backend
mvn test
```

### Use MySQL instead of H2 (optional)

```sql
CREATE DATABASE clinic;
CREATE USER 'clinic_user'@'localhost' IDENTIFIED BY 'change_me';
GRANT ALL ON clinic.* TO 'clinic_user'@'localhost';
```

```bash
cd backend
mvn spring-boot:run -Dspring-boot.run.profiles=mysql
```

## API

| Method | Endpoint | What it does |
|--------|----------|--------------|
| GET    | `/api/appointments?status=BOOKED` | List appointments (filter optional) |
| GET    | `/api/appointments/{id}` | Get one appointment |
| POST   | `/api/appointments` | Book an appointment |
| PUT    | `/api/appointments/{id}` | Edit an appointment |
| PATCH  | `/api/appointments/{id}/status?value=CHECKED_IN` | Change status |
| DELETE | `/api/appointments/{id}` | Delete (admin only) |

Example:

```bash
curl -u staff:staff123 -H "Content-Type: application/json" \
  -d '{"patientName":"Sara Ahmed","providerName":"Dr. Patel","startTime":"2026-12-01T09:00"}' \
  http://localhost:8080/api/appointments
```

## Project structure

```
backend/
  src/main/java/com/alshifa/clinic/
    appointment/   entity, repository, service, controller, DTOs
    common/        exceptions + global error handler
    config/        security, demo data
  src/test/java/   JUnit 5 + Mockito + MockMvc tests
frontend/
  src/             React components and API client
docs/SPEC.md       specification
.github/workflows/ CI pipeline
```

## Next steps

- Store users in the database with JWT login
- Add Docker Compose for the app + MySQL
- Add React component tests
