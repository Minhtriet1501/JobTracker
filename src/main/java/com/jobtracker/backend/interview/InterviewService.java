package com.jobtracker.backend.interview;


import com.jobtracker.backend.applicaition.Application;
import com.jobtracker.backend.applicaition.ApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InterviewService {
    private final InterviewRepository interviewRepository;
    private final ApplicationRepository applicationRepository;

    private Application getApplication(Long applicationId, String email) {
        Application app = applicationRepository.findById(applicationId).orElseThrow(() -> new RuntimeException("Application not found"));
        if(!app.getUser().getEmail().equals(email)) {
            throw new RuntimeException("UnAuthorized");
        }
        return app;
    }

    private InterviewResponse toResponse(Interview interview) {
        InterviewResponse res = new InterviewResponse();
        res.setId(interview.getId());
        res.setApplicationId(interview.getApplication().getId());
        res.setRound(interview.getRound());
        res.setType(interview.getType());
        res.setNotes(interview.getNotes());
        res.setOutcome(interview.getOutcome());
        return res;
    }

    public List<InterviewResponse> getAll(Long applicationId, String email) {
        Application app = getApplication(applicationId, email);
        return interviewRepository.findByApplication(app).stream().map(this::toResponse).toList();
    }

    public InterviewResponse create(Long applicationId,InterviewRequest request, String email) {
        Application app = getApplication(applicationId, email);
        Interview interview = new Interview();
        interview.setApplication(app);
        interview.setRound(request.getRound());
        interview.setType(request.getType());
        interview.setNotes(request.getNotes());
        interview.setScheduleAt(request.getScheduleAt());
        interview.setOutcome(request.getOutcome());
        return toResponse(interviewRepository.save(interview));
    }

    public InterviewResponse update(Long id, InterviewRequest request, String email) {
        Interview interview = interviewRepository.findById(id).orElseThrow(() -> new RuntimeException("Interview not found"));
        if(!interview.getApplication().getUser().getEmail().equals(email)) {
            throw new RuntimeException("UnAuthorized");
        }
        interview.setRound(request.getRound());
        interview.setType(request.getType());
        interview.setNotes(request.getNotes());
        interview.setScheduleAt(request.getScheduleAt());
        interview.setOutcome(request.getOutcome());
        return toResponse(interviewRepository.save(interview));
    }

    public void delete(Long id, String email) {
        Interview interview = interviewRepository.findById(id).orElseThrow(() -> new RuntimeException("Interview not found"));
        if(!interview.getApplication().getUser().getEmail().equals(email)) {
            throw new RuntimeException("UnAuthorized");
        }
        interviewRepository.delete(interview);
    }
}
