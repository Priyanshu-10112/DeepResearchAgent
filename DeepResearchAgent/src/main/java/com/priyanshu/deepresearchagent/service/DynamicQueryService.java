package com.priyanshu.deepresearchagent.service;

import com.priyanshu.deepresearchagent.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class DynamicQueryService {
    
    @Autowired
    private NewsSourceService newsSourceService;
    
    @Autowired
    private AcademicSourceService academicSourceService;
    
    @Autowired
    private ForumSourceService forumSourceService;

    public ResearchResult processDynamicQuery(DynamicQuery query) {
        // Initialize result map
        Map<Source, List<SourceContent>> sourceContents = new HashMap<>();
        
        // Use all available sources instead of preferred sources
        List<Source> allSources = Arrays.asList(Source.values());
        
        // Process each source in parallel with timeout
        List<CompletableFuture<Map.Entry<Source, List<SourceContent>>>> futures = allSources.stream()
            .<CompletableFuture<Map.Entry<Source, List<SourceContent>>>>map(source -> CompletableFuture.supplyAsync(() -> {
                try {
                    List<SourceContent> contents = switch (source) {
                        case NEWS -> newsSourceService.searchContent(query.getQuery(), query.getTimeRange());
                        case ACADEMIC -> academicSourceService.searchContent(query.getQuery(), query.getTimeRange());
                        case FORUM -> forumSourceService.searchContent(query.getQuery(), query.getTimeRange());
                    };
                    return new AbstractMap.SimpleEntry<>(source, contents);
                } catch (Exception e) {
                    return new AbstractMap.SimpleEntry<>(source, new ArrayList<SourceContent>());
                }
            }))
            .collect(Collectors.toList());

        // Wait for all futures to complete with timeout
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
            .orTimeout(90, TimeUnit.SECONDS)
            .join();

        // Collect results
        futures.forEach(future -> {
            try {
                Map.Entry<Source, List<SourceContent>> result = future.get(5, TimeUnit.SECONDS);
                sourceContents.put(result.getKey(), result.getValue());
            } catch (Exception e) {
                // Log error but continue with other results
                System.err.println("Error processing source: " + e.getMessage());
            }
        });

        // Generate summary based on query type
        String summary = generateSummary(query, sourceContents);
        
        // Extract key findings
        List<String> keyFindings = extractKeyFindings(sourceContents, query.getQueryType());
        
        // Generate recommendations
        List<String> recommendations = generateRecommendations(query, sourceContents);

        // Calculate confidence score
        double confidenceScore = calculateConfidenceScore(sourceContents);

        return new ResearchResult(
            query.getQuery(),
            summary,
            sourceContents,
            LocalDateTime.now(),
            confidenceScore,
            keyFindings,
            recommendations
        );
    }

    private String generateSummary(DynamicQuery query, Map<Source, List<SourceContent>> sourceContents) {
        StringBuilder summary = new StringBuilder();
        
        // Count total sources and articles
        int totalSources = sourceContents.size();
        int totalArticles = sourceContents.values().stream()
            .mapToInt(List::size)
            .sum();

        // Start with a brief overview
        summary.append(String.format("Analysis of %d articles from %d sources reveals ", totalArticles, totalSources));

        // Collect key findings from each source
        Map<Source, Optional<SourceContent>> mostRelevantBySource = sourceContents.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                e -> e.getValue().stream()
                    .max(Comparator.comparingDouble(SourceContent::getRelevanceScore))
            ));

        // Build a cohesive paragraph
        List<String> keyPoints = new ArrayList<>();
        
        mostRelevantBySource.forEach((source, contentOpt) -> {
            contentOpt.ifPresent(content -> {
                String sourceType = switch (source) {
                    case NEWS -> "news reports";
                    case ACADEMIC -> "academic research";
                    case FORUM -> "community discussions";
                };
                keyPoints.add(String.format("%s indicates %s", sourceType, content.getTitle()));
            });
        });

        // Combine key points into a paragraph
        if (!keyPoints.isEmpty()) {
            summary.append(String.join(". ", keyPoints));
        }

        // Add a brief conclusion
        summary.append(". This information suggests ");
        switch (query.getQueryType()) {
            case IMMIGRATION_RULES -> summary.append("significant developments in immigration policies");
            case UNIVERSITY_CHANGES -> summary.append("important changes in university regulations");
            case COMPETITOR_ACTIVITY -> summary.append("notable movements in competitor strategies");
            default -> summary.append("relevant updates in the research area");
        }
        summary.append(".");

        return summary.toString();
    }

    private List<String> extractKeyFindings(Map<Source, List<SourceContent>> sourceContents, 
                                          DynamicQuery.QueryType queryType) {
        List<String> findings = new ArrayList<>();
        
        // Extract findings based on query type and source contents
        sourceContents.forEach((source, contents) -> {
            contents.forEach(content -> {
                // Add source-specific key findings extraction logic
                findings.add(content.getTitle());
            });
        });

        return findings;
    }

    private List<String> generateRecommendations(DynamicQuery query, 
                                               Map<Source, List<SourceContent>> sourceContents) {
        List<String> recommendations = new ArrayList<>();
        
        // Generate recommendations based on query type and findings
        switch (query.getQueryType()) {
            case IMMIGRATION_RULES -> {
                recommendations.add("Monitor official immigration websites for updates");
                recommendations.add("Consult with immigration experts for personalized advice");
            }
            case UNIVERSITY_CHANGES -> {
                recommendations.add("Check university websites for detailed policy changes");
                recommendations.add("Contact university admissions offices for clarification");
            }
            case COMPETITOR_ACTIVITY -> {
                recommendations.add("Set up alerts for competitor announcements");
                recommendations.add("Regularly review competitor websites and social media");
            }
            default -> {
                recommendations.add("Continue monitoring relevant sources for updates");
                recommendations.add("Consider setting up automated alerts for new information");
            }
        }

        return recommendations;
    }

    private double calculateConfidenceScore(Map<Source, List<SourceContent>> sourceContents) {
        // Calculate confidence score based on:
        // - Number of sources
        // - Recency of content
        // - Source reliability
        // - Content consistency
        
        double score = 0.0;
        int totalSources = sourceContents.size();
        int totalContents = sourceContents.values().stream()
            .mapToInt(List::size)
            .sum();

        if (totalSources > 0 && totalContents > 0) {
            score = (double) totalContents / (totalSources * 5); // Normalize to 0-1 range
        }

        return Math.min(score, 1.0);
    }
} 