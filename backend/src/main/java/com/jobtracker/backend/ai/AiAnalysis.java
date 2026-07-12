package com.jobtracker.backend.ai;


import com.jobtracker.backend.applicaition.Application;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "ai_analysis")
@Data
public class AiAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne
    @JoinColumn(name = "application_id", nullable = false)
    private Application application;

    private Integer matchScore;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Column(columnDefinition = "TEXT")
    private String suggestion;

    @Column(columnDefinition = "TEXT")
    private String interviewQs ;

    private LocalDateTime analyzeAt;

    @PrePersist
    public void prePersist() {
        analyzeAt = LocalDateTime.now();
    }


}
