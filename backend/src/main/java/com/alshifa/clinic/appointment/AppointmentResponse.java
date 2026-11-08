package com.alshifa.clinic.appointment;

import java.time.LocalDateTime;

/**
 * What the API sends back. Keeping it separate from the entity means
 * the database design can change without breaking clients.
 */
public record AppointmentResponse(
        Long id,
        String patientName,
        String providerName,
        LocalDateTime startTime,
        String reason,
        AppointmentStatus status
) {
    public static AppointmentResponse from(Appointment a) {
        return new AppointmentResponse(
                a.getId(), a.getPatientName(), a.getProviderName(),
                a.getStartTime(), a.getReason(), a.getStatus());
    }
}
