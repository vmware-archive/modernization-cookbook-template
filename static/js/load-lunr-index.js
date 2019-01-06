// Faking infrastructure for JQuery usage
var document = self.document = {
    parentNode: null, nodeType: 9, toString: function () {
        return "FakeDocument"
    }
};
var window = self.window = self;
var fakeElement = Object.create(document);
fakeElement.nodeType = 1;
fakeElement.toString = function () {
    return "FakeElement"
};
fakeElement.parentNode = fakeElement.firstChild = fakeElement.lastChild = fakeElement;
fakeElement.ownerDocument = document;

document.head = document.body = fakeElement;
document.ownerDocument = document.documentElement = document;
document.getElementById = document.createElement = function () {
    return fakeElement;
};
document.createDocumentFragment = function () {
    return this;
};
document.getElementsByTagName = document.getElementsByClassName = function () {
    return [fakeElement];
};
document.getAttribute = document.setAttribute = document.removeChild =
    document.addEventListener = document.removeEventListener =
        function () {
            return null;
        };
document.cloneNode = document.appendChild = function () {
    return this;
};
document.appendChild = function (child) {
    return child;
};

importScripts("/js/lunr.min.js");
importScripts("/js/jquery-2.x.min.js");
importScripts("/js/localforage.nopromises.min.js");

// Initialize lunrjs using our generated index file
function initLunr(callback) {
    localforage.config({
        name: 'cookbook',
        storeName: 'search_index_store'
    });

    var lunrIndex, pagesIndex;

    localforage.getItem('index', function (err, value) {
        if (err)
            return;

        lunrIndex = value;
        localforage.getItem('pages', function (err, value) {
            if (err)
                return;
            pagesIndex = value;

            // We obtained indexes and ready to continue
            if (lunrIndex && pagesIndex) {
                callback();
            } else {
                // We need to download and calculate the indexes
                calculateIndexes(function () {
                    callback();
                });
            }
        });
    });
}

function calculateIndexes(callback) {
    var start = new Date().getTime();

    var indexPromise = $.getJSON("/index.json")
        .done(function (index) {
            pagesIndex = index;
        });

    var lunrSerializedPromise = $.getJSON("/lunrSerializedIndex.json")
        .done(function (lunrSerializedIndex) {
            lunrIndex = lunr.Index.load(lunrSerializedIndex);
        });

    $.when(lunrSerializedPromise, indexPromise)
        .done(function () {
            if (lunrIndex && pagesIndex)
                localforage.setItem('index', JSON.stringify(lunrIndex), function (err) {
                    localforage.setItem('pages', JSON.stringify(pagesIndex), function (err) {
                        callback();
                    });
                });
            var end = new Date().getTime();
            var time = end - start;
            console.log('Lunr Index Create Execution time: ' + time + 'ms');
        })
        .fail(function (jqxhr, textStatus, error) {
            var err = textStatus + ", " + error;
            console.warn("Error getting pre-built Lunr index file:", err);
            console.warn("Falling back to create index on the fly");

            createLunrIndexOnFlyHandler(function () {
                callback();
            });
        });
}

initLunr(function () {
    postMessage("Loading completed");
});

/* Handler to build Lunr Index on the fly if there is no index locally or a failure loading occurs */
function createLunrIndexOnFlyHandler(callback) {
    var start = new Date().getTime();

    $.getJSON("/index.json")
        .done(function (index) {
            pagesIndex = index;
            lunrIndex = lunr(function () {
                this.ref('uri');
                this.field('title', {
                    boost: 15
                  });
                  this.field('tags', {
                    boost: 10
                  });
                  this.field("content");

                var idx = this;
                pagesIndex.forEach(function (page) {
                    idx.add(page);
                });
            });    

            if (lunrIndex && pagesIndex)
                localforage.setItem('index', JSON.stringify(lunrIndex), function (err) {
                    localforage.setItem('pages', JSON.stringify(pagesIndex), function (err) {
                        callback();
                    });
                });

                var end = new Date().getTime();
                var time = end - start;
                console.log('Lunr Index Create On The Fly Execution time: ' + time + 'ms');    
        })
        .fail(function (jqxhr, textStatus, error) {
            var err = textStatus + ", " + error;
            console.error("Error getting Hugo index file for search:", err);
        });
}