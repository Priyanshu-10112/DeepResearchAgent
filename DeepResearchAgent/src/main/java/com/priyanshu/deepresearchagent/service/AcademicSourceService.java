package com.priyanshu.deepresearchagent.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.priyanshu.deepresearchagent.config.ApiConfig;
import com.priyanshu.deepresearchagent.model.DynamicQuery;
import com.priyanshu.deepresearchagent.model.SourceContent;
import com.priyanshu.deepresearchagent.util.RelevanceScorer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class AcademicSourceService implements DataSourceService {
    
    private static final Logger logger = LoggerFactory.getLogger(AcademicSourceService.class);
    private final RestTemplate restTemplate;
    private final ApiConfig apiConfig;
    private static final int MAX_RETRIES = 3;
    private static final int RETRY_DELAY_MS = 1000;
    
    public AcademicSourceService(RestTemplate restTemplate, ApiConfig apiConfig) {
        this.restTemplate = restTemplate;
        this.apiConfig = apiConfig;
        configureRestTemplate();
    }
    
    private void configureRestTemplate() {
        MappingJackson2XmlHttpMessageConverter converter = new MappingJackson2XmlHttpMessageConverter();
        converter.setSupportedMediaTypes(Arrays.asList(
            MediaType.APPLICATION_XML,
            MediaType.TEXT_XML,
            MediaType.APPLICATION_ATOM_XML
        ));
        restTemplate.getMessageConverters().add(converter);
    }
    
    public List<SourceContent> searchContent(String query, DynamicQuery.TimeRange timeRange) {
        int retryCount = 0;
        while (retryCount < MAX_RETRIES) {
            try {
                String url = UriComponentsBuilder.newInstance()
                    .scheme("https")
                    .host("export.arxiv.org")
                    .path("/api/query")
                    .queryParam("search_query", "all:" + query)
                    .queryParam("start", 0)
                    .queryParam("max_results", apiConfig.getArxivMaxResults())
                    .queryParam("sortBy", "relevance")
                    .queryParam("sortOrder", "descending")
                    .build()
                    .toUriString();
                
                logger.info("Fetching papers from URL: {} (Attempt {}/{})", url, retryCount + 1, MAX_RETRIES);
                
                // Add delay between retries
                if (retryCount > 0) {
                    int delay = RETRY_DELAY_MS * (int)Math.pow(2, retryCount); // Exponential backoff
                    logger.info("Waiting {} ms before retry...", delay);
                    Thread.sleep(delay);
                }
                
                ArxivResponse response = restTemplate.getForObject(url, ArxivResponse.class);
                
                if (response != null && response.entries != null) {
                    logger.info("Found {} papers", response.entries.size());
                    return response.entries.stream()
                        .map(this::convertToSourceContent)
                        .toList();
                }
                
                return new ArrayList<>();
                
            } catch (Exception e) {
                retryCount++;
                logger.error("Error fetching arXiv papers (Attempt {}/{}): {}", 
                    retryCount, MAX_RETRIES, e.getMessage());
                
                if (retryCount >= MAX_RETRIES) {
                    logger.error("Failed to fetch arXiv papers after {} attempts", MAX_RETRIES);
                    return new ArrayList<>();
                }
            }
        }
        
        return new ArrayList<>();
    }
    
    private SourceContent convertToSourceContent(ArxivEntry entry) {
        return new SourceContent(
            entry.title,
            entry.summary,
            entry.id,
            entry.published,
            entry.authors.stream()
                .map(author -> author.name)
                .reduce((a, b) -> a + ", " + b)
                .orElse("Unknown Author"),
            calculateRelevanceScore(entry),
            "ACADEMIC"
        );
    }
    
    private double calculateRelevanceScore(ArxivEntry entry) {
        double score = 0.5; // Base score
        
        if (entry.summary != null && !entry.summary.isEmpty()) {
            score += 0.2;
        }
        
        if (entry.authors != null && !entry.authors.isEmpty()) {
            score += 0.1;
        }
        
        if (entry.categories != null && !entry.categories.isEmpty()) {
            score += 0.2;
        }
        
        return Math.min(score, 1.0);
    }
    
    @Override
    public List<SourceContent> fetchContent(String query) {
        return searchContent(query, DynamicQuery.TimeRange.LAST_MONTH);
    }

    @Override
    public double getRelevanceScore(String query, String content) {
        return RelevanceScorer.calculateRelevanceScore(query, content);
    }
    
    @JacksonXmlRootElement(localName = "feed")
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class ArxivResponse {
        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "entry")
        public List<ArxivEntry> entries;
    }
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class ArxivEntry {
        public String id;
        public String title;
        public String summary;
        public String published;
        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "author")
        public List<Author> authors;
        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "category")
        public List<String> categories;
    }
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Author {
        public String name;
    }
} 