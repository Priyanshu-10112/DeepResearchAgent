package com.priyanshu.deepresearchagent.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiConfig {
    
    @Value("${reddit.client.id}")
    private String redditClientId;
    
    @Value("${reddit.client.secret}")
    private String redditClientSecret;
    
    @Value("${reddit.username}")
    private String redditUsername;
    
    @Value("${reddit.password}")
    private String redditPassword;
    
    @Value("${arxiv.api.base-url}")
    private String arxivApiBaseUrl;
    
    @Value("${arxiv.api.max-results}")
    private int arxivMaxResults;
    
    @Value("${news.api.key}")
    private String newsApiKey;
    
    @Value("${news.api.url}")
    private String newsApiUrl;
    
    @Value("${serp.api.key}")
    private String serpApiKey;
    
    @Value("${serp.api.url}")
    private String serpApiUrl;
    
    @Value("${openalex.api.url}")
    private String openAlexApiUrl;
    
    @Value("${openalex.api.mailto}")
    private String openAlexMailto;
    
    public String getRedditClientId() {
        return redditClientId;
    }
    
    public String getRedditClientSecret() {
        return redditClientSecret;
    }
    
    public String getRedditUsername() {
        return redditUsername;
    }
    
    public String getRedditPassword() {
        return redditPassword;
    }
    
    public String getArxivApiBaseUrl() {
        return arxivApiBaseUrl;
    }
    
    public int getArxivMaxResults() {
        return arxivMaxResults;
    }
    
    public String getNewsApiKey() {
        return newsApiKey;
    }
    
    public String getNewsApiUrl() {
        return newsApiUrl;
    }
    
    public String getSerpApiKey() {
        return serpApiKey;
    }
    
    public String getSerpApiUrl() {
        return serpApiUrl;
    }
    
    public String getOpenAlexApiUrl() {
        return openAlexApiUrl;
    }
    
    public String getOpenAlexMailto() {
        return openAlexMailto;
    }
} 