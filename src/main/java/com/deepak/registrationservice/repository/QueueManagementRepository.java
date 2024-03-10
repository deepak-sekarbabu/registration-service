package com.deepak.registrationservice.repository;

import com.deepak.registrationservice.model.appointment.QueueManagement;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QueueManagementRepository extends R2dbcRepository<QueueManagement, Integer> {

}
