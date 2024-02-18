package com.deepak.registrationservice.model.user;

import io.swagger.v3.oas.annotations.Hidden;
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
    private String phoneNumber;

    @Column("email")
    private String email;

    @Column("birthdate")
    private LocalDate birthdate;

}
