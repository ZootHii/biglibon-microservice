package com.biglibon.sharedlibrary.dto;

import lombok.Data;

import java.util.List;

@Data
public class LibraryDto {
    private Long id;
    private String name;
    private String city;
    private String phone;
    private List<BookDto> books;
}

