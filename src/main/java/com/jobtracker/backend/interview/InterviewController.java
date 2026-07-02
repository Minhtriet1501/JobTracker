package com.jobtracker.backend.interview;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/applications/{applicationId}/interviews")
@RequiredArgsConstructor
public class InterviewController {
    private final InterviewRepository interviewRepository;

    @PostMapping
    public List<InterviewResponse> getAll(@PathVariable long applicationId,
                                          @AuthenticationPrincipal UserDetails userDetails) {
        return getAll(applicationId, userDetails);
    }
}
