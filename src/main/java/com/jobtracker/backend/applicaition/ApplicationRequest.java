package com.jobtracker.backend.applicaition;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ApplicationRequest {
    private String companyName;
    private String position;
    private ApplicationStatus status;
    private LocalDate appliedDate;
    private LocalDate deadLine;
    private String jobUrl;
    private String notes;
    private String salaryRange;
}
