package com.biglibon.libraryservice.dto;

import java.util.List;

public record AddBooksToLibraryByBookIdsRequest(Long libraryId, List<String> bookIds) {
}
