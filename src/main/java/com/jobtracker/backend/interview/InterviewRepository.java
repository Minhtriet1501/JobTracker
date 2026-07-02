package com.jobtracker.backend.interview;


import com.jobtracker.backend.applicaition.Application;
import com.jobtracker.backend.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InterviewRepository extends JpaRepository<Interview, Long> {
    List<Interview> findByApplication(Application application);
}
