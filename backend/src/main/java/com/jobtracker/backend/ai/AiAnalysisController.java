package com.jobtracker.backend.ai;


import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/applications/{applicationId}/analyze")
@RequiredArgsConstructor
public class AiAnalysisController {

    private final AiService aiService;

    @PostMapping
    public ResponseEntity<AiAnalysisResponse> analyze(@PathVariable Long applicationId, @Valid @RequestBody AiAnalysisRequest request, @AuthenticationPrincipal UserDetails userDetails) throws IOException {
        AiAnalysisResponse response = aiService.analyze(applicationId, request, userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<AiAnalysisResponse> getAnalysis(@PathVariable Long applicationId, @AuthenticationPrincipal UserDetails userDetails){
        AiAnalysisResponse response = aiService.getAnalysis(applicationId, userDetails.getUsername());
        return ResponseEntity.ok(response);

    }

}
