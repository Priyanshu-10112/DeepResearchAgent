package com.priyanshu.deepresearchagent.controller;

import com.priyanshu.deepresearchagent.model.DynamicQuery;
import com.priyanshu.deepresearchagent.model.ResearchResult;
import com.priyanshu.deepresearchagent.model.Source;
import com.priyanshu.deepresearchagent.service.DynamicQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dynamic")
@CrossOrigin(origins = "http://localhost:3000")
public class DynamicQueryController {

    @Autowired
    private DynamicQueryService dynamicQueryService;

    @PostMapping("/research")
    public ResponseEntity<ResearchResult> processDynamicQuery(@RequestBody DynamicQuery query) {
        try {
            ResearchResult result = dynamicQueryService.processDynamicQuery(query);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/sources")
    public ResponseEntity<Source[]> getAvailableSources() {
        return ResponseEntity.ok(Source.values());
    }

    @GetMapping("/query-types")
    public ResponseEntity<DynamicQuery.QueryType[]> getQueryTypes() {
        return ResponseEntity.ok(DynamicQuery.QueryType.values());
    }

    @GetMapping("/time-ranges")
    public ResponseEntity<DynamicQuery.TimeRange[]> getTimeRanges() {
        return ResponseEntity.ok(DynamicQuery.TimeRange.values());
    }
} 