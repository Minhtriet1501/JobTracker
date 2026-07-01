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
    public List<Application> getAll(@AuthenticationPrincipal UserDetails userDetails ) {
        return applicationService.getAllApplications(userDetails.getUsername());
    }

    @GetMapping("/{id}")
    public Application getById(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails){
        return applicationService.getById(id, userDetails.getUsername());
    }

    @PostMapping
    public Application create(@RequestBody Application application, @AuthenticationPrincipal UserDetails userDetails) {
        return applicationService.createApplication(application, userDetails.getUsername());
    }

    @PutMapping("/{id}")
    public Application update(@PathVariable Long id, @RequestBody Application application, @AuthenticationPrincipal UserDetails userDetails) {
        return  applicationService.updateApplication(id, application, userDetails.getUsername());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        applicationService.deleteApplication(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }


}
