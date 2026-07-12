package com.jobtracker.backend.user;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserUpdateRequest {
    @Size(min = 1, max = 20000, message = "Resume text must not be blank and at most 20000 characters")
    private String resumeText;

    @Size(min = 1, max = 100, message = "Name must not be blank and at most 100 characters")
    private String name;
}
