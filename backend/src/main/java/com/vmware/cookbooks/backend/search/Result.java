package com.vmware.cookbooks.backend.search;

import lombok.Value;

import java.util.List;

@Value
public class Result {
    String title;
    String url;
    List<Tag> tags;
    String content;
}
