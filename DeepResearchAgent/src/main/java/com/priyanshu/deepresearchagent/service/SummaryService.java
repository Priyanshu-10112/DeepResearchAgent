package com.priyanshu.deepresearchagent.service;

import com.priyanshu.deepresearchagent.model.SourceContent;
import com.priyanshu.deepresearchagent.model.SummaryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SummaryService {
    
    private static final Logger logger = LoggerFactory.getLogger(SummaryService.class);
    private final NewsSourceService newsSourceService;
    private final AcademicSourceService academicSourceService;
    private final ForumSourceService forumSourceService;
    private final SerpSourceService serpSourceService;
    private final OpenAlexSourceService openAlexSourceService;
    private final BasicSummarizationService basicSummarizationService;
    
    public SummaryService(NewsSourceService newsSourceService,
                         AcademicSourceService academicSourceService,
                         ForumSourceService forumSourceService,
                         SerpSourceService serpSourceService,
                         OpenAlexSourceService openAlexSourceService,
                         BasicSummarizationService basicSummarizationService) {
        this.newsSourceService = newsSourceService;
        this.academicSourceService = academicSourceService;
        this.forumSourceService = forumSourceService;
        this.serpSourceService = serpSourceService;
        this.openAlexSourceService = openAlexSourceService;
        this.basicSummarizationService = basicSummarizationService;
    }
    
    public SummaryResponse generateSummary(String topic) {
        logger.info("Generating summary for topic: {}", topic);
        
        List<SourceContent> newsContent = fetchContentSafely(() -> 
            newsSourceService.fetchContent(topic), "news");
        List<SourceContent> academicContent = fetchContentSafely(() -> 
            academicSourceService.fetchContent(topic), "academic");
        List<SourceContent> forumContent = fetchContentSafely(() -> 
            forumSourceService.fetchContent(topic), "forum");
        List<SourceContent> serpContent = fetchContentSafely(() -> 
            serpSourceService.fetchContent(topic), "SerpAPI");
        List<SourceContent> openAlexContent = fetchContentSafely(() -> 
            openAlexSourceService.fetchContent(topic), "OpenAlex");
        
        // Combine all content for basic summarization
        List<String> allContent = new ArrayList<>();
        allContent.addAll(newsContent.stream().map(content -> content.getDescription()).collect(Collectors.toList()));
        allContent.addAll(academicContent.stream().map(content -> content.getDescription()).collect(Collectors.toList()));
        allContent.addAll(forumContent.stream().map(content -> content.getDescription()).collect(Collectors.toList()));
        allContent.addAll(serpContent.stream().map(content -> content.getDescription()).collect(Collectors.toList()));
        allContent.addAll(openAlexContent.stream().map(content -> content.getDescription()).collect(Collectors.toList()));
        
        // Generate summary using basic summarization
        String overview = basicSummarizationService.generateSummary(topic, allContent);
        
        // Extract key insights and recommendations from basic summarization's summary
        List<String> keyInsights = extractKeyInsights(overview);
        List<String> recommendations = extractRecommendations(overview);
        
        SummaryResponse response = new SummaryResponse();
        response.setTopic(topic);
        response.setOverview(overview);
        response.setKeyInsights(keyInsights);
        response.setRecommendations(recommendations);
        response.setNewsContent(newsContent);
        response.setAcademicContent(academicContent);
        response.setForumContent(forumContent);
        response.setSerpContent(serpContent);
        response.setJsoupContent(new ArrayList<>());
        response.setOpenAlexContent(openAlexContent);
        
        logger.info("Summary generated successfully with {} news, {} academic, {} forum, {} SerpAPI, and {} OpenAlex items",
            newsContent.size(), academicContent.size(), forumContent.size(), serpContent.size(), openAlexContent.size());
        
        return response;
    }
    
    private List<String> extractKeyInsights(String summary) {
        List<String> insights = new ArrayList<>();
        String[] lines = summary.split("\n");
        boolean inInsightsSection = false;
        
        for (String line : lines) {
            if (line.contains("Key Insights") || line.contains("Main Findings")) {
                inInsightsSection = true;
                continue;
            }
            if (inInsightsSection && line.trim().startsWith("-")) {
                insights.add(line.trim().substring(1).trim());
            }
            if (inInsightsSection && line.contains("Recommendations")) {
                break;
            }
        }
        
        return insights.isEmpty() ? List.of("No key insights available.") : insights;
    }
    
    private List<String> extractRecommendations(String summary) {
        List<String> recommendations = new ArrayList<>();
        String[] lines = summary.split("\n");
        boolean inRecommendationsSection = false;
        
        for (String line : lines) {
            if (line.contains("Recommendations") || line.contains("Next Steps")) {
                inRecommendationsSection = true;
                continue;
            }
            if (inRecommendationsSection && line.trim().startsWith("-")) {
                recommendations.add(line.trim().substring(1).trim());
            }
        }
        
        return recommendations.isEmpty() ? List.of("No specific recommendations available.") : recommendations;
    }
    
    private List<SourceContent> fetchContentSafely(ContentFetcher fetcher, String sourceType) {
        try {
            List<SourceContent> content = fetcher.fetch();
            logger.info("Successfully fetched {} {} items", content.size(), sourceType);
            return content;
        } catch (Exception e) {
            logger.error("Error fetching {} content: {}", sourceType, e.getMessage());
            return new ArrayList<>();
        }
    }
    
    @FunctionalInterface
    private interface ContentFetcher {
        List<SourceContent> fetch();
    }
}
