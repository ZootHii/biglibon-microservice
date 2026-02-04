package com.biglibon.bookservice.service;

import com.biglibon.bookservice.mapper.BookMapper;
import com.biglibon.bookservice.model.Book;
import com.biglibon.bookservice.repository.BookRepository;
import com.biglibon.sharedlibrary.constant.KafkaConstants;
import com.biglibon.sharedlibrary.consumer.KafkaEvent;
import com.biglibon.sharedlibrary.dto.BookDto;
import com.biglibon.sharedlibrary.exception.BookNotFoundException;
import com.biglibon.sharedlibrary.producer.KafkaEventProducer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository repository;

    @Mock
    private BookMapper bookMapper;

    @Mock
    private KafkaEventProducer kafkaEventProducer;

    @InjectMocks
    private BookService bookService;

    @Test
    void create_whenValidRequest_producesKafkaAddBookEvent() {
        // api request
        BookDto bookDto = new BookDto(null, "Clean Code", 2008, "Robert C. Martin", "Prentice Hall", "isbn-1", null, null);
        // after dto mapped by bookMapper
        Book book = new Book("Clean Code", 2008, "Robert C. Martin", "Prentice Hall", "isbn-1");

        // after book saved to db
        Book savedBook = new Book("Clean Code", 2008, "Robert C. Martin", "Prentice Hall", "isbn-1");
        Instant now = Instant.parse("2026-02-04T10:04:42.525821Z");
        savedBook.setId("book-1");
        savedBook.setCreatedAt(now);
        savedBook.setUpdatedAt(now);

        BookDto savedDto = new BookDto("book-1", "Clean Code", 2008, "Robert C. Martin", "Prentice Hall", "isbn-1", now, now);

        when(bookMapper.toEntity(bookDto)).thenReturn(book);
        when(repository.save(book)).thenReturn(savedBook);
        when(bookMapper.toDto(savedBook)).thenReturn(savedDto);

        BookDto result = bookService.create(bookDto);

        assertThat(result).isEqualTo(savedDto);
        ArgumentCaptor<KafkaEvent<BookDto>> eventCaptor = ArgumentCaptor.forClass(KafkaEvent.class);
        verify(kafkaEventProducer).send(eventCaptor.capture());
        KafkaEvent<BookDto> event = eventCaptor.getValue();
        assertThat(event.getTopic()).isEqualTo(KafkaConstants.Book.TOPIC);
        assertThat(event.getEvent()).isEqualTo(KafkaConstants.Book.ADD_BOOK_EVENT);
        assertThat(event.getProducer()).isEqualTo(KafkaConstants.Book.PRODUCER);
        assertThat(event.getPayload()).isEqualTo(savedDto);
    }

    @Test
    void findAllByIsbns_whenBooksExist_returnsMappedBookDtos() {
        Book book = new Book("Domain-Driven Design", 2003, "Eric Evans", "Addison-Wesley", "isbn-2");
        List<Book> books = List.of(book);
        List<BookDto> expectedDtos = List.of(new BookDto("book-2", "Domain Driven Design", 2003, "Eric Evans", "Addison Wesley", "isbn-2", null, null));

        when(repository.findAllByIsbnIn(List.of("isbn-2"))).thenReturn(Optional.of(books));
        when(bookMapper.toDtoList(books)).thenReturn(expectedDtos);

        List<BookDto> result = bookService.findAllByIsbns(List.of("isbn-2"));

        assertThat(result).isEqualTo(expectedDtos);
    }

    @Test
    void findById_whenBookDoesNotExist_throwsBookNotFoundException() {
        when(repository.findById("book-3")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.findById("book-3"))
                .isInstanceOf(BookNotFoundException.class)
                .hasMessageContaining("book-3");
    }
}