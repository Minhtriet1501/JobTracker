package com.jobtracker.backend.applicaition;


import com.jobtracker.backend.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByUser(User user);
    List<Application> findByUserAndStatus(User user, ApplicationStatus status);
    List<Application> findByUserAndCompanyNameContainingIgnoreCase(User user, String companyName);
    List<Application> findByUserAndStatusAndCompanyNameContainingIgnoreCase(User user, ApplicationStatus status, String companyName);
    long countByUserAndStatus(User user, ApplicationStatus status);
    long countByUser(User user);
}
