package com.deepak.registrationservice.exception;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorDetails {
  private String timestamp;
  private String message;
  private String details;
}
