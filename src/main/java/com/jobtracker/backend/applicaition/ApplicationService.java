package com.jobtracker.backend.applicaition;


import com.jobtracker.backend.user.User;
import com.jobtracker.backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicationService {
    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;

    private User getUser(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    private ApplicationResponse toResponse(Application app) {
        ApplicationResponse res =  new ApplicationResponse();
        res.setId(app.getId());
        res.setCompanyName(app.getCompanyName());
        res.setPosition(app.getPosition());
        res.setStatus(app.getStatus());
        res.setAppliedDate(app.getAppliedDate());
        res.setDeadLine(app.getDeadLine());
        res.setJobUrl(app.getJobUrl());
        res.setNotes(app.getNotes());
        res.setSalaryRange(app.getSalaryRange());
        res.setCreatedAt(app.getCreatedAt());
        res.setUpdatedAt(app.getUpdatedAt());
        return res;

    }

    public List<ApplicationResponse> getAllApplications(String email) {
        return applicationRepository.findByUser(getUser(email))
                .stream().map(this::toResponse).toList();
    }

    public ApplicationResponse getById(Long id,  String email) {
        Application app = applicationRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("Application not found"));
        if(!app.getUser().getEmail().equals(email)) {
            throw new RuntimeException("Unauthorized");
        }
        return toResponse(app);
    }

    public ApplicationResponse createApplication(ApplicationRequest request, String email) {
        Application app = new Application();
        app.setUser(getUser(email));
        app.setCompanyName(request.getCompanyName());
        app.setPosition(request.getPosition());
        app.setStatus(request.getStatus());
        app.setAppliedDate(request.getAppliedDate());
        app.setDeadLine(request.getDeadLine());
        app.setJobUrl(request.getJobUrl());
        app.setNotes(request.getNotes());
        app.setSalaryRange(request.getSalaryRange());
        return toResponse(applicationRepository.save(app));
    }

    public ApplicationResponse updateApplication(Long id, ApplicationRequest request,  String email) {
        Application app = applicationRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("Application not found"));
        if(!app.getUser().getEmail().equals(email)) {
            throw new RuntimeException("Unauthorized");
        }
        app.setCompanyName(request.getCompanyName());
        app.setPosition(request.getPosition());
        app.setStatus(request.getStatus());
        app.setAppliedDate(request.getAppliedDate());
        app.setDeadLine(request.getDeadLine());
        app.setJobUrl(request.getJobUrl());
        app.setNotes(request.getNotes());
        app.setSalaryRange(request.getSalaryRange());
        return toResponse(applicationRepository.save(app));
    }

    public void deleteApplication(Long id, String email) {
        Application app = applicationRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("Application not found"));
        if(!app.getUser().getEmail().equals(email)) {
            throw new RuntimeException("Unauthorized");
        }
        applicationRepository.delete(app);
    }
}
