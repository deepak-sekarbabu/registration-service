package com.deepak.registrationservice.repository;

import com.deepak.registrationservice.model.appointment.AppointmentDetails;
import java.time.LocalDateTime;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface AppointmentRepository extends R2dbcRepository<AppointmentDetails, Integer> {
  Flux<AppointmentDetails> findAllBy(Pageable pageable);

  Flux<AppointmentDetails> findAllByUserId(Integer userId);

  Flux<AppointmentDetails> findAllByDoctorId(String doctorId, Pageable pageable);

  Flux<AppointmentDetails> findAllByClinicId(Integer clinicId, Pageable pageable);

  Flux<AppointmentDetails> findAllByAppointmentDateBetween(
      LocalDateTime startDate, LocalDateTime endDate);

  Flux<AppointmentDetails> findAllByDoctorIdAndAppointmentDateBetween(
      String doctorId, LocalDateTime startDate, LocalDateTime endDate);

  Flux<AppointmentDetails> findAllByClinicIdAndAppointmentDateBetween(
      Integer clinicId, LocalDateTime startDate, LocalDateTime endDate);
}
