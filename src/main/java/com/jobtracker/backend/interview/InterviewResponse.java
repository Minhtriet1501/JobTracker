package com.jobtracker.backend.interview;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InterviewResponse {
    private Long id;
    private Long applicationId;
    private Integer round;
    private InterviewType type;
    private String notes;
    private LocalDateTime scheduleAt;
    private InterviewOutcome outcome;
}
