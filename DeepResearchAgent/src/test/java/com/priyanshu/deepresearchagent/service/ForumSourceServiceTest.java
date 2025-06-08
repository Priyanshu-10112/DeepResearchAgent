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

class ForumSourceServiceTest {

    @Mock
    private ApiConfig apiConfig;
    
    @Mock
    private RestTemplate restTemplate;
    
    private ForumSourceService forumSourceService;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(apiConfig.getRedditClientId()).thenReturn("test-client-id");
        when(apiConfig.getRedditClientSecret()).thenReturn("test-client-secret");
        when(apiConfig.getRedditUsername()).thenReturn("test-username");
        when(apiConfig.getRedditPassword()).thenReturn("test-password");
        forumSourceService = new ForumSourceService(restTemplate, apiConfig);
    }
    
    @Test
    void testRedditApiConnection() {
        List<SourceContent> results = forumSourceService.searchContent("artificial intelligence", DynamicQuery.TimeRange.LAST_MONTH);
        assertNotNull(results);
        assertFalse(results.isEmpty(), "Should return some results");
        results.forEach(content -> {
            assertNotNull(content.getTitle());
            assertNotNull(content.getUrl());
            assertTrue(content.getRelevanceScore() >= 0.0 && content.getRelevanceScore() <= 1.0);
        });
    }
} 