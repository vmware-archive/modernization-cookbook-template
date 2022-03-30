package com.vmware.cookbooks.backend.search;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HugoDocument {
    String uri;
    String title;
    List<String> tags;
    String description;
    String content;
}
