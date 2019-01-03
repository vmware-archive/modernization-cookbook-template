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

                lunrIndex = lunr.Index.load(JSON.parse(value));
                localforage.getItem('pages', function (err, value) {
                    if (err)
                        return;

                    pagesIndex = JSON.parse(value);

                    if (lunrIndex && pagesIndex)
                        setupSearchHandler();
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
                delay: 300,
                /* selector for the search box element */
                selector: $("#search-by").get(0),
                /* source is the callback to perform the search */
                source: function (term, response) {
                    response(search(term));
                },
                /* renderItem displays individual search results */
                renderItem: function (item, term) {
                    return '<div class="autocomplete-suggestion" ' +
                        'data-term="' + term + '" ' +
                        'data-title="' + item.title + '" ' +
                        'data-uri="' + item.uri + '" ' +
                        'data-context="' + item.context + '">' +
                        '» ' + item.title +
                        '<div class="context">' +
                        (item.context || '') + '</div>' +
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
