package com.biglibon.sharedlibrary.dto;

import java.time.Instant;

public record BookResponse(
        String id,
        String title,
        Integer publicationYear,
        String author,
        String publisher,
        String isbn,
        Instant createdAt,
        Instant updatedAt) {
}
