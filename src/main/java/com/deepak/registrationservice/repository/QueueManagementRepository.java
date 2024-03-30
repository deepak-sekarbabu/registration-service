package com.deepak.registrationservice.repository;

import com.deepak.registrationservice.model.appointment.QueueManagement;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface QueueManagementRepository extends R2dbcRepository<QueueManagement, Integer> {

    Mono<QueueManagement> findByAppointmentId(Integer appointmentId);

    Mono<Void> deleteByAppointmentId(Integer appointmentId);
}
