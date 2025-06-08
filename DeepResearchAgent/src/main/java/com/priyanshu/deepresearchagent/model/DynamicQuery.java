package com.priyanshu.deepresearchagent.model;

import java.util.List;
import java.util.Map;

public class DynamicQuery {
    private String query;
    private List<Source> preferredSources;
    private Map<String, Object> parameters;
    private QueryType queryType;
    private TimeRange timeRange;

    public enum TimeRange {
        LAST_DAY,
        LAST_WEEK,
        LAST_MONTH,
        LAST_YEAR,
        ALL_TIME
    }

    public enum QueryType {
        IMMIGRATION_RULES,
        UNIVERSITY_CHANGES,
        COMPETITOR_ACTIVITY,
        GENERAL_RESEARCH
    }

    public DynamicQuery() {}

    public DynamicQuery(String query, List<Source> preferredSources, Map<String, Object> parameters, 
                       QueryType queryType, TimeRange timeRange) {
        this.query = query;
        this.preferredSources = preferredSources;
        this.parameters = parameters;
        this.queryType = queryType;
        this.timeRange = timeRange;
    }

    // Getters and setters
    public String getQuery() { return query; }
    public void setQuery(String query) { this.query = query; }
    
    public List<Source> getPreferredSources() { return preferredSources; }
    public void setPreferredSources(List<Source> preferredSources) { this.preferredSources = preferredSources; }
    
    public Map<String, Object> getParameters() { return parameters; }
    public void setParameters(Map<String, Object> parameters) { this.parameters = parameters; }
    
    public QueryType getQueryType() { return queryType; }
    public void setQueryType(QueryType queryType) { this.queryType = queryType; }
    
    public TimeRange getTimeRange() { return timeRange; }
    public void setTimeRange(TimeRange timeRange) { this.timeRange = timeRange; }
} 