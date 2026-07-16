package com.jobtracker.backend.ai;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobtracker.backend.applicaition.Application;
import com.jobtracker.backend.applicaition.ApplicationRepository;
import com.jobtracker.backend.common.AiServiceException;
import com.jobtracker.backend.common.BadRequestException;
import com.jobtracker.backend.common.RateLimitException;
import com.jobtracker.backend.common.UnauthorizedException;
import com.jobtracker.backend.common.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
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
        aiAnalysisRepository.findByApplication(app).ifPresent(existing -> {
            if(existing.getAnalyzeAt().isAfter(LocalDateTime.now().minusHours(1))) {
                throw new RateLimitException("Please wait 1 hour before analyzing again");
            }
        });

        String resumeText = app.getUser().getResumeText();
        if(resumeText == null || resumeText.isBlank()) {
            throw new BadRequestException("Please update your resume text before trying to analyze.");
        }


        String prompt = """
                Given this resume and job description, provide:
                1. matchScore (0-100): how well the candidate matches the job (response by integer type)
                2. summary: brief summary of the role (2-3 sentences)
                3. suggestion: CV improvement suggestions, as a single string with 3-5 bullet points separated by "\\n" (do not use a JSON array)
                4. interviewQuestions: likely interview questions, as a single string with 5 questions separated by "\\n" (do not use a JSON array)

                Resume:
                """ + resumeText + """

                Job Description:
                """ + request.getJobDescription() + """

                Respond in JSON format. matchScore must be a JSON integer; summary, suggestion, and interviewQuestions must be JSON strings (not arrays or objects):
                {
                    "matchScore": 75,
                    "summary": "...",
                    "suggestion": "...",
                    "interviewQuestions": "..."
                }
                """;

        String claudeResponse = callClaude(prompt);
        Map<String, Object> parsed = parseClaudeResponse(claudeResponse);

        AiAnalysis aiAnalysis = aiAnalysisRepository.findByApplication(app).orElse(new AiAnalysis());
        aiAnalysis.setApplication(app);
        aiAnalysis.setMatchScore(coerceMatchScore(parsed.get("matchScore")));
        aiAnalysis.setSummary(coerceStringField(parsed.get("summary"), "summary"));
        aiAnalysis.setSuggestion(coerceStringField(parsed.get("suggestion"), "suggestion"));
        aiAnalysis.setInterviewQs(coerceStringField(parsed.get("interviewQuestions"), "interviewQuestions"));
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
                                           "max_tokens", 4096,
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

        String jsonObject = extractJsonObject(text);
        try {
            return objectMapper.readValue(jsonObject, Map.class);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse AI response as JSON. Raw text: {}", text);
            throw new AiServiceException("AI service returned a response we couldn't understand. Please try again.");
        }
    }

    /**
     * Extracts the first balanced {} object from text by tracking brace depth
     * and string state, instead of naively matching the first "{" and last "}"
     * which breaks on stray braces in surrounding prose or a truncated response.
     */
    private String extractJsonObject(String text) {
        int start = text.indexOf('{');
        if (start == -1) {
            log.error("No JSON object found in AI response. Raw text: {}", text);
            throw new AiServiceException("AI service returned a response we couldn't understand. Please try again.");
        }

        int depth = 0;
        boolean inString = false;
        boolean escaped = false;
        for (int i = start; i < text.length(); i++) {
            char c = text.charAt(i);
            if (inString) {
                if (escaped) {
                    escaped = false;
                } else if (c == '\\') {
                    escaped = true;
                } else if (c == '"') {
                    inString = false;
                }
                continue;
            }
            if (c == '"') {
                inString = true;
            } else if (c == '{') {
                depth++;
            } else if (c == '}') {
                depth--;
                if (depth == 0) {
                    return text.substring(start, i + 1);
                }
            }
        }

        log.error("AI response JSON was truncated (likely hit max_tokens). Raw text: {}", text);
        throw new AiServiceException("AI service response was cut off. Please try again.");
    }

    private int coerceMatchScore(Object value) {
        if (value instanceof Integer i) {
            return i;
        }
        if (value instanceof Number n) {
            return n.intValue();
        }
        if (value instanceof String s) {
            try {
                return Integer.parseInt(s.trim());
            } catch (NumberFormatException e) {
                throw new AiServiceException("AI service returned an invalid matchScore: " + s);
            }
        }
        throw new AiServiceException("AI service returned an unexpected type for matchScore: "
                + (value == null ? "null" : value.getClass().getSimpleName()));
    }

    private String coerceStringField(Object value, String fieldName) {
        if (value instanceof String s) {
            return s;
        }
        if (value instanceof List<?> list) {
            // Claude sometimes returns a JSON array despite being told not to - join it back
            // into the "\n"-separated string format the rest of the app expects.
            return list.stream().map(String::valueOf).collect(Collectors.joining("\n"));
        }
        throw new AiServiceException("AI service returned an unexpected type for " + fieldName + ": "
                + (value == null ? "null" : value.getClass().getSimpleName()));
    }

    private AiAnalysisResponse toResponse(AiAnalysis analysis) {
        AiAnalysisResponse res = new AiAnalysisResponse();
        res.setId(analysis.getId());
        res.setApplicationId(analysis.getApplication().getId());
        res.setMatchScore(analysis.getMatchScore());
        res.setSummary(analysis.getSummary());
        res.setSuggestion(analysis.getSuggestion());
        res.setInterviewQs(analysis.getInterviewQs());
        res.setAnalysisAt(analysis.getAnalyzeAt());
        return res;
    }

}

