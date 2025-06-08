package com.priyanshu.deepresearchagent.service;

import com.priyanshu.deepresearchagent.model.SourceContent;
import java.util.List;

public interface DataSourceService {
    List<SourceContent> fetchContent(String topic);
    double getRelevanceScore(String content, String topic);
} 