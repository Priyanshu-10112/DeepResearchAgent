package com.priyanshu.deepresearchagent.controller;

import com.priyanshu.deepresearchagent.model.ResearchResult;
import com.priyanshu.deepresearchagent.service.ResearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/research")
@CrossOrigin(origins = "http://localhost:3000")
public class ResearchController {

    @Autowired
    private ResearchService researchService;

    @GetMapping
    public ResearchResult researchTopic(@RequestParam String topic) {
        return researchService.researchTopic(topic);
    }

    @PostMapping("/reports/save")
    public ResponseEntity<ResearchResult> saveReport(@RequestBody ResearchResult report) {
        return ResponseEntity.ok(researchService.saveReport(report));
    }

    @GetMapping("/reports/saved")
    public ResponseEntity<List<ResearchResult>> getSavedReports() {
        return ResponseEntity.ok(researchService.getSavedReports());
    }

    @DeleteMapping("/reports/{reportId}")
    public ResponseEntity<Void> deleteReport(@PathVariable String reportId) {
        researchService.deleteReport(reportId);
        return ResponseEntity.ok().build();
    }
} 