package com.jobtracker.backend.applicaition;


import com.jobtracker.backend.ai.AiAnalysisRepository;
import com.jobtracker.backend.common.UnauthorizedException;
import com.jobtracker.backend.common.ResourceNotFoundException;
import com.jobtracker.backend.interview.InterviewRepository;
import com.jobtracker.backend.user.User;
import com.jobtracker.backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ApplicationService {
    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final InterviewRepository interviewRepository;
    private final AiAnalysisRepository aiAnalysisRepository;

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

    public List<ApplicationResponse> getAllApplications(String email, ApplicationStatus status,  String companyName) {
        List<Application> applications;
        if(status != null && companyName != null) {
            applications = applicationRepository.findByUserAndStatusAndCompanyNameContainingIgnoreCase(getUser(email), status, companyName);
        }
        else if(status != null) {
            applications = applicationRepository.findByUserAndStatus(getUser(email), status);
        }
        else if(companyName != null) {
            applications = applicationRepository.findByUserAndCompanyNameContainingIgnoreCase(getUser(email), companyName);
        }
        else {
            applications = applicationRepository.findByUser(getUser(email));
        }


        return applications.stream().map(this::toResponse).toList();
    }

    public ApplicationResponse getById(Long id,  String email) {
        Application app = applicationRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Application not found"));
        if(!app.getUser().getEmail().equals(email)) {
            throw new UnauthorizedException();
        }
        return toResponse(app);
    }

    public ApplicationStatsResponse getStats(String email) {
        User user = getUser(email);
        Map<String, Long> byStatus = new HashMap<>();
        for(ApplicationStatus status : ApplicationStatus.values()) {
            byStatus.put(status.name(), applicationRepository.countByUserAndStatus(user, status));
        }

        ApplicationStatsResponse stats = new ApplicationStatsResponse();
        stats.setTotal(applicationRepository.countByUser(user));
        stats.setByStatus(byStatus);
        return stats;
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
        Application app = applicationRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Application not found"));
        if(!app.getUser().getEmail().equals(email)) {
            throw new UnauthorizedException();
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

    @Transactional
    public void deleteApplication(Long id, String email) {
        Application app = applicationRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Application not found"));
        if(!app.getUser().getEmail().equals(email)) {
            throw new UnauthorizedException();
        }
        interviewRepository.deleteAll(interviewRepository.findByApplication(app));
        aiAnalysisRepository.findByApplication(app).ifPresent(aiAnalysisRepository::delete);
        applicationRepository.delete(app);
    }
}
