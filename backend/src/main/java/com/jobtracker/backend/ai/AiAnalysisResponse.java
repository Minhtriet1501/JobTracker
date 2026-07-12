package com.jobtracker.backend.ai;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AiAnalysisResponse {
    private Long id;
    private Long applicationId;
    private Integer matchScore;
    private String summary;
    private String suggestion;
    private String interviewQs;
    private LocalDateTime analysisAt;
}
