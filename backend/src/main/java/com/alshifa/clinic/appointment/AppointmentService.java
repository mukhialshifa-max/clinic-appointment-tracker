package com.alshifa.clinic.appointment;

import com.alshifa.clinic.common.BusinessRuleException;
import com.alshifa.clinic.common.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Business logic lives here, between the controller (HTTP) and the
 * repository (database). This layering is the "service layer" pattern.
 */
@Service
@Transactional
public class AppointmentService {

    private final AppointmentRepository repository;

    // Constructor injection: Spring passes in the repository.
    public AppointmentService(AppointmentRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponse> findAll(AppointmentStatus status) {
        List<Appointment> appointments = (status == null)
                ? repository.findAllByOrderByStartTimeAsc()
                : repository.findByStatusOrderByStartTimeAsc(status);
        return appointments.stream().map(AppointmentResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public AppointmentResponse findById(Long id) {
        return AppointmentResponse.from(getOrThrow(id));
    }

    public AppointmentResponse create(AppointmentRequest request) {
        checkProviderIsFree(request);
        Appointment saved = repository.save(new Appointment(
                request.patientName().trim(),
                request.providerName().trim(),
                request.startTime(),
                request.reason()));
        return AppointmentResponse.from(saved);
    }

    public AppointmentResponse update(Long id, AppointmentRequest request) {
        Appointment appointment = getOrThrow(id);
        boolean slotChanged = !appointment.getStartTime().equals(request.startTime())
                || !appointment.getProviderName().equalsIgnoreCase(request.providerName().trim());
        if (slotChanged) {
            checkProviderIsFree(request);
        }
        appointment.setPatientName(request.patientName().trim());
        appointment.setProviderName(request.providerName().trim());
        appointment.setStartTime(request.startTime());
        appointment.setReason(request.reason());
        return AppointmentResponse.from(appointment);
    }

    public AppointmentResponse changeStatus(Long id, AppointmentStatus newStatus) {
        Appointment appointment = getOrThrow(id);
        if (appointment.getStatus() == AppointmentStatus.CANCELLED
                || appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new BusinessRuleException(
                    "Cannot change an appointment that is already " + appointment.getStatus());
        }
        appointment.setStatus(newStatus);
        return AppointmentResponse.from(appointment);
    }

    public void delete(Long id) {
        repository.delete(getOrThrow(id));
    }

    private Appointment getOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Appointment " + id + " not found"));
    }

    private void checkProviderIsFree(AppointmentRequest request) {
        boolean taken = repository.existsByProviderNameIgnoreCaseAndStartTimeAndStatusNot(
                request.providerName().trim(), request.startTime(), AppointmentStatus.CANCELLED);
        if (taken) {
            throw new BusinessRuleException(
                    request.providerName() + " already has an appointment at " + request.startTime());
        }
    }
}
