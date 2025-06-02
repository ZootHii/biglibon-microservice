package com.biglibon.bookservice.dto;


public record BookDtoMongo(String id, String title, Integer publicationYear, String author, String publisher, String isbn) {
}


