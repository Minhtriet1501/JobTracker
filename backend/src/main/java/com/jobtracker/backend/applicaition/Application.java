package com.jobtracker.backend.applicaition;


import com.jobtracker.backend.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "Application")
@Data
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String companyName;

    @Column(nullable = false)
    private String position;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;



    private LocalDate appliedDate;
    private LocalDate deadLine;
    private String jobUrl;


    @Column(columnDefinition = "TEXT")
    private String notes;

    private String salaryRange;

    private  LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

}

