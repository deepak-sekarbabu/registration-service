package com.deepak.registrationservice.model.appointment;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Table(name = "slot_information")
public class SlotInformation {

  @Id
  @Column("slot_id")
  private Integer slotId;

  @Column("slot_no")
  private Integer slotNo;

  @Column("shift_time")
  private String shiftTime;

  @Column("slot_time")
  private String slotTime;

  @Column("clinic_id")
  private String clinicId;

  @Column("doctor_id")
  private String doctorId;

  @Column("slot_date")
  private String slotDate;

  @Column("is_available")
  private Boolean isAvailable;
}
