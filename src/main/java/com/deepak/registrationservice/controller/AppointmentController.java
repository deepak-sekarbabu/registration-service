package com.deepak.registrationservice.controller;

import com.deepak.registrationservice.exception.ErrorDetails;
import com.deepak.registrationservice.model.appointment.AppointmentDetails;
import com.deepak.registrationservice.repository.AppointmentRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/v1")
@Tag(name = "Appointment", description = "Handles CRUD operations for Appointment registration")
@Validated
public class AppointmentController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AppointmentController.class);
    private final AppointmentRepository appointmentRepository;

    public AppointmentController(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }


    @GetMapping("/appointments")
    @Operation(summary = "Retrieve all appointments", description = "Retrieve all appointments")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "appointments Retrieved", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AppointmentDetails.class))), @ApiResponse(responseCode = "404", description = "appointment does not exist", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))),})
    public Flux<AppointmentDetails> getAllAppointments(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return appointmentRepository.findAllBy(PageRequest.of(page, size)).doOnError(error -> LOGGER.error("Error retrieving appointments: {}", error.getMessage()));

    }

    @GetMapping("/appointment/{id}")
    @Operation(summary = "Retrieve appointment by id", description = "Retrieve appointment by id")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "appointments Retrieved", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AppointmentDetails.class))), @ApiResponse(responseCode = "404", description = "appointment does not exist", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))),})
    public Mono<AppointmentDetails> getAppointment(@PathVariable("id") Integer id) {
        return appointmentRepository.findById(id).doOnError(error -> LOGGER.error("Error retrieving appointments: {}", error.getMessage()));
    }

    @GetMapping("/appointment/user/{id}")
    @Operation(summary = "Retrieve appointment by id", description = "Retrieve appointment by user id")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "appointments Retrieved", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AppointmentDetails.class))), @ApiResponse(responseCode = "404", description = "appointment does not exist", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))),})
    public Flux<AppointmentDetails> getAppointmentByUserId(@PathVariable("id") Integer id) {
        return appointmentRepository.findAllByUserId(id).doOnError(error -> LOGGER.error("Error retrieving appointments: {}", error.getMessage()));
    }

    //TODO Fix this
    @GetMapping("/appointments/between/{fromDate}/{toDate}")
    @Operation(summary = "Retrieve all appointments between a date range", description = "Retrieve all appointments between a specified date range")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Appointments retrieved", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AppointmentDetails.class))), @ApiResponse(responseCode = "404", description = "No appointments found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class)))})
    public Flux<AppointmentDetails> getAppointmentsBetweenDates(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate, @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate) {
        return appointmentRepository.findAllByAppointmentDateBetween(fromDate, toDate).doOnError(error -> LOGGER.error("Error retrieving appointments: {}", error.getMessage()));
    }

    @PostMapping("/appointments")
    @Operation(summary = "Create an appointment or multiple appointment", description = "Create an appointment")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Appointments retrieved", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AppointmentDetails.class))), @ApiResponse(responseCode = "500", description = "Problem creating appointment", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class)))})
    public Mono<List<AppointmentDetails>> createAppointments(@RequestBody List<AppointmentDetails> appointmentDetailsList) {
        LOGGER.info("Received request to create appointments: {}", appointmentDetailsList);
        return Flux.fromIterable(appointmentDetailsList).flatMap(appointmentRepository::save).collectList().doOnError(error -> LOGGER.error("Error creating appointments: {}", error.getMessage()));
    }

    @PutMapping("/appointments/{id}")
    @Operation(summary = "Update an appointment by ID", description = "Update an appointment by its ID")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Appointment updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AppointmentDetails.class))), @ApiResponse(responseCode = "404", description = "Appointment not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))), @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class)))})
    public Mono<ResponseEntity<AppointmentDetails>> updateAppointmentById(@PathVariable("id") Integer id, @RequestBody AppointmentDetails updatedAppointmentDetails) {
        LOGGER.info("Updating appointment with id: {} : {}", id, updatedAppointmentDetails);
        return appointmentRepository.findById(id).flatMap(existingAppointment -> {
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
                    existingAppointment.setDoctorName(updatedAppointmentDetails.getDoctorName());
                    existingAppointment.setClinicId(updatedAppointmentDetails.getClinicId());
                    existingAppointment.setActive(updatedAppointmentDetails.isActive());
                    // Save the updated appointment
                    return appointmentRepository.save(existingAppointment);
                }).map(ResponseEntity::ok) // Map the updated appointment to ResponseEntity
                .defaultIfEmpty(ResponseEntity.notFound().build()) // Handle case where appointment with given ID is not found
                .doOnError(error -> LOGGER.error("Error updating appointment with ID {}: {}", id, error.getMessage()));
    }

    @DeleteMapping("/appointment/{id}")
    @Operation(summary = "Delete appointment by id", description = "Delete appointment by id")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "appointments Deleted", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AppointmentDetails.class))), @ApiResponse(responseCode = "404", description = "appointment does not exist", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))),})
    public Mono<Void> deleteAppointment(@PathVariable("id") Integer id) {
        LOGGER.info("Received request to delete appointment with id: {}", id);
        return appointmentRepository.deleteById(id).doOnError(error -> LOGGER.error("Error deleting appointment: {}", error.getMessage()));
    }

}
