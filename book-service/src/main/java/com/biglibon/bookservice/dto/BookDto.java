package com.biglibon.bookservice.dto;


public record BookDto(Long id, String title, Integer publicationYear, String author, String publisher, String isbn) {
}


