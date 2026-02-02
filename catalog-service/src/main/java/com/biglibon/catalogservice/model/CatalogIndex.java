package com.biglibon.catalogservice.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "catalogs_index")
@JsonIgnoreProperties(ignoreUnknown = true)
@Setting(settingPath = "/elasticsearch/catalogs-settings.json")// index template + versioned index + alias
//setting ismi ne olmalı // resources altında eelasticsearch klasöründe olmalı // shared içinde mi olmalı ona göre yazıcaz
public class CatalogIndex {

    @Id
    private String id;

    @Field(type = FieldType.Object)
    private BookSummary book;

    @Field(type = FieldType.Nested)
    private List<LibrarySummary> libraries;

    // Elasticsearch does not parse Instant directly so we used JsonFormat and save these fields as String with pattern
    @Field(type = FieldType.Date, format = DateFormat.date_time)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX", timezone = "UTC")
    private Instant createdAt;

    // Elasticsearch does not parse Instant directly so we used JsonFormat and save these fields as String with pattern
    @Field(type = FieldType.Date, format = DateFormat.date_time)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX", timezone = "UTC")
    private Instant updatedAt;

    // Nested Classes defined under main class
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BookSummary {
        @Field(type = FieldType.Keyword)
        private String bookId;

        @Field(type = FieldType.Text, analyzer = "autocomplete_index", searchAnalyzer = "autocomplete_search")
        private String title;

        @Field(type = FieldType.Text)
        private String publicationYear;

        @Field(type = FieldType.Text, analyzer = "autocomplete_index", searchAnalyzer = "autocomplete_search")
        private String author;

        @Field(type = FieldType.Text, analyzer = "autocomplete_index", searchAnalyzer = "autocomplete_search")
        private String publisher;

        @Field(type = FieldType.Keyword)
        private String isbn;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LibrarySummary {
        @Field(type = FieldType.Keyword)
        private String libraryId;

        @Field(type = FieldType.Text, analyzer = "autocomplete_index", searchAnalyzer = "autocomplete_search")
        private String name;

        @Field(type = FieldType.Text, analyzer = "autocomplete_index", searchAnalyzer = "autocomplete_search")
        private String city;

        @Field(type = FieldType.Text)
        private String phone;
    }
}
