package com.priyanshu.deepresearchagent.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.priyanshu.deepresearchagent.config.ApiConfig;
import com.priyanshu.deepresearchagent.exception.ApiException;
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
import java.util.stream.Collectors;
import java.util.Map;

@Service
public class OpenAlexSourceService implements DataSourceService {
    
    private static final Logger logger = LoggerFactory.getLogger(OpenAlexSourceService.class);
    private final RestTemplate restTemplate;
    private final ApiConfig apiConfig;
    
    public OpenAlexSourceService(RestTemplate restTemplate, ApiConfig apiConfig) {
        this.restTemplate = restTemplate;
        this.apiConfig = apiConfig;
    }
    
    @Override
    public List<SourceContent> fetchContent(String topic) {
        if (topic == null || topic.trim().isEmpty()) {
            logger.warn("Empty topic provided to OpenAlexSourceService");
            return new ArrayList<>();
        }

        try {
            String url = UriComponentsBuilder.fromUriString(apiConfig.getOpenAlexApiUrl())
                .path("/works")
                .queryParam("search", topic)
                .queryParam("filter", "has_abstract:true")
                .queryParam("mailto", apiConfig.getOpenAlexMailto())
                .queryParam("per_page", "25") // Add pagination limit
                .build()
                .toUriString();
            
            logger.info("Fetching OpenAlex data from URL: {}", url);
            
            OpenAlexResponse response = restTemplate.getForObject(url, OpenAlexResponse.class);
            
            if (response == null) {
                throw new ApiException("Null response received from OpenAlex", "OPENALEX", HttpStatus.INTERNAL_SERVER_ERROR.value());
            }
            
            if (response.results == null) {
                logger.warn("No results found in OpenAlex response");
                return new ArrayList<>();
            }
            
            logger.info("Found {} OpenAlex results", response.results.size());
            return response.results.stream()
                .map(this::convertToSourceContent)
                .collect(Collectors.toList());
                
        } catch (ApiException e) {
            logger.error("API error in OpenAlexSourceService: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error in OpenAlexSourceService: {}", e.getMessage(), e);
            throw new ApiException("Failed to fetch content from OpenAlex", "OPENALEX", HttpStatus.INTERNAL_SERVER_ERROR.value(), e);
        }
    }

    private SourceContent convertToSourceContent(OpenAlexResult result) {
        String title = result.title != null ? result.title : "No title";
        String abstractText = result.abstract_inverted_index != null ? 
            String.join(" ", result.abstract_inverted_index.values().stream().flatMap(List::stream).collect(Collectors.toList())) : "No abstract";
        String url = result.primary_location != null && result.primary_location.pdf != null ? result.primary_location.pdf : "#";
        String publishedDate = result.publication_date != null ? result.publication_date : "N/A";
        String authors = result.authorships != null ? 
            result.authorships.stream()
                .map(a -> a.author != null ? a.author.display_name : "Unknown")
                .collect(Collectors.joining(", ")) : "Unknown";
        
        return new SourceContent(
            title,
            abstractText,
            url,
            publishedDate,
            authors,
            RelevanceScorer.calculateRelevanceScore(title + " " + abstractText, title + " " + abstractText),
            "ACADEMIC_OPENALEX"
        );
    }

    @Override
    public double getRelevanceScore(String query, String content) {
        return RelevanceScorer.calculateRelevanceScore(query, content);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class OpenAlexResponse {
        public List<OpenAlexResult> results;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class OpenAlexResult {
        public String title;
        @JsonProperty("abstract_inverted_index")
        public Map<String, List<String>> abstract_inverted_index;
        @JsonProperty("primary_location")
        public PrimaryLocation primary_location;
        @JsonProperty("publication_date")
        public String publication_date;
        public List<Authorship> authorships;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class PrimaryLocation {
        public String pdf;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Authorship {
        public Author author;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Author {
        @JsonProperty("display_name")
        public String display_name;
    }
} 