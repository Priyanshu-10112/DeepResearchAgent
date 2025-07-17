package com.priyanshu.deepresearchagent.service;

import com.priyanshu.deepresearchagent.model.SourceContent;
import com.priyanshu.deepresearchagent.model.DynamicQuery;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

@Service
public class GNewsSourceService {

    @Value("${gnews.api.key}")
    private String apiKey;

    public List<SourceContent> searchContent(String query, DynamicQuery.TimeRange timeRange) {
        List<SourceContent> results = new ArrayList<>();
        String url = "https://gnews.io/api/v4/search?q=" + query + "&token=" + apiKey + "&lang=en&max=10";
        RestTemplate restTemplate = new RestTemplate();
        try {
            String response = restTemplate.getForObject(url, String.class);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);
            JsonNode articles = root.get("articles");
            if (articles != null) {
                for (JsonNode article : articles) {
                    SourceContent content = new SourceContent(
                        article.hasNonNull("title") ? article.get("title").asText() : null,
                        article.hasNonNull("description") ? article.get("description").asText() : null,
                        article.hasNonNull("url") ? article.get("url").asText() : null,
                        article.hasNonNull("publishedAt") ? article.get("publishedAt").asText() : null,
                        article.hasNonNull("author") ? article.get("author").asText() : null,
                        0.0, // relevanceScore, can be set later
                        "GNEWS"
                    );
                    results.add(content);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }
} 