package com.deepak.registrationservice.service;

import com.deepak.registrationservice.exception.DuplicateEntryException;
import com.deepak.registrationservice.exception.SlotIdNotAvailableException;
import com.deepak.registrationservice.model.appointment.AppointmentDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public interface AppointmentService {

    Mono<List<AppointmentDetails>> savedAppointment(List<AppointmentDetails> appointmentDetailsList) throws DuplicateEntryException, SlotIdNotAvailableException;

    Mono<ResponseEntity<AppointmentDetails>> updateAppointment(Integer id, AppointmentDetails updatedAppointmentDetails);
}
