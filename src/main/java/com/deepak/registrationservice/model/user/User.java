package com.deepak.registrationservice.model.user;

import com.deepak.registrationservice.validation.ValidDate;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;


@ToString
@Getter
@Setter
@Table(name = "users")
public class User {
    @Id
    @Hidden
    private Integer id;

    @Column("name")
    private String name;

    @Column("phoneNumber")
    @Pattern(regexp = "\\+91\\d{10}", message = "Phone number must start with +91 followed by 10 digits")
    private String phoneNumber;

    @Column("email")
    @Email(message = "Invalid email format")
    private String email;

    @Column("birthdate")
    @ValidDate(message = "Invalid date format. Use yyyy-MM-dd format.")
    private LocalDate birthdate;

}
