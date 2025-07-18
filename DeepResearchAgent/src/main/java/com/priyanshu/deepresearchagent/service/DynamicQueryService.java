package com.priyanshu.deepresearchagent.service;

import com.priyanshu.deepresearchagent.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import com.priyanshu.deepresearchagent.service.BasicSummarizationService;

@Service
public class DynamicQueryService {
    private final NewsSourceService newsSourceService;
    private final AcademicSourceService academicSourceService;
    private final ForumSourceService forumSourceService;
    private final GNewsSourceService gNewsSourceService;
    private final BasicSummarizationService basicSummarizationService;

    public DynamicQueryService(
        NewsSourceService newsSourceService,
        AcademicSourceService academicSourceService,
        ForumSourceService forumSourceService,
        GNewsSourceService gNewsSourceService,
        BasicSummarizationService basicSummarizationService
    ) {
        this.newsSourceService = newsSourceService;
        this.academicSourceService = academicSourceService;
        this.forumSourceService = forumSourceService;
        this.gNewsSourceService = gNewsSourceService;
        this.basicSummarizationService = basicSummarizationService;
    }
    
    public ResearchResult processDynamicQuery(DynamicQuery query) {
        // Initialize result map
        Map<Source, List<SourceContent>> sourceContents = new HashMap<>();
        
        // Use preferred sources from the query, fallback to all sources if none specified
        List<Source> sourcesToProcess = query.getPreferredSources() != null && !query.getPreferredSources().isEmpty() 
            ? query.getPreferredSources() 
            : Arrays.asList(Source.values());
        
        // Process each source in parallel with timeout
        List<CompletableFuture<Map.Entry<Source, List<SourceContent>>>> futures = sourcesToProcess.stream()
            .<CompletableFuture<Map.Entry<Source, List<SourceContent>>>>map(source -> CompletableFuture.supplyAsync(() -> {
                try {
                    List<SourceContent> contents = switch (source) {
                        case NEWS -> {
                            List<SourceContent> newsResults = newsSourceService.searchContent(query.getQuery(), query.getTimeRange());
                            List<SourceContent> gnewsResults = gNewsSourceService.searchContent(query.getQuery(), query.getTimeRange());
                            // Set source label to NEWS for all GNews results
                            gnewsResults.forEach(c -> c.setSource("NEWS"));
                            List<SourceContent> combined = new ArrayList<>();
                            combined.addAll(newsResults);
                            combined.addAll(gnewsResults);
                            yield combined;
                        }
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

        // --- NEW: Aggregate all descriptions for summarization ---
        List<String> allDescriptions = sourceContents.values().stream()
            .flatMap(list -> list.stream())
            .map(SourceContent::getDescription)
            .filter(Objects::nonNull)
            .filter(desc -> !desc.isBlank())
            .collect(Collectors.toList());

        // Generate summary using BasicSummarizationService
        String summary = basicSummarizationService.generateSummary(query.getQuery(), allDescriptions);
        // --- END NEW ---
        
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