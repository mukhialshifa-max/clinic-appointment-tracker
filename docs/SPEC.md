# Specification — Clinic Appointment Tracker

This spec was written first and then used to drive the code, tests and
AI-assisted implementation (spec-driven development).

## Problem

Front-desk staff at a small clinic need one place to book, update and track
patient appointments, and to stop a provider from being double-booked.

## Users and roles

| Role  | Can do |
|-------|--------|
| STAFF | View, book, edit, check in, complete and cancel appointments |
| ADMIN | Everything STAFF can do, plus delete appointments |

## Business rules

1. Patient name, provider name and start time are required.
2. Start time must be in the future when booking or editing.
3. A provider cannot have two active (not cancelled) appointments at the same time.
4. Status flow: `BOOKED → CHECKED_IN → COMPLETED`. `BOOKED` or `CHECKED_IN` can go to `CANCELLED`.
5. A `COMPLETED` or `CANCELLED` appointment cannot change status again.

## API contract

Base path: `/api/appointments`. All calls need HTTP Basic auth.

| Method | Path | Body / params | Success | Errors |
|--------|------|---------------|---------|--------|
| GET    | `/` | `?status=BOOKED` (optional) | 200, list sorted by start time | 401 |
| GET    | `/{id}` | — | 200 | 404 |
| POST   | `/` | `AppointmentRequest` JSON | 201 | 400 invalid, 409 double-booked |
| PUT    | `/{id}` | `AppointmentRequest` JSON | 200 | 400, 404, 409 |
| PATCH  | `/{id}/status` | `?value=CHECKED_IN` | 200 | 404, 409 rule 5 |
| DELETE | `/{id}` | — | 204 | 403 not admin, 404 |

`AppointmentRequest`:

```json
{
  "patientName": "Sara Ahmed",
  "providerName": "Dr. Patel",
  "startTime": "2026-12-01T09:00",
  "reason": "Annual check-up"
}
```

Errors follow RFC 7807 problem details: `{ "status": 400, "detail": "...", "errors": { "field": "message" } }`.

## Acceptance tests

Each rule above has a matching JUnit test in `backend/src/test`.
