package com.alshifa.clinic.appointment;

import com.alshifa.clinic.common.BusinessRuleException;
import com.alshifa.clinic.common.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Unit tests: the repository is a Mockito mock, so no database is needed. */
@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository repository;

    @InjectMocks
    private AppointmentService service;

    private final LocalDateTime nineAm = LocalDateTime.now().plusDays(1).withHour(9).withMinute(0).withSecond(0).withNano(0);

    @Test
    void createSavesAppointmentWhenProviderIsFree() {
        AppointmentRequest request = new AppointmentRequest("Sara Ahmed", "Dr. Patel", nineAm, "Check-up");
        when(repository.existsByProviderNameIgnoreCaseAndStartTimeAndStatusNot(
                "Dr. Patel", nineAm, AppointmentStatus.CANCELLED)).thenReturn(false);
        when(repository.save(any(Appointment.class))).thenAnswer(inv -> {
            Appointment a = inv.getArgument(0);
            a.setId(1L);
            return a;
        });

        AppointmentResponse response = service.create(request);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.status()).isEqualTo(AppointmentStatus.BOOKED);
    }

    @Test
    void createRejectsDoubleBooking() {
        AppointmentRequest request = new AppointmentRequest("Sara Ahmed", "Dr. Patel", nineAm, null);
        when(repository.existsByProviderNameIgnoreCaseAndStartTimeAndStatusNot(
                "Dr. Patel", nineAm, AppointmentStatus.CANCELLED)).thenReturn(true);

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("already has an appointment");
        verify(repository, never()).save(any());
    }

    @Test
    void findByIdThrowsWhenMissing() {
        when(repository.findById(42L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(42L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void cannotChangeStatusOfCancelledAppointment() {
        Appointment cancelled = new Appointment("Sara Ahmed", "Dr. Patel", nineAm, null);
        cancelled.setStatus(AppointmentStatus.CANCELLED);
        when(repository.findById(eq(7L))).thenReturn(Optional.of(cancelled));

        assertThatThrownBy(() -> service.changeStatus(7L, AppointmentStatus.CHECKED_IN))
                .isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void checkInMovesBookedAppointmentForward() {
        Appointment booked = new Appointment("Sara Ahmed", "Dr. Patel", nineAm, null);
        when(repository.findById(3L)).thenReturn(Optional.of(booked));

        AppointmentResponse response = service.changeStatus(3L, AppointmentStatus.CHECKED_IN);

        assertThat(response.status()).isEqualTo(AppointmentStatus.CHECKED_IN);
    }
}
