package com.alshifa.clinic.config;

import com.alshifa.clinic.appointment.Appointment;
import com.alshifa.clinic.appointment.AppointmentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.LocalDate;
import java.time.LocalTime;

/** Adds a few sample appointments when running locally (the "dev" profile). */
@Configuration
@Profile("dev")
public class DemoDataLoader {

    @Bean
    CommandLineRunner loadDemoData(AppointmentRepository repository) {
        return args -> {
            if (repository.count() > 0) {
                return;
            }
            LocalDate tomorrow = LocalDate.now().plusDays(1);
            repository.save(new Appointment("Sara Ahmed", "Dr. Patel",
                    tomorrow.atTime(LocalTime.of(9, 0)), "Annual check-up"));
            repository.save(new Appointment("John MacLeod", "Dr. Patel",
                    tomorrow.atTime(LocalTime.of(9, 30)), "Follow-up: blood work"));
            repository.save(new Appointment("Mei Chen", "Dr. Thompson",
                    tomorrow.atTime(LocalTime.of(10, 15)), "Flu shot"));
        };
    }
}
