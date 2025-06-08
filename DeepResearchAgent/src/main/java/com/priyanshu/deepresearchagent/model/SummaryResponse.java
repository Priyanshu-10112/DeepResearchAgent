package com.priyanshu.deepresearchagent.model;

import java.util.List;

public class SummaryResponse {
    private String topic;
    private String overview;
    private List<String> keyInsights;
    private List<String> recommendations;
    private List<SourceContent> newsContent;
    private List<SourceContent> academicContent;
    private List<SourceContent> forumContent;
    private List<SourceContent> serpContent;
    private List<SourceContent> jsoupContent;
    private List<SourceContent> openAlexContent;

    public SummaryResponse() {
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public List<String> getKeyInsights() {
        return keyInsights;
    }

    public void setKeyInsights(List<String> keyInsights) {
        this.keyInsights = keyInsights;
    }

    public List<String> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(List<String> recommendations) {
        this.recommendations = recommendations;
    }

    public List<SourceContent> getNewsContent() {
        return newsContent;
    }

    public void setNewsContent(List<SourceContent> newsContent) {
        this.newsContent = newsContent;
    }

    public List<SourceContent> getAcademicContent() {
        return academicContent;
    }

    public void setAcademicContent(List<SourceContent> academicContent) {
        this.academicContent = academicContent;
    }

    public List<SourceContent> getForumContent() {
        return forumContent;
    }

    public void setForumContent(List<SourceContent> forumContent) {
        this.forumContent = forumContent;
    }

    public List<SourceContent> getSerpContent() {
        return serpContent;
    }

    public void setSerpContent(List<SourceContent> serpContent) {
        this.serpContent = serpContent;
    }

    public List<SourceContent> getJsoupContent() {
        return jsoupContent;
    }

    public void setJsoupContent(List<SourceContent> jsoupContent) {
        this.jsoupContent = jsoupContent;
    }

    public List<SourceContent> getOpenAlexContent() {
        return openAlexContent;
    }

    public void setOpenAlexContent(List<SourceContent> openAlexContent) {
        this.openAlexContent = openAlexContent;
    }
}
