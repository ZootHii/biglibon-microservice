package com.biglibon.sharedlibrary.dto;

public record BookDto(
        String id,
        String title,
        Integer publicationYear,
        String author,
        String publisher,
        String isbn
) {
}
