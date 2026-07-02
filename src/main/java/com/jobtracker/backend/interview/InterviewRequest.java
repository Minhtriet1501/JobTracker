package com.jobtracker.backend.interview;


import lombok.Data;

@Data
public class InterviewRequest {
    private Integer round;
    private InterviewType type;
    private String notes;
    private InterviewOutcome outcome;
}
