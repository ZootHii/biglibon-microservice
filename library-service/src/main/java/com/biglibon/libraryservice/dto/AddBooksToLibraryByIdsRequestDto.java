package com.biglibon.libraryservice.dto;

import java.util.List;

public record AddBooksToLibraryByIdsRequestDto(Long libraryId, List<String> bookId) {
}
