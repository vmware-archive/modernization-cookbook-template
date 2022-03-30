package com.vmware.cookbooks.backend.search;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SearchService {
    public static final String SEARCH_INDEX_JSON = "/static/index.json";
    public static final String FIELD_TITLE = "title";
    public static final String FIELD_URL = "url";
    public static final String FIELD_TAG = "tag";
    public static final String FIELD_CONTENT = "content";
    public static final String[] SEARCH_FIELDS = new String[]{FIELD_TITLE, FIELD_CONTENT, FIELD_TAG};
    private Directory directory;
    private Analyzer analyzer;

    public List<Result> search(String query) throws IOException, ParseException {
        init();

        IndexReader indexReader = DirectoryReader.open(this.directory);
        IndexSearcher searcher = new IndexSearcher(indexReader);
        Query luceneQuery = new MultiFieldQueryParser(SEARCH_FIELDS, this.analyzer).parse(query);
        TopDocs topDocs = searcher.search(luceneQuery, 50);

        List<Result> results = new ArrayList<>();
        for (ScoreDoc docRef : topDocs.scoreDocs) {
            Document doc = searcher.doc(docRef.doc);
            results.add(new Result(
                    doc.get(FIELD_TITLE),
                    doc.get(FIELD_URL),
                    // sort to make objects stable for comparison
                    Arrays.stream(doc.getValues(FIELD_TAG))
                            .sorted()
                            .map(it -> new Tag(it, linkForTag(it)))
                            .collect(Collectors.toList()),
                    formatContent(doc.get(FIELD_CONTENT))
            ));
        }

        return results;
    }

    public void index() throws IOException {
        init();

        IndexWriter indexWriter = indexWriter();
        HugoDocument[] hugoDocuments = readDocuments();

        for (HugoDocument document : hugoDocuments) {
            if (document.getContent().isEmpty()) {
                // We only index real pages with content.
                // This skips tag overview pages. Tags are part of each real document still.
                continue;
            }

            Document luceneDoc = new Document();
            luceneDoc.add(new TextField(FIELD_TITLE, document.getTitle(), Field.Store.YES));
            luceneDoc.add(new StringField(FIELD_URL, document.getUri(), Field.Store.YES));
            luceneDoc.add(new TextField(FIELD_CONTENT, document.getContent(), Field.Store.YES));

            for (String tag : document.getTags()) {
                luceneDoc.add(new TextField(FIELD_TAG, tag, Field.Store.YES));
            }

            indexWriter.addDocument(luceneDoc);
        }

        indexWriter.close();
    }

    private void init() {
        if (this.analyzer == null) {
            this.analyzer = new EnglishAnalyzer();
        }
        if (this.directory == null) {
            this.directory = new RAMDirectory();
        }
    }

    private IndexWriter indexWriter() throws IOException {
        IndexWriterConfig config = new IndexWriterConfig(this.analyzer);
        return new IndexWriter(this.directory, config);
    }

    private HugoDocument[] readDocuments() throws java.io.IOException {
        InputStream indexStream = getClass().getResourceAsStream(SEARCH_INDEX_JSON);
        return new ObjectMapper().readValue(indexStream, HugoDocument[].class);
    }

    private String linkForTag(String tagName) {
        String normalizedTag = tagName.toLowerCase().replaceAll(" ", "-");
        return String.format("/tags/%s/", normalizedTag);
    }

    private String formatContent(String content) {
        final int maxLength = 512;
        if (content.length() <= maxLength) {
            return content;
        }

        String[] words = content.split(" ");
        StringBuilder sb = new StringBuilder();
        int offset = 0;
        while (sb.length() <= maxLength) {
            sb.append(words[offset]);
            sb.append(' ');
            offset++;
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append("...");

        return sb.toString();
    }
}
