package com.vmware.cookbooks.backend.features;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.fluentlenium.adapter.junit.jupiter.FluentTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SearchTest extends FluentTest {
    @LocalServerPort
    int port;

    @Test
    void userCanSearchFromHomePage() {
        String testQuery = "dog cat crocodile";

        given_i_am_on_the_homepage();
        when_i_search_for(testQuery);
        then_i_see_the_results(expectedResults());
    }

    @Test
    void userCanSearchFromSearchPage() {
        String testQuery = "dog cat crocodile";

        given_i_am_on_the_search_page();
        when_i_search_for(testQuery);
        then_i_see_the_results(expectedResults());
    }

    private void given_i_am_on_the_homepage() {
        goTo(homepage());
        await().until($(".search-box input")).displayed();
    }

    private void when_i_search_for(String testQuery) {
        $(".search-box input").first().fill().with(testQuery + "\n");
        await().until($(".query")).textContent(testQuery);
    }

    private void then_i_see_the_results(List<SearchResult> expectedResults) {
        assertThat(getResults()).containsExactlyInAnyOrderElementsOf(expectedResults);
    }

    private void given_i_am_on_the_search_page() {
        goTo(searchUrl("test"));
        await().until($(".search-box input")).displayed();
    }

    private List<SearchResult> expectedResults() {
        String dogDescription = "Problem You are trying to feed a canine. The canine is hangry and expects a " +
                "form of nutrition ASAP. Solution Travel to Kentucky on the fastest connection. Purchase a bucket " +
                "of fried chicken and deliver it to the animal through the messaging solution of your choice. " +
                "How To Do It " +
                "1. Book a flight " +
                "2. Purchase fried chicken. There are several valid purchasing strategies, depending on stakeholder " +
                "preferences optimizing for price or taste preferences could make sense. " +
                "3. Send chicken over messaging solution " +
                "References " +
                "*...";

        return Arrays.asList(
                new SearchResult(
                        "Dog",
                        "/dog/",
                        Arrays.asList(mammal, woof),
                        dogDescription),
                new SearchResult(
                        "Cat",
                        "/cat/",
                        Arrays.asList(mammal, meow),
                        "Cat"),
                new SearchResult(
                        "Crocodile",
                        "/crocodile/",
                        Arrays.asList(tagWithSpaces, reptile, roar),
                        "Crocodile")
        );
    }

    private String homepage() {
        return UriComponentsBuilder
                .fromHttpUrl("http://localhost/")
                .port(port)
                .toUriString();
    }

    private String searchUrl(String query) {
        return UriComponentsBuilder
                .fromHttpUrl("http://localhost/search")
                .port(port)
                .queryParam("query", query)
                .toUriString();
    }

    private List<SearchResult> getResults() {
        // Selenium turns relative links into absolute ones
        String urlPrefix = "http://localhost:" + port;

        return $("li.result").stream()
                .map(it -> new SearchResult(
                        it.find(".title").first().text(),
                        it.find(".link").first().attribute("href").replace(urlPrefix, ""),
                        it.find(".tag").stream()
                                .map(tag -> new SearchResultTag(
                                        tag.find(".label").first().text(),
                                        tag.find(".label").first().attribute("href")
                                                .replace(urlPrefix, "")
                                ))
                                .collect(Collectors.toList()),
                        it.find(".content").first().text()
                ))
                .collect(Collectors.toList());
    }

    @Data
    @AllArgsConstructor
    static class SearchResult {
        String title;
        String url;
        List<SearchResultTag> tags;
        String description;
    }

    @Data
    @AllArgsConstructor
    static class SearchResultTag {
        String name;
        String link;
    }

    private SearchResultTag mammal = new SearchResultTag("mammal", "/tags/mammal/");
    private SearchResultTag woof = new SearchResultTag("woof", "/tags/woof/");
    private SearchResultTag meow = new SearchResultTag("meow", "/tags/meow/");
    private SearchResultTag reptile = new SearchResultTag("reptile", "/tags/reptile/");
    private SearchResultTag roar = new SearchResultTag("roar", "/tags/roar/");
    private SearchResultTag tagWithSpaces = new SearchResultTag(
            "With spaces and casing",
            "/tags/with-spaces-and-casing/"
    );
}
