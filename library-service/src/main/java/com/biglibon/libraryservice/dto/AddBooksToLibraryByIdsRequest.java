package com.biglibon.libraryservice.dto;

import java.util.List;

public record AddBooksToLibraryByIdsRequest(Long libraryId, List<String> bookIds) {
}
