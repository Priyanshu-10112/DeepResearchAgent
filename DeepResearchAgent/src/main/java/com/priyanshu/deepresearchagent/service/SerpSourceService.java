package com.priyanshu.deepresearchagent.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.priyanshu.deepresearchagent.config.ApiConfig;
import com.priyanshu.deepresearchagent.exception.ApiException;
import com.priyanshu.deepresearchagent.model.DynamicQuery;
import com.priyanshu.deepresearchagent.model.SourceContent;
import com.priyanshu.deepresearchagent.util.RelevanceScorer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SerpSourceService implements DataSourceService {
    
    private static final Logger logger = LoggerFactory.getLogger(SerpSourceService.class);
    private final RestTemplate restTemplate;
    private final ApiConfig apiConfig;
    
    public SerpSourceService(RestTemplate restTemplate, ApiConfig apiConfig) {
        this.restTemplate = restTemplate;
        this.apiConfig = apiConfig;
    }
    
    @Override
    public List<SourceContent> fetchContent(String topic) {
        if (topic == null || topic.trim().isEmpty()) {
            logger.warn("Empty topic provided to SerpSourceService");
            return new ArrayList<>();
        }

        try {
            String url = UriComponentsBuilder.fromUriString(apiConfig.getSerpApiUrl())
                .queryParam("engine", "google_news")
                .queryParam("q", topic)
                .queryParam("api_key", apiConfig.getSerpApiKey())
                .build()
                .toUriString();
            
            logger.info("Fetching SerpAPI news from URL: {}", url);
            
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            
            if (response == null) {
                throw new ApiException("Null response received from SerpAPI", "SERPAPI", HttpStatus.INTERNAL_SERVER_ERROR.value());
            }
            
            if (!response.containsKey("news_results")) {
                logger.warn("No news results found in SerpAPI response");
                return new ArrayList<>();
            }
            
            List<Map<String, Object>> newsResults = (List<Map<String, Object>>) response.get("news_results");
            logger.info("Found {} SerpAPI news results", newsResults.size());
            
            return newsResults.stream()
                .map(this::convertToSourceContent)
                .collect(Collectors.toList());
                
        } catch (ApiException e) {
            logger.error("API error in SerpSourceService: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error in SerpSourceService: {}", e.getMessage(), e);
            throw new ApiException("Failed to fetch news from SerpAPI", "SERPAPI", HttpStatus.INTERNAL_SERVER_ERROR.value(), e);
        }
    }

    private SourceContent convertToSourceContent(Map<String, Object> newsResult) {
        String title = getStringValue(newsResult.get("title"));
        String snippet = getStringValue(newsResult.get("snippet"));
        String link = getStringValue(newsResult.get("link"));
        String date = getStringValue(newsResult.get("date"));
        String source = getStringValue(newsResult.get("source"));
        
        return new SourceContent(
            title != null ? title : "No title",
            snippet != null ? snippet : "No snippet available",
            link != null ? link : "#",
            date != null ? date : "N/A",
            source != null ? source : "Unknown source",
            RelevanceScorer.calculateRelevanceScore(title + " " + snippet, title + " " + snippet),
            "SERPAPI"
        );
    }

    private String getStringValue(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof String) {
            return (String) value;
        }
        if (value instanceof Map) {
            // If it's a map, try to get the first value
            Map<?, ?> map = (Map<?, ?>) value;
            if (!map.isEmpty()) {
                Object firstValue = map.values().iterator().next();
                return firstValue != null ? firstValue.toString() : null;
            }
        }
        return value.toString();
    }

    @Override
    public double getRelevanceScore(String query, String content) {
        return RelevanceScorer.calculateRelevanceScore(query, content);
    }
} 