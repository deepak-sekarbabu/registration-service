package com.deepak.registrationservice.model.appointment;

import com.deepak.registrationservice.model.appointment.enums.AppointmentFor;
import com.deepak.registrationservice.model.appointment.enums.AppointmentType;
import com.deepak.registrationservice.model.appointment.enums.Symptom;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString(exclude = {"appointmentType", "appointmentFor", "appointmentForName", "appointmentForAge", "otherSymptoms", "symptom"})
@EqualsAndHashCode
@JsonFormat
@Table(name = "appointments")
public class AppointmentDetails {

    @Hidden
    @Id
    @Column("appointmentId")
    private Integer appointmentId;

    @Column("user_id")
    private Integer userId;

    @Column("appointment_type")
    @Schema(description = "Appointment Type", example = "GENERAL_CHECKUP")
    private AppointmentType appointmentType;

    @Schema(description = "Appointment For", example = "SELF")
    @Column("appointment_for")
    private AppointmentFor appointmentFor;

    @Schema(description = "Appointment For Name", example = "Deepak Sharma")
    @Column("appointment_for_name")
    @Max(150)
    private String appointmentForName;

    @Schema(description = "Age of person seeking appointment", example = "25")
    @Column("appointment_for_age")
    private String appointmentForAge;

    @Schema(description = "Symptoms", example = "HEADACHE")
    @Column("symptom")
    private Symptom symptom;

    @Schema(description = "Other Symptoms", example = "Vomiting etc")
    @Column("other_symptoms")
    @Max(250)
    private String otherSymptoms;

    @Schema(description = "Appointment Date", example = "2023-12-24T16:25:48.748Z")
    @Column("appointment_date")
    private LocalDateTime appointmentDate;

    @Schema(description = "Doctors Name", example = "Dr Dinesh Child Specialist")
    @Column("doctor_name")
    private String doctorName;

    @Schema(description = "Clinic Name", example = "Dr Dinesh Child Specialist Clinic")
    @Column("clinic_id")
    private String clinicId;

    @Column("active")
    @Schema(description = "Active or Not", example = "true")
    private boolean active;

}