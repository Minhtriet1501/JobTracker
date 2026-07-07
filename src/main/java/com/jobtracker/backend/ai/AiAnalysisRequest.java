package com.jobtracker.backend.ai;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AiAnalysisRequest {
    @NotBlank(message = "Job description is required")
    private String jobDescription;
}
