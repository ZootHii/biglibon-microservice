package com.biglibon.bookservice.service;

import com.biglibon.bookservice.mapper.BookMapper;
import com.biglibon.bookservice.model.Book;
import com.biglibon.bookservice.repository.BookRepository;
import com.biglibon.sharedlibrary.constant.KafkaTopics;
import com.biglibon.sharedlibrary.dto.BookDto;
import com.biglibon.sharedlibrary.exception.BookNotFoundException;
import com.biglibon.sharedlibrary.producer.KafkaProducer;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    private final BookRepository repository;
    private final BookMapper bookMapper;
    private final KafkaProducer kafkaProducer;

    public BookService(BookRepository repository, BookMapper bookMapper, KafkaProducer kafkaProducer) {
        this.repository = repository;
        this.bookMapper = bookMapper;
        this.kafkaProducer = kafkaProducer;
    }

    public BookDto create(BookDto bookDto) {
        // some checks can be performed
        Book newBook = bookMapper.toEntity(bookDto);
        BookDto savedBookDto = bookMapper.toDto(repository.save(newBook));
        kafkaProducer.sendMessage(KafkaTopics.BS_BOOK_ADDED, savedBookDto.toString());
        return savedBookDto;
    }

    public List<BookDto> findAllByIds(List<String> ids) {
        return bookMapper.toDtoList(repository.findAllById(ids));
    }

    public List<BookDto> findAll() {
        return bookMapper.toDtoList(repository.findAll());
    }

    public BookDto findByIsbn(String isbn) {
        return bookMapper.toDto(repository.findByIsbn(isbn)
                .orElseThrow(() -> new BookNotFoundException("Book could not found by isbn: " + isbn)));
    }

    public BookDto findById(String id) {
        return bookMapper.toDto(repository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book could not found by id: " + id)));
    }
}
