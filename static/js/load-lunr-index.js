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
    $.getJSON("/index.json")
        .done(function (index) {
            pagesIndex = index;
            lunrIndex = lunr(function () {
                this.ref("uri");
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

            localforage.setItem('index', JSON.stringify(lunrIndex), function (err) {
                localforage.setItem('pages', JSON.stringify(pagesIndex), function (err) {
                    callback();
                })
            });
        })
        .fail(function (jqxhr, textStatus, error) {
            var err = textStatus + ", " + error;
            console.error("Error getting Hugo index file for search:", err);
            callback();
        });
}

initLunr(function () {
    postMessage("Loading completed");
});
