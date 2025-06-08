package com.priyanshu.deepresearchagent.config;

import com.priyanshu.deepresearchagent.service.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class DataSourceConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public NewsSourceService newsSourceService(RestTemplate restTemplate, ApiConfig apiConfig) {
        return new NewsSourceService(restTemplate, apiConfig);
    }

    @Bean
    public AcademicSourceService academicSourceService(RestTemplate restTemplate, ApiConfig apiConfig) {
        return new AcademicSourceService(restTemplate, apiConfig);
    }

    @Bean
    public ForumSourceService forumSourceService(RestTemplate restTemplate, ApiConfig apiConfig) {
        return new ForumSourceService(restTemplate, apiConfig);
    }

    @Bean
    public SerpSourceService serpSourceService(RestTemplate restTemplate, ApiConfig apiConfig) {
        return new SerpSourceService(restTemplate, apiConfig);
    }

    @Bean
    public OpenAlexSourceService openAlexSourceService(RestTemplate restTemplate, ApiConfig apiConfig) {
        return new OpenAlexSourceService(restTemplate, apiConfig);
    }

    @Bean
    public JsoupSourceService jsoupSourceService() {
        return new JsoupSourceService();
    }

    @Bean
    public BasicSummarizationService basicSummarizationService() {
        return new BasicSummarizationService();
    }
} 