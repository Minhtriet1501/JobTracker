package com.jobtracker.backend.ai;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobtracker.backend.applicaition.Application;
import com.jobtracker.backend.applicaition.ApplicationRepository;
import com.jobtracker.backend.common.BadRequestException;
import com.jobtracker.backend.common.UnauthorizedException;
import com.jobtracker.backend.common.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AiService {

    private final AiAnalysisRepository  aiAnalysisRepository;
    private final ApplicationRepository applicationRepository;
    private final ObjectMapper objectMapper;

    @Value("${anthropic.api.key}")
    private String apiKey;

    private static final String API_URL = "https://api.anthropic.com/v1/messages";
    private static final String ANTHROPIC_VERSION = "2023-06-01";
    private final OkHttpClient httpClient = new OkHttpClient();

    public AiAnalysisResponse analyze(Long applicationId, AiAnalysisRequest request, String email) throws IOException {
        Application app =  applicationRepository.findById(applicationId).orElseThrow(() -> new ResourceNotFoundException("Application not found"));
        if(!app.getUser().getEmail().equals(email)) {
            throw new UnauthorizedException();
        }

        String resumeText = app.getUser().getResumeText();
        if(resumeText == null || resumeText.isBlank()) {
            throw new BadRequestException("Please update your resume text before trying to analyze.");
        }


        String prompt = """
                Given this resume and job description, provide:
                1. matchScore (0-100): how well the candidate matches the job (response by integer type)
                2. summary: brief summary of the role (2-3 sentences)
                3. suggestions: CV improvement suggestions (3-5 bullet points)
                4. interviewQuestions: likely interview questions (5 questions)

                Resume:
                """ + resumeText + """

                Job Description:
                """ + request.getJobDescription() + """

                Respond in JSON format:
                {
                    "matchScore": 75,
                    "summary": "...",
                    "suggestions": "...",
                    "interviewQuestions": "..."
                }
                """;

        String claudeResponse = callClaude(prompt);
        Map<String, Object> parsed = parseClaudeResponse(claudeResponse);

        AiAnalysis aiAnalysis = aiAnalysisRepository.findByApplication(app).orElse(new AiAnalysis());
        aiAnalysis.setApplication(app);
        aiAnalysis.setMatchScore((Integer) parsed.get("matchScore"));
        aiAnalysis.setSummary((String) parsed.get("summary"));
        aiAnalysis.setSuggestion((String) parsed.get("suggestions"));
        aiAnalysis.setInterviewQs((String) parsed.get("interviewQuestions"));
        aiAnalysisRepository.save(aiAnalysis);
        return toResponse(aiAnalysis);

    }
    public AiAnalysisResponse getAnalysis(Long applicationId, String email) {
        Application app =   applicationRepository.findById(applicationId).orElseThrow(() -> new ResourceNotFoundException("Application not found"));
        if(!app.getUser().getEmail().equals(email)) {
            throw new UnauthorizedException();
        }
        AiAnalysis aiAnalysis = aiAnalysisRepository.findByApplication(app).orElseThrow(() -> new ResourceNotFoundException("No analysis found for this application"));
        return toResponse(aiAnalysis);
    }






    private String callClaude(String prompt) throws IOException {
        Map<String, Object> body = Map.of("model", "claude-sonnet-4-5",
                                           "max_tokens", 1024,
                                           "messages", List.of((Map.of("role", "user", "content", prompt)))
        );

        RequestBody requestBody = RequestBody.create(
                objectMapper.writeValueAsString(body),
                MediaType.get("application/json")
        );

        Request request = new Request.Builder()
                .url(API_URL)
                .addHeader("x-api-key", apiKey)
                .addHeader("content-type", "application/json")
                .addHeader("anthropic-version", ANTHROPIC_VERSION)
                .post(requestBody)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            String responseBody = response.body().string();
            if (!response.isSuccessful()) {
                throw new IOException("Claude API error " + response.code() + ": " + responseBody);
            }
            return responseBody;
        }
    }

    private Map<String, Object> parseClaudeResponse(String responseBody) throws IOException {
        Map<String, Object> responseMap = objectMapper.readValue(responseBody, Map.class);
        List<Map<String, Object>> content = (List<Map<String, Object>>) responseMap.get("content");
        String text = (String) content.get(0).get("text");
        int start = text.indexOf("{");
        int end = text.lastIndexOf("}") + 1;
        return objectMapper.readValue(text.substring(start, end), Map.class);
    }

    private AiAnalysisResponse toResponse(AiAnalysis analysis) {
        AiAnalysisResponse res = new AiAnalysisResponse();
        res.setId(analysis.getId());
        res.setApplicationId(analysis.getApplication().getId());
        res.setMatchScore(analysis.getMatchScore());
        res.setSummary(analysis.getSummary());
        res.setSuggestions(analysis.getSuggestion());
        res.setInterviewQs(analysis.getInterviewQs());
        res.setAnalysisAt(analysis.getAnalyzeAt());
        return res;
    }

}

