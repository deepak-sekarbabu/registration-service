package com.deepak.registrationservice.controller;

import com.deepak.registrationservice.exception.AppointmentNotFoundException;
import com.deepak.registrationservice.exception.DuplicateEntryException;
import com.deepak.registrationservice.exception.ErrorDetails;
import com.deepak.registrationservice.exception.SlotIdNotAvailableException;
import com.deepak.registrationservice.model.appointment.AppointmentDetails;
import com.deepak.registrationservice.repository.AppointmentRepository;
import com.deepak.registrationservice.service.AppointmentServiceImpl;
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
import org.springframework.lang.NonNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/v1")
@Tag(name = "Appointment", description = "Handles CRUD operations for Appointment registration")
@Validated
public class AppointmentController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppointmentController.class);
    private final AppointmentRepository appointmentRepository;
    private final AppointmentServiceImpl appointmentService;

    public AppointmentController(AppointmentRepository appointmentRepository, AppointmentServiceImpl appointmentService) {
        this.appointmentRepository = appointmentRepository;
        this.appointmentService = appointmentService;
    }

    @GetMapping("/appointments")
    @Operation(summary = "Retrieve all appointments", description = "Retrieve all appointments")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "appointments Retrieved", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AppointmentDetails.class))), @ApiResponse(responseCode = "404", description = "appointment does not exist", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))),})
    public Flux<AppointmentDetails> getAllAppointments(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        LOGGER.info("Retrieving all appointments");
        return this.appointmentRepository.findAllBy(PageRequest.of(page, size)).doOnError(error -> LOGGER.error("Error retrieving appointments: {}", error.getMessage()));

    }

    @GetMapping("/appointment/{id}")
    @Operation(summary = "Retrieve appointment by id", description = "Retrieve appointment by id")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "appointments Retrieved", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AppointmentDetails.class))), @ApiResponse(responseCode = "404", description = "appointment does not exist", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))),})
    public Mono<AppointmentDetails> getAppointment(@PathVariable("id") Integer id) {
        LOGGER.info("Retrieving appointment by id {}", id);
        return this.appointmentRepository.findById(id).doOnError(error -> LOGGER.error("Error retrieving appointments: {}", error.getMessage()));
    }

    @GetMapping("/appointment/byuser/{id}")
    @Operation(summary = "Retrieve appointment by user id", description = "Retrieve appointment by user id")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "appointments Retrieved", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AppointmentDetails.class))), @ApiResponse(responseCode = "404", description = "appointment does not exist", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))),})
    public Flux<AppointmentDetails> getAppointmentByUserId(@PathVariable("id") @NonNull Integer id) {
        LOGGER.info("Retrieving appointment by user id {}", id);
        return this.appointmentRepository.findAllByUserId(id).doOnError(error -> LOGGER.error("Error retrieving appointments: {}", error.getMessage()));
    }

    @GetMapping("/appointment/bydoctor/{id}")
    @Operation(summary = "Retrieve all appointment by doctor id", description = "Retrieve all appointment by doctor id")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "appointments Retrieved", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AppointmentDetails.class))), @ApiResponse(responseCode = "404", description = "appointment does not exist", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))),})
    public Flux<AppointmentDetails> getAppointmentByDoctorId(@PathVariable("id") @NonNull String id, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        LOGGER.info("Retrieving appointment by doctor id {}", id);
        return this.appointmentRepository.findAllByDoctorId(id, PageRequest.of(page, size)).doOnError(error -> LOGGER.error("Error retrieving appointments: {}", error.getMessage()));
    }

    @GetMapping("/appointment/byclinic/{id}")
    @Operation(summary = "Retrieve all appointment by clinic id", description = "Retrieve all appointment by clinic id")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "appointments Retrieved", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AppointmentDetails.class))), @ApiResponse(responseCode = "404", description = "appointment does not exist", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))),})
    public Flux<AppointmentDetails> getAppointmentByClinicId(@PathVariable("id") Integer id, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        LOGGER.info("Retrieving appointment by clinic id {}", id);
        return this.appointmentRepository.findAllByClinicId(id, PageRequest.of(page, size)).doOnError(error -> LOGGER.error("Error retrieving appointments: {}", error.getMessage()));
    }


    @GetMapping("/appointments/between/{fromDate}/{toDate}")
    @Operation(summary = "Retrieve all appointments between a date range", description = "Retrieve all appointments between a specified date range")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Appointments retrieved", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AppointmentDetails.class))), @ApiResponse(responseCode = "404", description = "No appointments found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class)))})
    public Flux<AppointmentDetails> getAppointmentsBetweenDates(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate, @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        LOGGER.info("Retrieving appointments between dates {} and {}", fromDate, toDate);
        return this.appointmentRepository.findAllByAppointmentDateBetween(fromDate.atStartOfDay(), toDate.atTime(LocalTime.MAX)).doOnError(error -> LOGGER.error("Error retrieving appointments: {}", error.getMessage()));
    }

    @GetMapping("/appointments/bydoctorid/{doctorId}/between/{fromDate}/{toDate}")
    @Operation(summary = "Retrieve all appointments for a doctor between a date range", description = "Retrieve all appointments for a specified doctor between a specified date range")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Appointments retrieved", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AppointmentDetails.class))), @ApiResponse(responseCode = "404", description = "No appointments found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class)))})
    public Flux<AppointmentDetails> getAppointmentsByDoctorIdBetweenDates(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate, @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate, @PathVariable String doctorId) {
        LOGGER.info("Retrieving appointments for doctor {} between dates {} and {}", doctorId, fromDate, toDate);
        LocalDateTime startOfDay = fromDate.atStartOfDay();
        LocalDateTime endOfDay = toDate.atTime(LocalTime.MAX);
        return this.appointmentRepository.findAllByDoctorIdAndAppointmentDateBetween(doctorId, startOfDay, endOfDay).doOnError(error -> LOGGER.error("Error retrieving appointments: {}", error.getMessage()));
    }

    @GetMapping("/appointments/byclinicid/{clinicId}/between/{fromDate}/{toDate}")
    @Operation(summary = "Retrieve all appointments for a clinic between a date range", description = "Retrieve all appointments for a specified clinic between a specified date range")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Appointments retrieved", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AppointmentDetails.class))), @ApiResponse(responseCode = "404", description = "No appointments found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class)))})
    public Flux<AppointmentDetails> getAppointmentsByClinicIdBetweenDates(@PathVariable Integer clinicId, @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate, @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        LOGGER.info("Retrieving appointments for clinic {} between dates {} and {}", clinicId, fromDate, toDate);
        LocalDateTime startOfDay = fromDate.atStartOfDay();
        LocalDateTime endOfDay = toDate.atTime(LocalTime.MAX);
        return this.appointmentRepository.findAllByClinicIdAndAppointmentDateBetween(clinicId, startOfDay, endOfDay).doOnError(error -> LOGGER.error("Error retrieving appointments: {}", error.getMessage()));
    }

    @PostMapping("/appointments")
    @Operation(summary = "Create an appointment or multiple appointment", description = "Create an appointment")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Appointments retrieved", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AppointmentDetails.class))), @ApiResponse(responseCode = "500", description = "Problem creating appointment", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))),})
    public Mono<List<AppointmentDetails>> createAppointments(@RequestBody List<AppointmentDetails> appointmentDetailsList) throws DuplicateEntryException, SlotIdNotAvailableException {
        LOGGER.info("Received request to create appointments: {}", appointmentDetailsList);
        return this.appointmentService.savedAppointment(appointmentDetailsList);
    }


    @PutMapping("/appointments/{id}")
    @Operation(summary = "Update an appointment by ID", description = "Update an appointment by its ID")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Appointment updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AppointmentDetails.class))), @ApiResponse(responseCode = "404", description = "Appointment not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))), @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class)))})
    public Mono<AppointmentDetails> updateAppointmentById(@PathVariable("id") @NonNull Integer id, @RequestBody AppointmentDetails updatedAppointmentDetails) throws AppointmentNotFoundException {
        LOGGER.info("Updating appointment with id: {} : {}", id, updatedAppointmentDetails);
        return this.appointmentService.updateAppointment(id, updatedAppointmentDetails);
    }

    @DeleteMapping("/appointment/{id}")
    @Operation(summary = "Delete appointment by id", description = "Delete appointment by id")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "appointments Deleted", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AppointmentDetails.class))), @ApiResponse(responseCode = "404", description = "appointment does not exist", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))),})
    public Mono<Void> deleteAppointment(@PathVariable("id") @NonNull Integer id) {
        LOGGER.info("Received request to delete appointment with id: {}", id);
        return this.appointmentRepository.deleteById(id).doOnError(error -> LOGGER.error("Error deleting appointment: {}", error.getMessage()));
    }
}
