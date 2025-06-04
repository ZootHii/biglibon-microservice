package com.biglibon.libraryservice.dto;

import com.biglibon.sharedlibrary.dto.BookDto;

import java.util.List;

public record LibraryDto(
        Long id,
        String name,
        String city,
        String phone,
        List<BookDto> books) {
}
