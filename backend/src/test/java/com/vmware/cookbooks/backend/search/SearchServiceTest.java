package com.vmware.cookbooks.backend.search;

import org.apache.lucene.queryparser.classic.ParseException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SearchServiceTest {
    @Test
    void index_indexesIndexJson() throws IOException, ParseException {
        SearchService searchService = new SearchService();
        searchService.index();

        List<Result> dogResults = searchService.search("dog");
        assertThat(dogResults).containsExactly(dogResult);

        // intentionally left out the "e"
        List<Result> crocoResults = searchService.search("crocodil");
        assertThat(crocoResults).containsExactly(crocoResult);
    }

    @Test
    void index_doesNotIndexTags() throws IOException, ParseException {
        SearchService searchService = new SearchService();
        searchService.index();

        List<Result> tagResults = searchService.search("tag");
        assertThat(tagResults).isEmpty();
    }

    @Test
    void search_searchesInTitles() throws IOException, ParseException {
        SearchService searchService = new SearchService();
        searchService.index();

        List<Result> dogResults = searchService.search("dog");
        assertThat(dogResults).containsExactly(dogResult);
    }

    @Test
    void search_searchesInContent() throws IOException, ParseException {
        SearchService searchService = new SearchService();
        searchService.index();

        List<Result> dogResults = searchService.search("canine");
        assertThat(dogResults).containsExactly(dogResult);
    }

    @Test
    void search_searchesInTags() throws IOException, ParseException {
        SearchService searchService = new SearchService();
        searchService.index();

        List<Result> dogResults = searchService.search("roar");
        assertThat(dogResults).containsExactly(crocoResult);
    }


    private Tag mammal = new Tag("mammal", "/tags/mammal/");
    private Tag woof = new Tag("woof", "/tags/woof/");
    private Tag reptile = new Tag("reptile", "/tags/reptile/");
    private Tag roar = new Tag("roar", "/tags/roar/");
    private Tag tagWithSpaces = new Tag("With spaces and casing", "/tags/with-spaces-and-casing/");
    private String dogDescription = "Problem\nYou are trying to feed a canine. The canine is hangry and expects a " +
            "form of nutrition ASAP.\nSolution\nTravel to Kentucky on the fastest connection. Purchase a bucket of " +
            "fried chicken and deliver it to the animal through the messaging solution of your choice.\n" +
            "How To Do It\n" +
            "1. Book a flight\n" +
            "2. Purchase fried chicken. There are several valid purchasing strategies, depending on stakeholder " +
            "preferences optimizing for price or taste preferences could make sense.\n" +
            "3. Send chicken over messaging solution\n" +
            "References\n" +
            "*...";

    private Result dogResult = new Result("Dog", "/dog/", Arrays.asList(mammal, woof), dogDescription);
    private Result crocoResult = new Result(
            "Crocodile",
            "/crocodile/",
            Arrays.asList(tagWithSpaces, reptile, roar),
            "Crocodile");
}