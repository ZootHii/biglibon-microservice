package com.biglibon.sharedlibrary.dto;

import java.time.Instant;
import java.util.List;

public record CatalogDto(
        String id,
        BookSummaryDto book,
        List<LibrarySummaryDto> libraries,
        Instant createdAt,
        Instant updatedAt) {
}
