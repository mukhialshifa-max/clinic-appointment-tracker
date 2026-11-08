package com.alshifa.clinic.appointment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * JPA entity: one row in the "appointments" table.
 * Hibernate creates the table from this class at start-up.
 */
@Entity
@Table(name = "appointments")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String patientName;

    @Column(nullable = false, length = 100)
    private String providerName;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(length = 255)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AppointmentStatus status = AppointmentStatus.BOOKED;

    protected Appointment() {
        // required by JPA
    }

    public Appointment(String patientName, String providerName, LocalDateTime startTime, String reason) {
        this.patientName = patientName;
        this.providerName = providerName;
        this.startTime = startTime;
        this.reason = reason;
    }

    public Long getId() { return id; }
    public String getPatientName() { return patientName; }
    public String getProviderName() { return providerName; }
    public LocalDateTime getStartTime() { return startTime; }
    public String getReason() { return reason; }
    public AppointmentStatus getStatus() { return status; }

    public void setId(Long id) { this.id = id; }
    public void setPatientName(String patientName) { this.patientName = patientName; }
    public void setProviderName(String providerName) { this.providerName = providerName; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public void setReason(String reason) { this.reason = reason; }
    public void setStatus(AppointmentStatus status) { this.status = status; }
}
