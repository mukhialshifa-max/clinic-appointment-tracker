package com.alshifa.clinic.appointment;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * What the client sends to create or update an appointment.
 * The annotations are checked automatically because the controller uses @Valid.
 */
public record AppointmentRequest(
        @NotBlank(message = "Patient name is required")
        @Size(max = 100)
        String patientName,

        @NotBlank(message = "Provider name is required")
        @Size(max = 100)
        String providerName,

        @NotNull(message = "Start time is required")
        @Future(message = "Start time must be in the future")
        LocalDateTime startTime,

        @Size(max = 255)
        String reason
) {
}
