/* Adaptation and override of base docdock search - https://github.com/vjeantet/hugo-theme-docdock/blob/master/static/js/search.js */
var lunrIndex, pagesIndex;

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

// Initializing index
(function () {
    initializeSerializedIndex();
})();

function initializeSerializedIndex(){
    if (!endsWith(baseurl,"/")){
        baseurl = baseurl+'/';
    }

    var indexPromise = $.getJSON(baseurl + "index.json")
        .done(function (index) {
            pagesIndex = index;
        })
        .fail(function (jqxhr, textStatus, error) {
            var err = textStatus + ", " + error;
            console.error("Error getting Hugo index file:", err);
        });

    var lunrSerializedPromise = $.getJSON(baseurl + "lunrSerializedIndex.json")
        .done(function (lunrSerializedIndex) {
            lunrIndex = lunr.Index.load(lunrSerializedIndex);
        })
        .fail(function (jqxhr, textStatus, error) {
            var err = textStatus + ", " + error;
            console.error("Error getting Hugo index file:", err);
        });

    $.when(lunrSerializedPromise, indexPromise).done(function () {
        if (lunrIndex && pagesIndex)
            setupSearchHandler();
    });
    
}

function setupSearchHandler() {
    if (lunrIndex && pagesIndex) {
        $(document).ready(function () {
            var searchList = new autoComplete({
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

function endsWith(str, suffix) {
    return str.indexOf(suffix, str.length - suffix.length) !== -1;
}