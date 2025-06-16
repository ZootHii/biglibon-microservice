package com.biglibon.sharedlibrary.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookSummaryDto {
    private String bookId;
    private String title;
    private int publicationYear;
    private String author;
    private String publisher;
    private String isbn;
}
