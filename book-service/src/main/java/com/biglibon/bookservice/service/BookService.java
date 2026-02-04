package com.biglibon.bookservice.service;

import com.biglibon.bookservice.mapper.BookMapper;
import com.biglibon.bookservice.model.Book;
import com.biglibon.bookservice.repository.BookRepository;
import com.biglibon.sharedlibrary.constant.KafkaConstants;
import com.biglibon.sharedlibrary.consumer.KafkaEvent;
import com.biglibon.sharedlibrary.dto.BookDto;
import com.biglibon.sharedlibrary.exception.BookDuplicateException;
import com.biglibon.sharedlibrary.exception.BookNotFoundException;
import com.biglibon.sharedlibrary.producer.KafkaEventProducer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
public class BookService {

    private final BookRepository repository;
    private final BookMapper bookMapper;
    private final KafkaEventProducer kafkaEventProducer;

    public BookService(
            BookRepository repository,
            BookMapper bookMapper,
            KafkaEventProducer kafkaEventProducer) {
        this.repository = repository;
        this.bookMapper = bookMapper;
        this.kafkaEventProducer = kafkaEventProducer;
    }

    // Outbox pattern veya transactional event
    @Transactional
    public BookDto create(BookDto bookDto) {
        Book bookToSave = bookMapper.toEntity(bookDto);
        repository.findByIsbn(bookDto.isbn()).ifPresent(book -> {
            throw new BookDuplicateException("Book already exists with ISBN: " + book.getIsbn());
        });
        Book bookSaved = repository.save(bookToSave);
        BookDto bookSavedDto = bookMapper.toDto(bookSaved);

        kafkaEventProducer.send(new KafkaEvent<>(
                KafkaConstants.Book.TOPIC,
                KafkaConstants.Book.ADD_BOOK_EVENT,
                KafkaConstants.Book.PRODUCER,
                bookSavedDto));
        return bookSavedDto;
    }

    public List<BookDto> findAllByIds(List<String> ids) {
        return bookMapper.toDtoList(repository.findAllById(ids));
    }

    public List<BookDto> findAll() {
        return bookMapper.toDtoList(repository.findAll());
    }

    public List<BookDto> findAllByIsbns(List<String> isbns) {
        List<Book> books = repository.findAllByIsbnIn(isbns).orElse(Collections.emptyList());
        return bookMapper.toDtoList(books);
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
