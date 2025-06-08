package com.priyanshu.deepresearchagent.util;

import org.springframework.stereotype.Component;

@Component
public class RelevanceScorer {
    
    /**
     * Calculates a relevance score between a query and content based on term matching.
     * @param query The search query
     * @param content The content to score against
     * @return A score between 0 and 1, where 1 means all query terms are found
     */
    public static double calculateRelevanceScore(String query, String content) {
        if (query == null || content == null || query.trim().isEmpty() || content.trim().isEmpty()) {
            return 0.0;
        }
        
        String[] queryTerms = query.toLowerCase().split("\\s+");
        String contentLower = content.toLowerCase();
        int matches = 0;
        
        for (String term : queryTerms) {
            if (contentLower.contains(term)) {
                matches++;
            }
        }
        
        return (double) matches / queryTerms.length;
    }
} 