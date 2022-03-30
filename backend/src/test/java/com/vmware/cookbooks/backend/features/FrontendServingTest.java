package com.vmware.cookbooks.backend.features;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FrontendServingTest {
    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    void servesMainPage() {
        ResponseEntity<String> response = testRestTemplate.getForEntity("/", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("Application Modernization Cookbook");
        assertThat(response.getHeaders().getFirst("Content-Type")).containsIgnoringCase("utf-8");
    }

    @Test
    void pathEndingWithSlash_servesIndexHtmlFromPath() {
        ResponseEntity<String> response = testRestTemplate.getForEntity("/tags/cloud/", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains(" recipe(s) tagged with ");
        assertThat(response.getHeaders().getFirst("Content-Type")).containsIgnoringCase("utf-8");
    }

    @Test
    void pathNotEndingWithSlash_servesIndexHtmlFromPath() {
        ResponseEntity<String> response = testRestTemplate.getForEntity("/tags/cloud", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains(" recipe(s) tagged with ");
        assertThat(response.getHeaders().getFirst("Content-Type")).containsIgnoringCase("utf-8");
    }

    @Test
    void directoryDoesNotExist_returns404() {
        ResponseEntity<String> response = testRestTemplate.getForEntity("/tags/does_not-exist", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
