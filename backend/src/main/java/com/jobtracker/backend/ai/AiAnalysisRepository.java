package com.jobtracker.backend.ai;

import com.jobtracker.backend.applicaition.Application;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AiAnalysisRepository extends JpaRepository<AiAnalysis, Long> {
    Optional<AiAnalysis>  findByApplication(Application application);
}
