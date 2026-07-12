package com.jobtracker.backend.applicaition;

import lombok.Data;

import java.util.Map;

@Data
public class ApplicationStatsResponse {
    private long total;
    private Map<String, Long> byStatus;
}
