package com.jobtracker.backend.applicaition;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationController {
    private final ApplicationService applicationService;

    @GetMapping
    public List<ApplicationResponse> getAll(@AuthenticationPrincipal UserDetails userDetails ) {
        return applicationService.getAllApplications(userDetails.getUsername());
    }

    @GetMapping("/{id}")
    public ApplicationResponse getById(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails){
        return applicationService.getById(id, userDetails.getUsername());
    }

    @PostMapping
    public ResponseEntity<ApplicationResponse> create(@RequestBody ApplicationRequest application, @AuthenticationPrincipal UserDetails userDetails) {
        ApplicationResponse res = applicationService.createApplication(application, userDetails.getUsername());
        return ResponseEntity.status(201).body(res);
    }

    @PutMapping("/{id}")
    public ApplicationResponse update(@PathVariable Long id, @RequestBody ApplicationRequest application, @AuthenticationPrincipal UserDetails userDetails) {
        return  applicationService.updateApplication(id, application, userDetails.getUsername());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        applicationService.deleteApplication(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }


}
