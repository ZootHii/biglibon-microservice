//package com.biglibon.bookservice.model;
//
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//@Entity
//@Table(name = "books")
//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//public class Book {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bookSeq")
//    @SequenceGenerator(name = "bookSeq", sequenceName = "book_seq")
//    private long id;
//
//    private String title;
//    private int publicationYear;
//    private String author;
//    private String publisher;
//
//    @Column(unique = true)
//    private String isbn;
//
//    public Book(String title, Integer publicationYear, String author, String publisher, String isbn) {
//        this.title = title;
//        this.publicationYear = publicationYear;
//        this.author = author;
//        this.publisher = publisher;
//        this.isbn = isbn;
//    }
//}
