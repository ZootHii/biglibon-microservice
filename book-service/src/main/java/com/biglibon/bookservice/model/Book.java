package com.biglibon.bookservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "books")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Book {

    @Id
    private String id;

    private String title;
    private int publicationYear;
    private String author;
    private String publisher;
    private String isbn;

    public Book(String title, Integer publicationYear, String author, String publisher, String isbn) {
        this.title = title;
        this.publicationYear = publicationYear;
        this.author = author;
        this.publisher = publisher;
        this.isbn = isbn;
    }
}
