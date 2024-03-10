package com.deepak.registrationservice.repository;

import com.deepak.registrationservice.model.appointment.SlotInformation;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SlotInformationRepository extends R2dbcRepository<SlotInformation, Integer> {

}
