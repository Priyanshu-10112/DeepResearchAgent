package com.priyanshu.deepresearchagent.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.priyanshu.deepresearchagent.config.ApiConfig;
import com.priyanshu.deepresearchagent.model.DynamicQuery;
import com.priyanshu.deepresearchagent.model.SourceContent;
import com.priyanshu.deepresearchagent.util.RelevanceScorer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class NewsSourceService implements DataSourceService {
    
    private static final Logger logger = LoggerFactory.getLogger(NewsSourceService.class);
    private final RestTemplate restTemplate;
    private final ApiConfig apiConfig;
    
    public NewsSourceService(RestTemplate restTemplate, ApiConfig apiConfig) {
        this.restTemplate = restTemplate;
        this.apiConfig = apiConfig;
    }
    
    @Override
    public List<SourceContent> fetchContent(String topic) {
        return searchContent(topic, DynamicQuery.TimeRange.LAST_MONTH);
    }

    @Override
    public double getRelevanceScore(String query, String content) {
        return RelevanceScorer.calculateRelevanceScore(query, content);
    }

    public List<SourceContent> searchContent(String query, DynamicQuery.TimeRange timeRange) {
        try {
            String fromDate = getFromDate(timeRange);
            
            String url = UriComponentsBuilder.newInstance()
                .scheme("https")
                .host("newsapi.org")
                .path("/v2/everything")
                .queryParam("q", query)
                .queryParam("from", fromDate)
                .queryParam("sortBy", "relevancy")
                .queryParam("language", "en")
                .queryParam("apiKey", apiConfig.getNewsApiKey())
                .build()
                .toUriString();
            
            logger.info("Fetching news from URL: {}", url);
            
            ResponseEntity<NewsApiResponse> response = restTemplate.getForEntity(url, NewsApiResponse.class);
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                NewsApiResponse newsResponse = response.getBody();
                if (newsResponse.articles != null) {
                    logger.info("Found {} news articles", newsResponse.articles.size());
                    return newsResponse.articles.stream()
                        .map(this::convertToSourceContent)
                        .toList();
                }
            } else {
                logger.error("News API returned status: {}", response.getStatusCode());
            }
        } catch (Exception e) {
            logger.error("Error fetching news: {}", e.getMessage(), e);
        }
        
        return new ArrayList<>();
    }
    
    private SourceContent convertToSourceContent(Article article) {
        return new SourceContent(
            article.title,
            article.description,
            article.url,
            article.publishedAt,
            article.author,
            calculateRelevanceScore(article),
            "NEWS"
        );
    }
    
    private double calculateRelevanceScore(Article article) {
        double score = 0.5; // Base score
        
        if (article.author != null) {
            score += 0.1;
        }
        
        if (article.description != null) {
            score += 0.2;
        }
        
        if (article.content != null) {
            score += 0.2;
        }
        
        return Math.min(score, 1.0);
    }
    
    private String getFromDate(DynamicQuery.TimeRange timeRange) {
        LocalDate now = LocalDate.now();
        LocalDate fromDate = switch (timeRange) {
            case LAST_DAY -> now.minusDays(1);
            case LAST_WEEK -> now.minusWeeks(1);
            case LAST_MONTH -> now.minusMonths(1);
            case LAST_YEAR -> now.minusYears(1);
            case ALL_TIME -> now.minusYears(5);
        };
        
        return fromDate.format(DateTimeFormatter.ISO_DATE);
    }
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class NewsApiResponse {
        @JsonProperty("articles")
        public List<Article> articles;
    }
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Article {
        public String title;
        public String description;
        public String url;
        @JsonProperty("publishedAt")
        public String publishedAt;
        public String author;
        public String content;
    }
}