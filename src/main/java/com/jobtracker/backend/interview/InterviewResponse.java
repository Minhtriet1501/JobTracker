package com.jobtracker.backend.interview;


import lombok.Data;

@Data
public class InterviewResponse {
    private Long id;
    private Long applicationId;
    private Integer round;
    private InterviewType type;
    private String notes;
    private InterviewOutcome outcome;
}
