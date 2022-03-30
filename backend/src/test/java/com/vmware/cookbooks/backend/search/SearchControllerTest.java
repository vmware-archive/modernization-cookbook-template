package com.vmware.cookbooks.backend.search;

import org.apache.lucene.queryparser.classic.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static java.util.Collections.emptyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class SearchControllerTest {
    private MockMvc mockMvc;
    private SearchService searchService;

    @BeforeEach
    void setUp() throws IOException, ParseException {
        searchService = mock(SearchService.class);
        when(searchService.search(anyString())).thenReturn(expectedResults);

        SearchController searchController = new SearchController(searchService);
        mockMvc = MockMvcBuilders.standaloneSetup(searchController).build();
    }

    @Test
    void search_returnsResults() throws Exception {
        String query = "the search query";

        mockMvc.perform(get("/search")
                .queryParam("query", query))
                .andExpect(status().isOk())
                .andExpect(view().name("search_results"))
                .andExpect(model().attribute("query", query))
        .andExpect(model().attribute("results", expectedResults));

        verify(searchService).search(query);
    }

    @Test
    void search_showsParseErrors() throws Exception {
        String query = "the search query";
        when(searchService.search(anyString())).thenThrow(new ParseException("error message"));

        mockMvc.perform(get("/search")
                .queryParam("query", query))
                .andExpect(status().isOk())
                .andExpect(view().name("search_results"))
                .andExpect(model().attribute("query", query))
                .andExpect(model().attribute("results", emptyList()))
                .andExpect(model().attribute("error", "error message"));

        verify(searchService).search(query);

    }

    private Tag tag1 = new Tag("tag1", "/tag1/");
    private Tag tag2 = new Tag("tag2", "/tag2/");
    private Tag tag3 = new Tag("tag3", "/tag3/");

    private List<Result> expectedResults = Arrays.asList(
            new Result("foo1", "/foo1/", Arrays.asList(tag1, tag2),"description 1" ),
            new Result("foo2", "/foo2/", Arrays.asList(tag2, tag3), "description 2")
    );
}