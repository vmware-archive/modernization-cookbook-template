var lunrIndex, pagesIndex, ww;

// Specifying storage at which we keep search indexes
localforage.config({
    name: 'cookbook',
    storeName: 'search_index_store'
});

/**
 * Trigger a search in lunr and transform the result
 *
 * @param  {String} term
 * @return {Array}  results
 */
function search(term) {
    // By default, the query will search for an exact match across title, tags, and content fields
    // Find the item in our index corresponding to the lunr one to have more info
    return lunrIndex.search(term)
        .slice(0,50)
        .map(function (result) {
            return pagesIndex.filter(function (page) {
                return page.uri === result.ref;
            })[0];
        });
}

// Cleaning up the search index storage if client has started new session
(function () {
    var sessionExists = sessionStorage.getItem("cookbook.index.exists");
    if (!sessionExists) {
        localforage.clear(function (err) {
            initializeSearch();
        });
    } else {
        initializeSearch();
    }
})();

function initializeSearch() {
    // Attempt to load the indexes from storage
    if (typeof(ww) === "undefined") {
        ww = new Worker("/js/load-lunr-index.js");
        ww.onmessage = function (ev) {
            localforage.getItem('index', function (err, value) {
                if (err)
                    return;

                var start = new Date().getTime();    
                lunrIndex = lunr.Index.load(JSON.parse(value));
                var end = new Date().getTime();
                var time = end - start;
                console.log('Lunr Index LOAD Execution time: ' + time + 'ms');
                localforage.getItem('pages', function (err, value) {
                    if (err)
                        return;

                    pagesIndex = JSON.parse(value);

                    if (lunrIndex && pagesIndex)
                        var start = new Date().getTime(); 
                        setupSearchHandler();
                        var end = new Date().getTime();
                        var time = end - start;
                        console.log('Search Handler Execution time: ' + time + 'ms');
                        console.log('Index and Search Setup Complete');
                });
            });

            sessionStorage.setItem("cookbook.index.exists", "true");
            ww.terminate();
            ww = undefined;
        }
    }
}

function setupSearchHandler() {
    if (lunrIndex && pagesIndex) {
        $(document).ready(function () {
            var searchList = new autoComplete({
                delay: 200,
                /* Turn off autocomplete caching */
                cache: 0,
                menuClass: 'autocomplete-scroller',
                /* selector for the search box element */
                selector: $("#search-by").get(0),
                /* source is the callback to perform the search */
                source: function (term, response) {
                    response(search(term));
                },
                /* renderItem displays individual search results */
                renderItem: function (item, term) {
                    var tags = item.tags.length > 0 ? '<strong>tags:</strong> ' + item.tags : '';
                    var type = item.uri.includes('tags') ? '<strong>type:</strong> tag' : '<strong>type:</strong> recipe';
                    return '<div class="autocomplete-suggestion"' +
                        'data-term="' + term + '" ' +
                        'data-title="' + item.title + '" ' +
                        'data-uri="' + item.uri + '" ' +
                        'data-context="' + item.context + '">' +
                        '» ' + item.title + '' + 
                        '<div class="context">' + 
                            type + 
                            '<br >' +
                            tags + 
                        '</div>' +
                        '</div>';
                },
                /* onSelect callback fires when a search suggestion is chosen */
                onSelect: function (e, term, item) {
                    location.href = item.getAttribute('data-uri');
                }
            });
        });
    }
}