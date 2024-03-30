package com.deepak.registrationservice.service;

import com.deepak.registrationservice.exception.DuplicateEntryException;
import com.deepak.registrationservice.exception.SlotIdNotAvailableException;
import com.deepak.registrationservice.model.appointment.AppointmentDetails;
import com.deepak.registrationservice.model.appointment.QueueManagement;
import com.deepak.registrationservice.model.appointment.SlotInformation;
import com.deepak.registrationservice.repository.AppointmentRepository;
import com.deepak.registrationservice.repository.QueueManagementRepository;
import com.deepak.registrationservice.repository.SlotInformationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Service
public class AppointmentServiceImpl implements AppointmentService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AppointmentServiceImpl.class);
    private final AppointmentRepository appointmentRepository;
    private final QueueManagementRepository queueManagementRepository;
    private final SlotInformationRepository slotInformationRepository;

    public AppointmentServiceImpl(AppointmentRepository appointmentRepository, QueueManagementRepository queueManagementRepository, SlotInformationRepository slotInformationRepository) {
        this.appointmentRepository = appointmentRepository;
        this.queueManagementRepository = queueManagementRepository;
        this.slotInformationRepository = slotInformationRepository;
    }

    @Override
    public Mono<List<AppointmentDetails>> savedAppointment(List<AppointmentDetails> appointmentDetailsList) throws DuplicateEntryException, SlotIdNotAvailableException {
        List<Mono<AppointmentDetails>> savedAppointments = new ArrayList<>();

        for (AppointmentDetails appointmentDetails : appointmentDetailsList) {
            Mono<SlotInformation> slotInfoMono = slotInformationRepository.findById(appointmentDetails.getSlotId());
            savedAppointments.add(slotInfoMono.flatMap(slotInformation -> {
                if (slotInformation != null && slotInformation.getIsAvailable()) {
                    return this.appointmentRepository.save(Objects.requireNonNull(appointmentDetails)).flatMap(savedAppointment -> {
                        QueueManagement queueManagement = new QueueManagement();
                        queueManagement.setAppointmentId(savedAppointment.getAppointmentId());
                        queueManagement.setSlotId(savedAppointment.getSlotId());
                        queueManagement.setClinicId(savedAppointment.getClinicId());
                        queueManagement.setDoctorId(savedAppointment.getDoctorId());
                        queueManagement.setInitialQueueNo(slotInformation.getSlotNo());
                        queueManagement.setCurrentQueueNo(slotInformation.getSlotNo());
                        queueManagement.setCancelled(false);
                        queueManagement.setAdvancePaid(false);
                        queueManagement.setAdvanceRevertIfPaid(false);
                        queueManagement.setPatientReached(false);
                        queueManagement.setVisitStatus(null);
                        queueManagement.setConsultationFeePaid(false);
                        queueManagement.setConsultationFeeAmount(null);
                        queueManagement.setTransactionIdAdvanceFee(null);
                        queueManagement.setTransactionIdConsultationFee(null);
                        queueManagement.setTransactionIdAdvanceRevert(null);
                        queueManagement.setDate(Date.valueOf(LocalDate.now()));
                        slotInformation.setIsAvailable(false);
                        return this.slotInformationRepository.save(slotInformation).then(this.queueManagementRepository.save(queueManagement).thenReturn(savedAppointment));
                    });
                } else {
                    if (slotInformation != null && slotInformation.getIsAvailable() == false || slotInformation == null) {
                        return Mono.error(new SlotIdNotAvailableException("Slot not available"));
                    } else {
                        return Mono.error(new DuplicateEntryException("Duplicate appointment found"));
                    }
                }
            }).switchIfEmpty(Mono.error(new SlotIdNotAvailableException("Slot not available"))).onErrorMap(DataIntegrityViolationException.class, ex -> new DuplicateEntryException("Duplicate appointment found")));
        }

        return Flux.concat(savedAppointments).collectList().onErrorResume(error -> {
            LOGGER.error("Error creating appointments: {}", error.getMessage());
            return Mono.error(error);
        });
    }



