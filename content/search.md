+++
date = "2020-03-19T15:28:50-06:00"
title = "Cookbook - Search"
type = "search"
+++

{{< rawhtml >}}
<h1 class="results-title">Search results for: <span class="query">{{query}}</span></h1>
<div class="search-results-page">
    {{#error}}
    <div class="search-error">
        <pre>{{.}}</pre>
    </div>
    {{/error}}
    <ul class="results">
        {{#results}}
        <li class="result">
            <a class="title link" href="{{url}}">{{title}}</a>
            <div class="content">{{&content}}</div>
            <div class="tags">
                <i class="fas fa-tags"></i>
                <ul class="tags">
                    {{#tags}}
                    <li class="tag"><a class="label clickable" href="{{link}}">{{name}}</a></li>
                    {{/tags}}
                </ul>
            </div>
        </li>
        {{/results}}
    </ul>
</div>
{{< /rawhtml >}}