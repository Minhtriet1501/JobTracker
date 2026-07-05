package com.jobtracker.backend.interview;


import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InterviewRequest {
    @NotNull(message = "Round is required")
    private Integer round;

    @NotNull(message = "Type is required")
    private InterviewType type;

    private String notes;
    private LocalDateTime scheduleAt;
    private InterviewOutcome outcome;

}
