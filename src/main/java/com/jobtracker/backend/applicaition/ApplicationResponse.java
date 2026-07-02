package com.jobtracker.backend.applicaition;


import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ApplicationResponse {
    private Long id;
    private String companyName;
    private String position;
    private ApplicationStatus status;
    private LocalDate appliedDate;
    private LocalDate deadLine;
    private String jobUrl;
    private String notes;
    private String salaryRange;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
