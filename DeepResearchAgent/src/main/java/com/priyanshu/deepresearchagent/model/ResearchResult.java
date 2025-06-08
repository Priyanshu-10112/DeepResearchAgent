package com.priyanshu.deepresearchagent.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class ResearchResult {
    private String topic;
    private String summary;
    private Map<Source, List<SourceContent>> sourceContents;
    private LocalDateTime timestamp;
    private double confidenceScore;
    private List<String> keyFindings;
    private List<String> recommendations;

    public ResearchResult(String topic, String summary, Map<Source, List<SourceContent>> sourceContents,
                         LocalDateTime timestamp, double confidenceScore, List<String> keyFindings,
                         List<String> recommendations) {
        this.topic = topic;
        this.summary = summary;
        this.sourceContents = sourceContents;
        this.timestamp = timestamp;
        this.confidenceScore = confidenceScore;
        this.keyFindings = keyFindings;
        this.recommendations = recommendations;
    }

    // Getters and setters
    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public Map<Source, List<SourceContent>> getSourceContents() { return sourceContents; }
    public void setSourceContents(Map<Source, List<SourceContent>> sourceContents) { this.sourceContents = sourceContents; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public double getConfidenceScore() { return confidenceScore; }
    public void setConfidenceScore(double confidenceScore) { this.confidenceScore = confidenceScore; }
    public List<String> getKeyFindings() { return keyFindings; }
    public void setKeyFindings(List<String> keyFindings) { this.keyFindings = keyFindings; }
    public List<String> getRecommendations() { return recommendations; }
    public void setRecommendations(List<String> recommendations) { this.recommendations = recommendations; }
} 