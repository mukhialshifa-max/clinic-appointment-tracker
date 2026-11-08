package com.alshifa.clinic.common;

/** Thrown when a request breaks a business rule (e.g. double-booking). Mapped to HTTP 409. */
public class BusinessRuleException extends RuntimeException {
    public BusinessRuleException(String message) {
        super(message);
    }
}
