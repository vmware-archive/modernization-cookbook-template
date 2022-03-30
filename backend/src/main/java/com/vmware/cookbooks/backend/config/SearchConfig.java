package com.vmware.cookbooks.backend.config;

import com.vmware.cookbooks.backend.search.SearchService;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Configuration
public class SearchConfig {
    private SearchService searchService;

    public SearchConfig(SearchService searchService) {
        this.searchService = searchService;
    }

    @PostConstruct
    public void index() throws IOException {
        searchService.index();
    }
}
