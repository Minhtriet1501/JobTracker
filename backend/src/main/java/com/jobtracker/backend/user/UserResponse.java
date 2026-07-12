package com.jobtracker.backend.user;

import lombok.Data;

@Data
public class UserResponse {
    private String email;
    private String name;
    private String resumeText;
}
