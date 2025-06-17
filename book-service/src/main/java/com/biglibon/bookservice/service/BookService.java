package com.biglibon.bookservice.service;

import com.biglibon.bookservice.mapper.BookMapper;
import com.biglibon.bookservice.repository.BookRepository;
import com.biglibon.sharedlibrary.constant.KafkaConstants;
import com.biglibon.sharedlibrary.consumer.KafkaEvent;
import com.biglibon.sharedlibrary.dto.BookDto;
import com.biglibon.sharedlibrary.exception.BookNotFoundException;
import com.biglibon.sharedlibrary.producer.KafkaEventProducer;
import org.springframework.stereotype.Service;

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

    public BookDto create(BookDto bookDto) {
        BookDto newBook = bookMapper.toDto(repository.save(bookMapper.toEntity(bookDto)));

        kafkaEventProducer.send(new KafkaEvent<>(
                KafkaConstants.Book.TOPIC,
                KafkaConstants.Book.ADD_BOOK_EVENT,
                KafkaConstants.Book.PRODUCER,
                newBook));
        return newBook;
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
