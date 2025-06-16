package com.biglibon.catalogservice.model;

import com.biglibon.sharedlibrary.dto.BookSummaryDto;
import com.biglibon.sharedlibrary.dto.LibrarySummaryDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Persistent;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Document(collection = "catalogs")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Persistent
@CompoundIndex(name = "catalog_book_index", def = "{'book.bookId': 1, 'book.isbn': 1}", unique = true)
public class Catalog {

    @Id
    private String id;

    private BookSummaryDto book;
    private List<LibrarySummaryDto> libraries;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    public Catalog(BookSummaryDto book, List<LibrarySummaryDto> libraries) {
        this.book = book;
        this.libraries = libraries;
    }
}
