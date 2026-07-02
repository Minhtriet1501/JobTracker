package com.jobtracker.backend.interview;


import com.jobtracker.backend.applicaition.Application;
import com.jobtracker.backend.applicaition.ApplicationStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "interviews")
@Data
public class Interview {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "application_id", nullable = false)
    private Application application;

    private Integer round;

    @Enumerated(EnumType.STRING)
    private InterviewType type;

    @Column(columnDefinition = "TEXT")
    private String notes;

    private LocalDateTime scheduleAt;

    @Enumerated(EnumType.STRING)
    private InterviewOutcome outcome;

}
