package com.vmware.cookbooks.backend.search;

import org.apache.lucene.queryparser.classic.ParseException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;

@Controller
public class SearchController {
    private SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/search")
    public ModelAndView search(@RequestParam String query) throws IOException, ParseException {
        Map<String, Object> model = new HashMap<>();
        model.put("query", query);

        try {
            List<Result> results = searchService.search(query);
            model.put("results", results);
        } catch (ParseException e) {
            model.put("results", emptyList());
            model.put("error", e.getMessage());
        }

        return new ModelAndView("search_results", model);
    }
}
