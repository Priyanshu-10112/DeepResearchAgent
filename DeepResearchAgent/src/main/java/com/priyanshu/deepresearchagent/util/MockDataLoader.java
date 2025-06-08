package com.priyanshu.deepresearchagent.util;

import java.util.List;

public class MockDataLoader {

    public static String getMockContent(String topic) {
        return "News: Canada updated visa rules for 2025.\n"
             + "Reddit: Students facing IRCC delays.\n"
             + "Paper: Academic impact of immigration policies.";
    }

    public static List<String> getSources() {
        return List.of(
                "https://news.example.com/canada-visa",
                "https://reddit.com/r/ImmigrationCanada",
                "https://arxiv.org/abs/2306.00001"
        );
    }
}
