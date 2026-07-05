package com.jobtracker.backend.applicaition;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ApplicationRequest {
    @NotBlank(message = "Company name is required")
    private String companyName;

    @NotBlank(message = "Position is required")
    private String position;

    private ApplicationStatus status;
    private LocalDate appliedDate;
    private LocalDate deadLine;
    private String jobUrl;
    private String notes;
    private String salaryRange;
}
