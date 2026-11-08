package com.alshifa.clinic.appointment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Spring Data JPA writes the SQL for these methods from their names.
 * save, findById, findAll and deleteById come from JpaRepository.
 */
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findAllByOrderByStartTimeAsc();

    List<Appointment> findByStatusOrderByStartTimeAsc(AppointmentStatus status);

    /** Used to stop double-booking a provider at the same time. */
    boolean existsByProviderNameIgnoreCaseAndStartTimeAndStatusNot(
            String providerName, LocalDateTime startTime, AppointmentStatus status);
}
