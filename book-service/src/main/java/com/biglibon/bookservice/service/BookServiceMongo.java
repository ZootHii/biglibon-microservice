package com.biglibon.bookservice.service;

import com.biglibon.bookservice.dto.BookDtoMongo;
import com.biglibon.bookservice.exception.BookNotFoundException;
import com.biglibon.bookservice.mapper.BookMapperMongo;
import com.biglibon.bookservice.model.BookMongo;
import com.biglibon.bookservice.repository.BookRepositoryMongo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookServiceMongo {

    private final BookRepositoryMongo repository;
    private final BookMapperMongo bookMapper;

    public BookServiceMongo(BookRepositoryMongo repository, BookMapperMongo bookMapper) {
        this.repository = repository;
        this.bookMapper = bookMapper;
    }

    public BookDtoMongo create(BookDtoMongo bookDto) {
        // some checks can be performed
        BookMongo newBook = bookMapper.toEntity(bookDto);
        repository.save(newBook);
        return bookDto;
    }

    public List<BookDtoMongo> findAllByIds(List<String> ids) {
        return bookMapper.toDtoList(repository.findAllById(ids));
    }

    public List<BookDtoMongo> findAll() {
        return bookMapper.toDtoList(repository.findAll());
    }

    public BookDtoMongo findByIsbn(String isbn) {
        return bookMapper.toDto(repository.findByIsbn(isbn)
                .orElseThrow(() -> new BookNotFoundException("Book could not found by isbn: " + isbn)));
    }

    public BookDtoMongo findById(String id) {
        return bookMapper.toDto(repository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book could not found by id: " + id)));
    }
}
