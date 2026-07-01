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

    public List<Application> getAllApplications(String email) {
        return applicationRepository.findByUser(getUser(email));
    }

    public Application getById(Long id,  String email) {
        Application app = applicationRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("Application not found"));
        if(!app.getUser().getEmail().equals(email)) {
            throw new RuntimeException("Unauthorized");
        }
        return app;
    }

    public Application createApplication(Application application, String email) {
        application.setUser(getUser(email));
        return applicationRepository.save(application);
    }

    public Application updateApplication(Long id, Application updated,  String email) {
        Application app = getById(id, email);
        app.setCompanyName(updated.getCompanyName());
        app.setPosition(updated.getPosition());
        app.setStatus(updated.getStatus());
        app.setAppliedDate(updated.getAppliedDate());
        app.setDeadLine(updated.getDeadLine());
        app.setJobUrl(updated.getJobUrl());
        app.setNotes(updated.getNotes());
        app.setSalaryRange(updated.getSalaryRange());
        return applicationRepository.save(app);
    }

    public void deleteApplication(Long id, String email) {
        Application app = getById(id, email);
        applicationRepository.delete(app);
    }
}
