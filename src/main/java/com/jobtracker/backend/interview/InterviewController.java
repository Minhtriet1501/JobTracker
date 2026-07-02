package com.jobtracker.backend.interview;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications/{applicationId}/interviews")
@RequiredArgsConstructor
public class InterviewController {
    private final InterviewService interviewService;

    @GetMapping
    public List<InterviewResponse> getAll(@PathVariable long applicationId,
                                          @AuthenticationPrincipal UserDetails userDetails) {
        return interviewService.getAll(applicationId, userDetails.getUsername());
    }

    @PostMapping
    public ResponseEntity<InterviewResponse> create(@PathVariable Long applicationId,
                                                    @RequestBody InterviewRequest request,
                                                    @AuthenticationPrincipal UserDetails userDetails) {
        InterviewResponse res = interviewService.create(applicationId, request, userDetails.getUsername());
        return ResponseEntity.status(201).body(res);
    }

    @PutMapping("/{id}")
    public InterviewResponse update(@PathVariable Long id,
                                    @RequestBody InterviewRequest request,
                                    @AuthenticationPrincipal UserDetails userDetails) {
        return interviewService.update(id, request, userDetails.getUsername());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        interviewService.delete(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }


}
