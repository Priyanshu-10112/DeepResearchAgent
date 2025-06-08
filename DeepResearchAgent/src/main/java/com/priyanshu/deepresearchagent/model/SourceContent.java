package com.priyanshu.deepresearchagent.model;

public class SourceContent {
    private String title;
    private String description;
    private String url;
    private String publishedAt;
    private String author;
    private double relevanceScore;
    private String source;

    public SourceContent(String title, String description, String url, String publishedAt, 
                        String author, double relevanceScore, String source) {
        this.title = title;
        this.description = description;
        this.url = url;
        this.publishedAt = publishedAt;
        this.author = author;
        this.relevanceScore = relevanceScore;
        this.source = source;
    }

    // Getters and setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    
    public String getPublishedAt() { return publishedAt; }
    public void setPublishedAt(String publishedAt) { this.publishedAt = publishedAt; }
    
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    
    public double getRelevanceScore() { return relevanceScore; }
    public void setRelevanceScore(double relevanceScore) { this.relevanceScore = relevanceScore; }
    
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
} 