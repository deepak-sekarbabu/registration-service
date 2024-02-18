package com.deepak.registrationservice.repository;

import com.deepak.registrationservice.model.appointment.AppointmentDetails;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;

@Repository
public interface AppointmentRepository extends R2dbcRepository<AppointmentDetails, Integer> {
    Flux<AppointmentDetails> findAllBy(Pageable pageable);

    Flux<AppointmentDetails> findAllByUserId(Integer userId);

    Flux<AppointmentDetails> findAllByAppointmentDateBetween(LocalDateTime startDate, LocalDateTime endDate);
}
