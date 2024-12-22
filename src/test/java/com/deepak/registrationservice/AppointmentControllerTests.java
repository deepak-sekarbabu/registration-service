package com.deepak.registrationservice;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.deepak.registrationservice.controller.AppointmentController;
import com.deepak.registrationservice.model.appointment.AppointmentDetails;
import com.deepak.registrationservice.model.appointment.enums.AppointmentFor;
import com.deepak.registrationservice.model.appointment.enums.AppointmentType;
import com.deepak.registrationservice.model.appointment.enums.Symptom;
import com.deepak.registrationservice.repository.AppointmentRepository;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
public class AppointmentControllerTests {

  @Mock private AppointmentRepository appointmentRepository;

  @InjectMocks private AppointmentController appointmentController;

  @Test
  public void getAllAppointments_success() {
    AppointmentDetails appointment = new AppointmentDetails();
    appointment.setAppointmentId(1);
    appointment.setUserId(123);
    appointment.setAppointmentType(AppointmentType.GENERAL_CHECKUP);
    appointment.setAppointmentFor(AppointmentFor.SELF);
    appointment.setAppointmentForName("Deepak Sharma");
    appointment.setAppointmentForAge(25);
    appointment.setSymptom(Symptom.HEADACHE);
    appointment.setOtherSymptoms("Vomiting etc");
    appointment.setAppointmentDate(LocalDateTime.parse("2023-12-24T16:25:48"));
    appointment.setDoctorId("Dr Dinesh Child Specialist");
    appointment.setClinicId(456);
    appointment.setActive(true);

    when(appointmentRepository.findAllBy(any(Pageable.class))).thenReturn(Flux.just(appointment));

    // Call the controller method
    var result = appointmentController.getAllAppointments(0, 10).collectList().block();

    assertThat(result).isNotNull();
    assertThat(result).hasSize(1); // Ensure the size is correct
    assertThat(result.get(0)).isEqualTo(appointment); // Ensure the content is correct
  }

  @Test
  public void getAppointmentById_notFound() {
    when(this.appointmentRepository.findById(0)).thenReturn(Mono.empty());

    var result = this.appointmentController.getAppointment(0).block();

    assertThat(result).isNull();
  }
}
