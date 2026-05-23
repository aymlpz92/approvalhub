package com.approvalhub.controller;

import com.approvalhub.domain.entity.StatusHistoryIndex;
import com.approvalhub.service.ElasticSearchService;
import com.approvalhub.service.StatusHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("api/search")
@RequiredArgsConstructor
public class ElasticSearchController {

    private final ElasticSearchService elasticSearchService;

    @GetMapping("/history")
    public List<StatusHistoryIndex> searchHistory(@RequestParam(required = false, name = "q") String query, @RequestParam(required = false, name = "docId") String docId) {
        if (docId != null) {
            return List.of(elasticSearchService.findById(docId));
        }
        return elasticSearchService.searchHistory(query);

    }

    @DeleteMapping("/history")
    public void deleteHistory(@RequestParam(name = "docId") String docId) throws Exception {
        elasticSearchService.deleteById(docId);
    }


}
