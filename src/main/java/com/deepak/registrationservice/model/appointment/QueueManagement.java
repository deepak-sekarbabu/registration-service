package com.deepak.registrationservice.model.appointment;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.sql.Date;

@Getter
@Setter
@Table(name = "queue_management")
public class QueueManagement {
    @Id
    @Column("queue_management_id")
    private Integer queueManagementId;

    @Column("appointment_id")
    private Integer appointmentId;

    @Column("slot_id")
    private Integer slotId;

    @Column("clinic_id")
    private Integer clinicId;

    @Column("doctor_id")
    private String doctorId;

    @Column("initial_queue_no")
    private Integer initialQueueNo;

    @Column("current_queue_no")
    private Integer currentQueueNo;

    @Column("advance_paid")
    private Boolean advancePaid;

    @Column("cancelled")
    private Boolean cancelled;

    @Column("advance_revert_if_paid")
    private Boolean advanceRevertIfPaid;

    @Column("patient_reached")
    private Boolean patientReached;

    @Column("visit_status")
    private String visitStatus;

    @Column("consultation_fee_paid")
    private Boolean consultationFeePaid;

    @Column("consultation_fee_amount")
    private Double consultationFeeAmount;

    @Column("transaction_id_advance_fee")
    private String transactionIdAdvanceFee;

    @Column("transaction_id_consultation_fee")
    private String transactionIdConsultationFee;

    @Column("transaction_id_advance_revert")
    private String transactionIdAdvanceRevert;

    @Column("queue_date")
    private Date date;
}