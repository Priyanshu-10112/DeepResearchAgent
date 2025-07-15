package com.priyanshu.deepresearchagent.service;

import com.priyanshu.deepresearchagent.model.ResearchResult;
import com.priyanshu.deepresearchagent.model.Source;
import com.priyanshu.deepresearchagent.model.SourceContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class ResearchService {
    private final Map<String, ResearchResult> savedReports = new ConcurrentHashMap<>();

    @Autowired
    private List<DataSourceService> dataSources;

    public ResearchResult researchTopic(String topic) {
        // Collect content from all sources
        Map<Source, List<SourceContent>> sourceContents = new HashMap<>();
        for (DataSourceService source : dataSources) {
            List<SourceContent> contents = source.fetchContent(topic);
            if (!contents.isEmpty()) {
                sourceContents.put(Source.valueOf(contents.get(0).getSource()), contents);
            }
        }

        // Generate summary using all collected content
        String summary = generateSummary(sourceContents);
        
        // Extract key findings
        List<String> keyFindings = extractKeyFindings(sourceContents);
        
        // Generate recommendations
        List<String> recommendations = generateRecommendations(sourceContents);
        
        // Calculate overall confidence score
        double confidenceScore = calculateConfidenceScore(sourceContents);

        return new ResearchResult(
            topic,
            summary,
            sourceContents,
            LocalDateTime.now(),
            confidenceScore,
            keyFindings,
            recommendations
        );
    }

    public ResearchResult saveReport(ResearchResult report) {
        String reportId = UUID.randomUUID().toString();
        savedReports.put(reportId, report);
        return report;
    }

    public List<ResearchResult> getSavedReports() {
        return new ArrayList<>(savedReports.values());
    }

    public void deleteReport(String reportId) {
        savedReports.remove(reportId);
    }

    private String generateSummary(Map<Source, List<SourceContent>> sourceContents) {
        StringBuilder summary = new StringBuilder();
        summary.append("🔍 Research Summary:\n\n");
        
        for (Map.Entry<Source, List<SourceContent>> entry : sourceContents.entrySet()) {
            summary.append(entry.getKey().name()).append(" Sources:\n");
            for (SourceContent content : entry.getValue()) {
                summary.append("- ").append(content.getTitle()).append("\n");
            }
            summary.append("\n");
        }
        
        return summary.toString();
    }

    private List<String> extractKeyFindings(Map<Source, List<SourceContent>> sourceContents) {
        return sourceContents.values().stream()
            .flatMap(List::stream)
            .map(SourceContent::getTitle)
            .collect(Collectors.toList());
    }

    private List<String> generateRecommendations(Map<Source, List<SourceContent>> sourceContents) {
        return Arrays.asList(
            "Monitor official government websites for updates",
            "Join relevant forums for real-time discussions",
            "Subscribe to news alerts for the topic"
        );
    }

    private double calculateConfidenceScore(Map<Source, List<SourceContent>> sourceContents) {
        return sourceContents.values().stream()
            .flatMap(List::stream)
            .mapToDouble(SourceContent::getRelevanceScore)
            .average()
            .orElse(0.0);
    }
} 