/*    @Override
    public Mono<List<AppointmentDetails>> savedAppointment(List<AppointmentDetails> appointmentDetailsList) throws DuplicateEntryException {
        List<Mono<AppointmentDetails>> savedAppointments = new ArrayList<>();

        for (AppointmentDetails appointmentDetails : appointmentDetailsList) {
            if (slotInformationRepository.findById(appointmentDetails.getSlotId()) != null) {
                savedAppointments.add(this.appointmentRepository.save(Objects.requireNonNull(appointmentDetails)).flatMap(savedAppointment -> {
                    QueueManagement queueManagement = new QueueManagement();
                    queueManagement.setAppointmentId(savedAppointment.getAppointmentId());
                    queueManagement.setSlotId(savedAppointment.getSlotId());
                    queueManagement.setClinicId(savedAppointment.getClinicId());
                    queueManagement.setDoctorId(savedAppointment.getDoctorId());

                    if (savedAppointment.getSlotId() != null) {
                        return this.slotInformationRepository.findById(savedAppointment.getSlotId().intValue()).flatMap(slotInformation -> {
                            if (slotInformation != null && slotInformation.getIsAvailable()) {
                                queueManagement.setInitialQueueNo(slotInformation.getSlotNo());
                                queueManagement.setCurrentQueueNo(slotInformation.getSlotNo());
                                queueManagement.setCancelled(false);
                                queueManagement.setAdvancePaid(false);
                                queueManagement.setAdvanceRevertIfPaid(false);
                                queueManagement.setPatientReached(false);
                                queueManagement.setVisitStatus(null);
                                queueManagement.setConsultationFeePaid(false);
                                queueManagement.setConsultationFeeAmount(null);
                                queueManagement.setTransactionIdAdvanceFee(null);
                                queueManagement.setTransactionIdConsultationFee(null);
                                queueManagement.setTransactionIdAdvanceRevert(null);
                                queueManagement.setDate(Date.valueOf(LocalDate.now()));
                                slotInformation.setIsAvailable(false);
                                return this.slotInformationRepository.save(slotInformation).then(this.queueManagementRepository.save(queueManagement).thenReturn(savedAppointment));
                            } else {
                                appointmentDetails.setSlotId(null);
                                return appointmentRepository.save(appointmentDetails).then(Mono.error(new DuplicateEntryException("Slot already booked")));
                            }
                        });
                    } else {
                        LOGGER.info("Join the Queue for the appointment Id : {}", savedAppointment.getAppointmentId());
                        queueManagement.setInitialQueueNo(null);
                        queueManagement.setCurrentQueueNo(null);
                        queueManagement.setCancelled(false);
                        queueManagement.setAdvancePaid(false);
                        queueManagement.setAdvanceRevertIfPaid(false);
                        queueManagement.setPatientReached(false);
                        queueManagement.setVisitStatus(null);
                        queueManagement.setConsultationFeePaid(false);
                        queueManagement.setConsultationFeeAmount(null);
                        queueManagement.setTransactionIdAdvanceFee(null);
                        queueManagement.setTransactionIdConsultationFee(null);
                        queueManagement.setTransactionIdAdvanceRevert(null);
                        queueManagement.setDate(Date.valueOf(LocalDate.now()));
                        return this.queueManagementRepository.save(queueManagement).thenReturn(savedAppointment);
                    }
                }).onErrorMap(DataIntegrityViolationException.class, ex -> new DuplicateEntryException("Duplicate appointment found")));
            }

        }

        return Flux.concat(savedAppointments).collectList().onErrorResume(error -> {
            LOGGER.error("Error creating appointments: {}", error.getMessage());
            return Mono.error(error);
        });

    }*/

    @Override
    public Mono<ResponseEntity<AppointmentDetails>> updateAppointment(Integer id, AppointmentDetails updatedAppointmentDetails) {
        return this.appointmentRepository.findById(id).flatMap(existingAppointment -> {
                    // Update fields of existing appointment with new details
                    existingAppointment.setAppointmentDate(updatedAppointmentDetails.getAppointmentDate());
                    existingAppointment.setUserId(updatedAppointmentDetails.getUserId());
                    existingAppointment.setAppointmentType(updatedAppointmentDetails.getAppointmentType());
                    existingAppointment.setAppointmentFor(updatedAppointmentDetails.getAppointmentFor());
                    existingAppointment.setAppointmentForName(updatedAppointmentDetails.getAppointmentForName());
                    existingAppointment.setAppointmentForAge(updatedAppointmentDetails.getAppointmentForAge());
                    existingAppointment.setSymptom(updatedAppointmentDetails.getSymptom());
                    existingAppointment.setOtherSymptoms(updatedAppointmentDetails.getOtherSymptoms());
                    existingAppointment.setAppointmentDate(updatedAppointmentDetails.getAppointmentDate());
                    existingAppointment.setDoctorId(updatedAppointmentDetails.getDoctorId());
                    existingAppointment.setClinicId(updatedAppointmentDetails.getClinicId());
                    existingAppointment.setActive(updatedAppointmentDetails.isActive());
                    // Save the updated appointment
                    return this.appointmentRepository.save(existingAppointment);
                }).map(ResponseEntity::ok) // Map the updated appointment to ResponseEntity
                .defaultIfEmpty(ResponseEntity.notFound().build()) // Handle case where appointment with given ID is not found
                .doOnError(error -> LOGGER.error("Error updating appointment with ID {}: {}", id, error.getMessage()));
    }

}
