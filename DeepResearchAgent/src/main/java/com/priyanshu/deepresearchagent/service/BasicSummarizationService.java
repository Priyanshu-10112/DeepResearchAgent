package com.priyanshu.deepresearchagent.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.regex.Pattern;

@Service
public class BasicSummarizationService {
    private static final Logger logger = LoggerFactory.getLogger(BasicSummarizationService.class);
    private static final int MIN_SENTENCE_LENGTH = 20;
    private static final int MAX_SENTENCE_LENGTH = 200;
    private static final Pattern SENTENCE_PATTERN = Pattern.compile("[.!?]+");
    private static final Pattern CLEANUP_PATTERN = Pattern.compile("\\s+");
    
    private static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList(
        "the", "be", "to", "of", "and", "a", "in", "that", "have", "i", "it", "for", "not", "on", "with", "he",
        "as", "you", "do", "at", "this", "but", "his", "by", "from", "they", "we", "say", "her", "she", "or",
        "an", "will", "my", "one", "all", "would", "there", "their", "what", "so", "up", "out", "if", "about",
        "who", "get", "which", "go", "me", "when", "make", "can", "like", "time", "no", "just", "him", "know",
        "take", "people", "into", "year", "your", "good", "some", "could", "them", "see", "other", "than",
        "then", "now", "look", "only", "come", "its", "over", "think", "also", "back", "after", "use", "two",
        "how", "our", "work", "first", "well", "way", "even", "new", "want", "because", "any", "these", "give",
        "day", "most", "us", "very", "much", "many", "more", "most", "such", "both", "each", "few", "more",
        "most", "other", "some", "such", "no", "nor", "not", "only", "own", "same", "so", "than", "too", "very"
    ));

    private static final Set<String> INSIGHT_INDICATORS = new HashSet<>(Arrays.asList(
        "shows", "indicates", "suggests", "reveals", "demonstrates", "proves", "confirms",
        "highlights", "emphasizes", "points to", "signals", "reflects", "represents",
        "important", "significant", "crucial", "key", "major", "notable", "remarkable",
        "findings", "results", "analysis", "study", "research", "evidence", "data",
        "trend", "pattern", "development", "impact", "effect", "influence", "factor"
    ));

    private static final Set<String> RECOMMENDATION_INDICATORS = new HashSet<>(Arrays.asList(
        "should", "must", "need", "recommend", "suggest", "propose", "advise",
        "consider", "implement", "develop", "create", "establish", "improve",
        "enhance", "optimize", "focus", "prioritize", "address", "resolve",
        "strategy", "approach", "solution", "action", "plan", "initiative",
        "opportunity", "potential", "future", "next", "step", "way", "forward"
    ));

    public String generateSummary(String topic, List<String> contentList) {
        try {
            // Combine and preprocess content
            String combinedContent = preprocessContent(contentList);
            
            // Extract key phrases
            List<String> keyPhrases = extractKeyPhrases(combinedContent);
            
            // Generate different sections
            List<String> summaryPoints = generateSummaryPoints(combinedContent, keyPhrases);
            List<String> insights = generateInsights(combinedContent, keyPhrases);
            List<String> recommendations = generateRecommendations(combinedContent, keyPhrases);

            // Format the complete summary
            return formatSummary(topic, summaryPoints, insights, recommendations);

        } catch (Exception e) {
            logger.error("Error generating basic summary: {}", e.getMessage(), e);
            return generateFallbackSummary(topic, contentList);
        }
    }

    private String preprocessContent(List<String> contentList) {
        return contentList.stream()
            .filter(content -> content != null && !content.trim().isEmpty())
            .map(this::cleanText)
            .collect(Collectors.joining("\n"));
    }

    private String cleanText(String text) {
        // Remove URLs
        text = text.replaceAll("https?://\\S+\\s?", "");
        // Remove special characters but keep sentence endings
        text = text.replaceAll("[^a-zA-Z0-9\\s.,!?]", " ");
        // Remove multiple spaces
        text = CLEANUP_PATTERN.matcher(text).replaceAll(" ").trim();
        return text;
    }

    private List<String> extractKeyPhrases(String content) {
        List<String> sentences = splitIntoSentences(content);
        Map<String, Double> wordFrequency = calculateWordFrequency(content);
        
        return sentences.stream()
            .map(sentence -> {
                String[] words = sentence.toLowerCase().split("\\s+");
                StringBuilder phrase = new StringBuilder();
                double phraseScore = 0.0;
                int wordCount = 0;

                for (String word : words) {
                    word = word.replaceAll("[^a-zA-Z]", "");
                    if (!STOP_WORDS.contains(word) && word.length() > 3) {
                        phrase.append(word).append(" ");
                        phraseScore += wordFrequency.getOrDefault(word, 0.0);
                        wordCount++;
                    }
                }

                return wordCount > 0 && phraseScore / wordCount > 0.5 ? 
                    phrase.toString().trim() : null;
            })
            .filter(Objects::nonNull)
            .distinct()
            .collect(Collectors.toList());
    }

    private List<String> generateSummaryPoints(String content, List<String> keyPhrases) {
        List<String> sentences = splitIntoSentences(content);
        Map<String, Double> sentenceScores = scoreSentences(sentences, keyPhrases, null);

        return selectTopSentences(sentences, sentenceScores, 5)
            .stream()
            .map(this::formatAsPoint)
            .collect(Collectors.toList());
    }

    private List<String> generateInsights(String content, List<String> keyPhrases) {
        List<String> sentences = splitIntoSentences(content);
        Map<String, Double> sentenceScores = scoreSentences(sentences, keyPhrases, INSIGHT_INDICATORS);

        return selectTopSentences(sentences, sentenceScores, 3)
            .stream()
            .map(this::formatAsPoint)
            .collect(Collectors.toList());
    }

    private List<String> generateRecommendations(String content, List<String> keyPhrases) {
        List<String> sentences = splitIntoSentences(content);
        Map<String, Double> sentenceScores = scoreSentences(sentences, keyPhrases, RECOMMENDATION_INDICATORS);

        return selectTopSentences(sentences, sentenceScores, 3)
            .stream()
            .map(this::formatAsPoint)
            .collect(Collectors.toList());
    }

    private List<String> splitIntoSentences(String content) {
        return Arrays.stream(SENTENCE_PATTERN.split(content))
            .map(String::trim)
            .filter(s -> s.length() >= MIN_SENTENCE_LENGTH && s.length() <= MAX_SENTENCE_LENGTH)
            .collect(Collectors.toList());
    }

    private Map<String, Double> scoreSentences(List<String> sentences, List<String> keyPhrases, Set<String> indicators) {
        Map<String, Double> sentenceScores = new HashMap<>();
        
        for (String sentence : sentences) {
            double score = 0.0;
            String lowerSentence = sentence.toLowerCase();
            
            // Score based on key phrases
            for (String phrase : keyPhrases) {
                if (lowerSentence.contains(phrase.toLowerCase())) {
                    score += 1.0;
                }
            }
            
            // Score based on indicators if provided
            if (indicators != null) {
                for (String indicator : indicators) {
                    if (lowerSentence.contains(indicator)) {
                        score += 0.5;
                    }
                }
            }
            
            // Penalize very long sentences
            if (sentence.length() > 100) {
                score *= 0.8;
            }
            
            sentenceScores.put(sentence, score);
        }
        
        return sentenceScores;
    }

    private List<String> selectTopSentences(List<String> sentences, Map<String, Double> sentenceScores, int limit) {
        return sentenceScores.entrySet().stream()
            .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
            .limit(limit)
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
    }

    private String formatAsPoint(String sentence) {
        // Remove unnecessary words and clean up the sentence
        String point = sentence.replaceAll("^(The|This|That|These|Those|It|They|We|You|I)\\s+", "");
        point = point.replaceAll("\\s+", " ").trim();
        
        // Remove redundant phrases
        point = point.replaceAll("(it is|this is|that is|these are|those are)\\s+", "");
        
        // Ensure the point starts with a capital letter
        if (!point.isEmpty()) {
            point = point.substring(0, 1).toUpperCase() + point.substring(1);
        }
        
        // Add period if missing
        if (!point.endsWith(".")) {
            point += ".";
        }
        
        return point;
    }

    private String formatSummary(String topic, List<String> summaryPoints, List<String> insights, List<String> recommendations) {
        StringBuilder summary = new StringBuilder();
        
        summary.append("Key Points Summary:\n\n");
        for (String point : summaryPoints) {
            summary.append("• ").append(point).append("\n");
        }

        summary.append("\nKey Insights:\n\n");
        for (String insight : insights) {
            summary.append("• ").append(insight).append("\n");
        }

        summary.append("\nRecommendations:\n\n");
        for (String recommendation : recommendations) {
            summary.append("• ").append(recommendation).append("\n");
        }

        return summary.toString();
    }

    private Map<String, Double> calculateWordFrequency(String content) {
        Map<String, Double> wordFrequency = new HashMap<>();
        String[] words = content.toLowerCase().split("\\s+");
        
        for (String word : words) {
            word = word.replaceAll("[^a-zA-Z]", "");
            if (!STOP_WORDS.contains(word) && word.length() > 3) {
                wordFrequency.merge(word, 1.0, Double::sum);
            }
        }

        // Normalize frequencies
        double maxFreq = wordFrequency.values().stream().max(Double::compare).orElse(1.0);
        wordFrequency.replaceAll((k, v) -> v / maxFreq);

        return wordFrequency;
    }

    private String generateFallbackSummary(String topic, List<String> contentList) {
        StringBuilder summary = new StringBuilder();
        summary.append("Key Points Summary:\n\n");
        summary.append("• Information gathered from ").append(contentList.size()).append(" sources\n");
        summary.append("• Analysis of current trends and developments\n");
        summary.append("• Overview of key findings and implications\n\n");
        
        summary.append("Key Insights:\n\n");
        summary.append("• Current trends and patterns in the field\n");
        summary.append("• Important developments and their implications\n");
        summary.append("• Critical factors affecting the topic\n\n");
        
        summary.append("Recommendations:\n\n");
        summary.append("• Consider additional research for comprehensive understanding\n");
        summary.append("• Monitor developments in this area\n");
        summary.append("• Evaluate potential impact on related fields\n");
        
        return summary.toString();
    }
} 