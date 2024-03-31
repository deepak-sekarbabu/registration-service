package com.deepak.registrationservice.service;

import com.deepak.registrationservice.exception.AppointmentNotFoundException;
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
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class AppointmentServiceImpl implements AppointmentService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AppointmentServiceImpl.class);
    private final AppointmentRepository appointmentRepository;
    private final QueueManagementRepository queueManagementRepository;
    private final SlotInformationRepository slotInformationRepository;

    public AppointmentServiceImpl(AppointmentRepository appointmentRepository,
            QueueManagementRepository queueManagementRepository, SlotInformationRepository slotInformationRepository) {
        this.appointmentRepository = appointmentRepository;
        this.queueManagementRepository = queueManagementRepository;
        this.slotInformationRepository = slotInformationRepository;
    }

    @Override
    public Mono<List<AppointmentDetails>> savedAppointment(List<AppointmentDetails> appointmentDetailsList)
            throws DuplicateEntryException, SlotIdNotAvailableException {
        List<Mono<AppointmentDetails>> savedAppointments = new ArrayList<>();

        for (AppointmentDetails appointmentDetails : appointmentDetailsList) {
            Mono<SlotInformation> slotInfoMono = this.slotInformationRepository
                    .findById(appointmentDetails.getSlotId());
            savedAppointments.add(slotInfoMono.flatMap(slotInformation -> {
                if (slotInformation != null && slotInformation.getIsAvailable()) {
                    return this.appointmentRepository.save(Objects.requireNonNull(appointmentDetails))
                            .flatMap(savedAppointment -> {
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
                                return this.slotInformationRepository.save(slotInformation)
                                        .then(this.queueManagementRepository.save(queueManagement)
                                                .thenReturn(savedAppointment));
                            });
                } else {
                    if (slotInformation != null && slotInformation.getIsAvailable() == false
                            || slotInformation == null) {
                        return Mono.error(new SlotIdNotAvailableException("Slot not available"));
                    } else {
                        return Mono.error(new DuplicateEntryException("Duplicate appointment found"));
                    }
                }
            }).switchIfEmpty(Mono.error(new SlotIdNotAvailableException("Slot not available"))).onErrorMap(
                    DataIntegrityViolationException.class,
                    ex -> new DuplicateEntryException("Duplicate appointment found")));
        }

        return Flux.concat(savedAppointments).collectList().onErrorResume(error -> {
            LOGGER.error("Error creating appointments: {}", error.getMessage());
            return Mono.error(error);
        });
    }

    @Override
    public Mono<AppointmentDetails> updateAppointment(Integer id, AppointmentDetails updatedAppointmentDetails)
            throws AppointmentNotFoundException {
        return this.slotInformationRepository.findById(updatedAppointmentDetails.getSlotId())
                .flatMap(slotInformation -> {
                    if (slotInformation != null && slotInformation.getIsAvailable()) {
                        return this.appointmentRepository.findById(id).flatMap(existingAppointment -> {
                            if (existingAppointment == null) {
                                return Mono.error(new AppointmentNotFoundException("Appointment not found"));
                            }
                            existingAppointment.setAppointmentDate(updatedAppointmentDetails.getAppointmentDate());
                            existingAppointment.setUserId(updatedAppointmentDetails.getUserId());
                            existingAppointment.setAppointmentType(updatedAppointmentDetails.getAppointmentType());
                            existingAppointment.setAppointmentFor(updatedAppointmentDetails.getAppointmentFor());
                            existingAppointment
                                    .setAppointmentForName(updatedAppointmentDetails.getAppointmentForName());
                            existingAppointment.setAppointmentForAge(updatedAppointmentDetails.getAppointmentForAge());
                            existingAppointment.setSymptom(updatedAppointmentDetails.getSymptom());
                            existingAppointment.setOtherSymptoms(updatedAppointmentDetails.getOtherSymptoms());
                            existingAppointment.setDoctorId(updatedAppointmentDetails.getDoctorId());
                            existingAppointment.setClinicId(updatedAppointmentDetails.getClinicId());
                            existingAppointment.setActive(updatedAppointmentDetails.isActive());

                            // Atomic update of slot information, queue management, and appointment
                            return this.slotInformationRepository.findById(existingAppointment.getSlotId())
                                    .flatMap(slotInfo -> {
                                        slotInfo.setIsAvailable(true);
                                        return this.slotInformationRepository.save(slotInfo)
                                                .then(this.slotInformationRepository.findById(updatedAppointmentDetails.getSlotId())
                                                        .flatMap(updatedSlotInfo -> {
                                            updatedSlotInfo.setIsAvailable(false);
                                            existingAppointment.setSlotId(updatedAppointmentDetails.getSlotId());
                                            return this.slotInformationRepository.save(updatedSlotInfo)
                                                    .then(this.queueManagementRepository.findByAppointmentId(id).flatMap(queueManagement -> {
                                                queueManagement.setInitialQueueNo(updatedSlotInfo.getSlotNo());
                                                queueManagement.setCurrentQueueNo(updatedSlotInfo.getSlotNo());
                                                queueManagement.setSlotId(updatedAppointmentDetails.getSlotId());
                                                return this.queueManagementRepository.save(queueManagement);
                                            })).then(this.appointmentRepository.save(existingAppointment));
                                        }));
                                    });
                        });
                    } else {
                        return Mono.error(new SlotIdNotAvailableException("Slot not available"));
                    }
                });
    }

    @Override
    public Mono<Void> deleteAppointment(Integer id) {
        return this.appointmentRepository.findById(id).flatMap(appointmentDetails -> this.slotInformationRepository
                .findById(appointmentDetails.getSlotId()).flatMap(slotInformation -> {
                    slotInformation.setIsAvailable(true);
                    return this.slotInformationRepository.save(slotInformation);
                }).then(this.queueManagementRepository.deleteByAppointmentId(id))
                .then(this.appointmentRepository.deleteById(id)));

    }

    @Override
    public Mono<Void> cancelAppointment(Integer id) {
        return this.appointmentRepository.findById(id)
                .flatMap(appointmentDetails -> {
                    appointmentDetails.setActive(false);
                    return this.appointmentRepository.save(appointmentDetails)
                            .then(this.queueManagementRepository.deleteByAppointmentId(id))
                            .then(this.slotInformationRepository.findById(appointmentDetails.getSlotId())
                                    .flatMap(slotInformation -> {
                                        slotInformation.setIsAvailable(true);
                                        return this.slotInformationRepository.save(slotInformation);
                                    })
                            );
                })
                .then()
                .onErrorResume(error -> {
                    LOGGER.error("Error cancelling appointment: {}", error.getMessage());
                    return Mono.error(error);
                });
    }


}
