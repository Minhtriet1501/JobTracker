package com.jobtracker.backend.user;


import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    String name;

    @Column(columnDefinition = "TEXT")
    private String resumeText;

    LocalDateTime creat_at;

    @PrePersist
    public void prePersist() {
        this.creat_at = LocalDateTime.now();
    }

}
