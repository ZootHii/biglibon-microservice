package com.biglibon.sharedlibrary.dto;

import java.util.List;

public record LibraryDto(
        Long id,
        String name,
        String city,
        String phone,
        List<BookDto> books) {
}
