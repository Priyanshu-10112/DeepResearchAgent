package com.priyanshu.deepresearchagent.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.priyanshu.deepresearchagent.config.ApiConfig;
import com.priyanshu.deepresearchagent.model.DynamicQuery;
import com.priyanshu.deepresearchagent.model.SourceContent;
import com.priyanshu.deepresearchagent.util.RelevanceScorer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
public class ForumSourceService implements DataSourceService {
    
    private static final Logger logger = LoggerFactory.getLogger(ForumSourceService.class);
    private final RestTemplate restTemplate;
    private final ApiConfig apiConfig;
    private static final String REDDIT_AUTH_URL = "https://www.reddit.com/api/v1/access_token";
    private String accessToken;
    private long tokenExpiryTime;
    
    public ForumSourceService(RestTemplate restTemplate, ApiConfig apiConfig) {
        this.restTemplate = restTemplate;
        this.apiConfig = apiConfig;
    }
    
    @Override
    public List<SourceContent> fetchContent(String query) {
        return searchContent(query, DynamicQuery.TimeRange.LAST_MONTH);
    }

    @Override
    public double getRelevanceScore(String query, String content) {
        return RelevanceScorer.calculateRelevanceScore(query, content);
    }
    
    public List<SourceContent> searchContent(String query, DynamicQuery.TimeRange timeRange) {
        try {
            ensureValidToken();
            
            // Add subreddit filtering for better results
            String searchQuery = query;
            if (!query.toLowerCase().contains("subreddit:")) {
                searchQuery = query + " subreddit:science OR subreddit:technology OR subreddit:programming";
            }
            
            String url = UriComponentsBuilder.newInstance()
                .scheme("https")
                .host("oauth.reddit.com")
                .path("/r/all/search")
                .queryParam("q", searchQuery)
                .queryParam("t", getTimeRangeString(timeRange))
                .queryParam("sort", "relevance")
                .queryParam("limit", 10)
                .queryParam("type", "link")
                .build()
                .toUriString();
            
            logger.info("Fetching Reddit posts from URL: {}", url);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            headers.set("User-Agent", "DeepResearchAgent/1.0 (by /u/Grand-Rip-5870)");
            
            HttpEntity<?> entity = new HttpEntity<>(headers);
            
            logger.info("Making request with headers: {}", headers);
            
            ResponseEntity<RedditResponse> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                RedditResponse.class
            );
            
            logger.info("Response status: {}", response.getStatusCode());
            logger.info("Response headers: {}", response.getHeaders());
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                RedditResponse redditResponse = response.getBody();
                if (redditResponse.data != null && redditResponse.data.children != null) {
                    logger.info("Found {} Reddit posts", redditResponse.data.children.size());
                    return redditResponse.data.children.stream()
                        .map(child -> child.data)
                        .filter(post -> post.selftext != null && !post.selftext.isEmpty())
                        .map(this::convertToSourceContent)
                        .toList();
                } else {
                    logger.warn("No posts found in response");
                }
            } else {
                logger.error("Reddit API returned status: {}", response.getStatusCode());
                if (response.getStatusCode() == HttpStatus.FORBIDDEN) {
                    logger.error("Authentication may have failed. Attempting to re-authenticate...");
                    authenticate();
                }
            }
        } catch (Exception e) {
            logger.error("Error fetching Reddit posts: {}", e.getMessage(), e);
            if (e.getCause() != null) {
                logger.error("Caused by: {}", e.getCause().getMessage());
            }
        }
        
        return new ArrayList<>();
    }
    
    private void ensureValidToken() {
        if (accessToken == null || System.currentTimeMillis() >= tokenExpiryTime) {
            logger.info("Token expired or null, authenticating...");
            authenticate();
        } else {
            logger.info("Using existing token");
        }
    }
    
    private void authenticate() {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBasicAuth(apiConfig.getRedditClientId(), apiConfig.getRedditClientSecret());
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.set("User-Agent", "DeepResearchAgent/1.0 (by /u/Grand-Rip-5870)");
            
            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("grant_type", "password");
            body.add("username", apiConfig.getRedditUsername());
            body.add("password", apiConfig.getRedditPassword());
            
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
            
            logger.info("Authenticating with Reddit API");
            logger.info("Using client ID: {}", apiConfig.getRedditClientId());
            logger.info("Using username: {}", apiConfig.getRedditUsername());
            
            ResponseEntity<AuthResponse> response = restTemplate.postForEntity(
                REDDIT_AUTH_URL,
                request,
                AuthResponse.class
            );
            
            logger.info("Auth response status: {}", response.getStatusCode());
            logger.info("Auth response headers: {}", response.getHeaders());
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                AuthResponse authResponse = response.getBody();
                accessToken = authResponse.accessToken;
                tokenExpiryTime = System.currentTimeMillis() + (authResponse.expiresIn * 1000L);
                logger.info("Successfully authenticated with Reddit API");
                logger.info("Token expires in: {} seconds", authResponse.expiresIn);
            } else {
                logger.error("Failed to authenticate with Reddit API. Status: {}", response.getStatusCode());
                if (response.getBody() != null) {
                    logger.error("Error response: {}", response.getBody());
                }
            }
        } catch (Exception e) {
            logger.error("Error during Reddit authentication: {}", e.getMessage(), e);
            if (e.getCause() != null) {
                logger.error("Caused by: {}", e.getCause().getMessage());
            }
        }
    }
    
    private SourceContent convertToSourceContent(RedditPost post) {
        LocalDateTime createdDate = LocalDateTime.ofInstant(
            Instant.ofEpochSecond(post.createdUtc),
            ZoneId.systemDefault()
        );
        
        return new SourceContent(
            post.title,
            post.selftext,
            post.url,
            createdDate.toString(),
            post.author,
            getRelevanceScore(post.title, post.selftext),
            "FORUM"
        );
    }
    
    private double calculateRelevanceScore(RedditPost post) {
        double score = 0.5; // Base score
        
        if (post.selftext != null && !post.selftext.isEmpty()) {
            score += 0.2;
        }
        
        score += Math.min(post.score / 1000.0, 0.3);
        
        return Math.min(score, 1.0);
    }
    
    private String getTimeRangeString(DynamicQuery.TimeRange timeRange) {
        return switch (timeRange) {
            case LAST_DAY -> "day";
            case LAST_WEEK -> "week";
            case LAST_MONTH -> "month";
            case LAST_YEAR -> "year";
            case ALL_TIME -> "all";
        };
    }
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class RedditResponse {
        public RedditData data;
    }
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class RedditData {
        public List<RedditChild> children;
    }
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class RedditChild {
        public RedditPost data;
    }
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class RedditPost {
        public String title;
        public String selftext;
        public String url;
        @JsonProperty("created_utc")
        public long createdUtc;
        public String author;
        public int score;
    }
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class AuthResponse {
        @JsonProperty("access_token")
        public String accessToken;
        @JsonProperty("token_type")
        public String tokenType;
        @JsonProperty("expires_in")
        public int expiresIn;
        public String scope;
    }
} 