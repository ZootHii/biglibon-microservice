package com.biglibon.libraryservice.dto;

import java.util.List;

public record LibraryDto(Long id, List<BookDto> books) {
}
