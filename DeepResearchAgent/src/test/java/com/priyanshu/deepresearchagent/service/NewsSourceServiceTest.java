package com.priyanshu.deepresearchagent.service;

import com.priyanshu.deepresearchagent.config.ApiConfig;
import com.priyanshu.deepresearchagent.model.DynamicQuery;
import com.priyanshu.deepresearchagent.model.SourceContent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class NewsSourceServiceTest {

    @Mock
    private ApiConfig apiConfig;
    
    @Mock
    private RestTemplate restTemplate;
    
    private NewsSourceService newsSourceService;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(apiConfig.getNewsApiKey()).thenReturn("test-api-key");
        when(apiConfig.getNewsApiUrl()).thenReturn("https://newsapi.org/v2");
        newsSourceService = new NewsSourceService(restTemplate, apiConfig);
    }
    
    @Test
    void testFetchContent() {
        List<SourceContent> results = newsSourceService.fetchContent("test query");
        assertNotNull(results);
        assertTrue(results.isEmpty()); // Should be empty due to mock
    }
    
    @Test
    void testGetRelevanceScore() {
        double score = newsSourceService.getRelevanceScore("test query", "This is a test query content");
        assertTrue(score >= 0.0 && score <= 1.0);
    }
    
    @Test
    void testNewsApiConnection() {
        List<SourceContent> results = newsSourceService.searchContent("artificial intelligence", DynamicQuery.TimeRange.LAST_MONTH);
        assertNotNull(results);
        assertFalse(results.isEmpty(), "Should return some results");
        results.forEach(content -> {
            assertNotNull(content.getTitle());
            assertNotNull(content.getUrl());
            assertTrue(content.getRelevanceScore() >= 0.0 && content.getRelevanceScore() <= 1.0);
        });
    }
} 