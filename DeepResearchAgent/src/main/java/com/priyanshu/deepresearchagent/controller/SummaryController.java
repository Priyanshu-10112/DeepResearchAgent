package com.priyanshu.deepresearchagent.controller;

import com.priyanshu.deepresearchagent.model.SummaryResponse;
import com.priyanshu.deepresearchagent.service.SummaryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/summary")
@CrossOrigin(origins = "http://localhost:3000")
public class SummaryController {

    private static final Logger logger = LoggerFactory.getLogger(SummaryController.class);

    @Autowired
    private SummaryService service;

    @GetMapping
    public ResponseEntity<SummaryResponse> getSummary(@RequestParam String topic) {
        try {
            logger.info("Generating summary for topic: {}", topic);
            SummaryResponse response = service.generateSummary(topic);
            logger.info("Successfully generated summary with {} news, {} academic, and {} forum items", 
                response.getNewsContent().size(),
                response.getAcademicContent().size(),
                response.getForumContent().size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error generating summary for topic {}: {}", topic, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
