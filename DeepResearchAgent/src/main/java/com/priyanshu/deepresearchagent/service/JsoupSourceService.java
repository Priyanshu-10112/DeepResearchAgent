package com.priyanshu.deepresearchagent.service;

import com.priyanshu.deepresearchagent.model.SourceContent;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class JsoupSourceService implements DataSourceService {

    private static final Logger logger = LoggerFactory.getLogger(JsoupSourceService.class);

    @Override
    public List<SourceContent> fetchContent(String url) {
        if (url == null || url.trim().isEmpty()) {
            logger.warn("Jsoup fetchContent received an empty or null URL.");
            return Collections.emptyList();
        }

        try {
            logger.info("Attempting to scrape content from URL: {}", url);
            Document doc = Jsoup.connect(url)
                                .timeout(10000) // 10 seconds timeout
                                .get();

            String title = doc.title();
            String content = doc.body().text(); // Extract all text from the body

            // For forum-specific scraping, you might want to target specific elements
            // Example: Select specific forum post elements if you know their CSS selectors
            // Element forumPost = doc.select("div.forum-post").first();
            // if (forumPost != null) { content = forumPost.text(); }

            // Basic relevance score (can be improved based on actual query)
            double relevanceScore = getRelevanceScore("", content); // No specific query here for now

            SourceContent sourceContent = new SourceContent(
                    title,
                    content,
                    url,
                    "N/A", // Published date (Jsoup doesn't provide easily)
                    "N/A", // Author (Jsoup doesn't provide easily)
                    relevanceScore,
                    "JSOUP"
            );
            logger.info("Successfully scraped content from URL: {}", url);
            return Collections.singletonList(sourceContent);

        } catch (Exception e) {
            logger.error("Error scraping content from URL '{}': {}", url, e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    public double getRelevanceScore(String query, String content) {
        // This is a basic placeholder. For Jsoup, relevance might be more about
        // whether the URL itself is relevant, or if specific keywords appear within the scraped content.
        if (query == null || query.isEmpty()) {
            return 0.5; // Default relevance if no query is provided
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