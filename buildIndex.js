'use strict';

/* Adaptation of https://lunrjs.com/guides/index_prebuilding.html */
var lunr = require('./js/lunr.min.js'),
  stdin = process.stdin,
  stdout = process.stdout,
  buffer = [];

stdin.resume();
stdin.setEncoding('utf8');

stdin.on('data', function (data) {
  buffer.push(data);
});

stdin.on('end', function () {
  var documents = JSON.parse(buffer.join(''));

  var idx = lunr(function () {
    this.ref("uri");
    this.field("title", {
      boost: 15
    });
    this.field("tags", {
      boost: 10
    });
    this.field("content");

    documents.forEach(function (doc) {
      this.add(doc);
    }, this);
    
  });

  stdout.write(JSON.stringify(idx));
});