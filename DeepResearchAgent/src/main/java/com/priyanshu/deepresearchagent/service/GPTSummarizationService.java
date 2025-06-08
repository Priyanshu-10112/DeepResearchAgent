package com.priyanshu.deepresearchagent.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@Service
public class GPTSummarizationService {
    private static final Logger logger = LoggerFactory.getLogger(GPTSummarizationService.class);
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    @Value("${openai.api.key}")
    private String apiKey;
    @Value("${openai.api.url}")
    private String apiUrl;

    public GPTSummarizationService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.objectMapper = new ObjectMapper();
    }

    public String generateSummary(String topic, List<String> contentList) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "gpt-3.5-turbo");
            requestBody.put("temperature", 0.7);
            requestBody.put("max_tokens", 2000);
            
            List<Map<String, String>> messages = new ArrayList<>();
            messages.add(Map.of("role", "system", "content", 
                "You are a research assistant tasked with summarizing information from multiple sources. " +
                "Provide a comprehensive summary that includes key findings, trends, and insights. " +
                "Format your response with clear sections and bullet points where appropriate."));
            
            String prompt = String.format("Please analyze and summarize the following information about '%s':\n\n%s",
                topic, String.join("\n\n", contentList));
            messages.add(Map.of("role", "user", "content", prompt));
            
            requestBody.put("messages", messages);
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            Map<String, Object> response = restTemplate.postForObject(apiUrl, request, Map.class);
            
            if (response != null && response.containsKey("choices")) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
                if (!choices.isEmpty()) {
                    Map<String, Object> choice = choices.get(0);
                    Map<String, String> message = (Map<String, String>) choice.get("message");
                    return message.get("content");
                }
            }
            
            return generateFallbackSummary(topic, contentList);
            
        } catch (Exception e) {
            logger.error("Error generating summary with GPT: {}", e.getMessage());
            return generateFallbackSummary(topic, contentList);
        }
    }

    private String generateFallbackSummary(String topic, List<String> contentList) {
        StringBuilder summary = new StringBuilder();
        summary.append("Summary for topic: ").append(topic).append("\n\n");
        summary.append("Key Insights:\n");
        summary.append("- Information gathered from ").append(contentList.size()).append(" sources\n");
        summary.append("- Analysis of current trends and developments\n");
        summary.append("- Overview of key findings and implications\n\n");
        summary.append("Recommendations:\n");
        summary.append("- Consider additional research for comprehensive understanding\n");
        summary.append("- Monitor developments in this area\n");
        summary.append("- Evaluate potential impact on related fields\n");
        return summary.toString();
    }
} 