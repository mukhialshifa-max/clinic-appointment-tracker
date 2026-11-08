package com.alshifa.clinic.appointment;

import com.alshifa.clinic.common.NotFoundException;
import com.alshifa.clinic.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Web-layer tests with MockMvc: real controller, real security rules,
 * mocked service. Checks HTTP status codes, JSON and access control.
 */
@WebMvcTest(AppointmentController.class)
@Import(SecurityConfig.class)
class AppointmentControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private AppointmentService service;

    private final LocalDateTime nineAm = LocalDateTime.now().plusDays(1).withHour(9).withMinute(0).withSecond(0).withNano(0);

    @Test
    void anonymousUsersAreRejected() throws Exception {
        mvc.perform(get("/api/appointments")).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "STAFF")
    void listReturnsAppointments() throws Exception {
        when(service.findAll(null)).thenReturn(List.of(
                new AppointmentResponse(1L, "Sara Ahmed", "Dr. Patel", nineAm, "Check-up", AppointmentStatus.BOOKED)));

        mvc.perform(get("/api/appointments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].patientName").value("Sara Ahmed"))
                .andExpect(jsonPath("$[0].status").value("BOOKED"));
    }

    @Test
    @WithMockUser(roles = "STAFF")
    void createReturns201() throws Exception {
        when(service.create(any())).thenReturn(
                new AppointmentResponse(5L, "Sara Ahmed", "Dr. Patel", nineAm, null, AppointmentStatus.BOOKED));

        String body = """
                {"patientName":"Sara Ahmed","providerName":"Dr. Patel","startTime":"%s"}
                """.formatted(nineAm);

        mvc.perform(post("/api/appointments").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(5));
    }

    @Test
    @WithMockUser(roles = "STAFF")
    void createWithMissingFieldsReturns400() throws Exception {
        mvc.perform(post("/api/appointments").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.patientName").exists());
    }

    @Test
    @WithMockUser(roles = "STAFF")
    void unknownIdReturns404() throws Exception {
        when(service.findById(99L)).thenThrow(new NotFoundException("Appointment 99 not found"));

        mvc.perform(get("/api/appointments/99")).andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "STAFF")
    void staffCannotDelete() throws Exception {
        mvc.perform(delete("/api/appointments/1")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"STAFF", "ADMIN"})
    void adminCanDelete() throws Exception {
        mvc.perform(delete("/api/appointments/1")).andExpect(status().isNoContent());
    }
}
