package com.biglibon.libraryservice.service;

import com.biglibon.libraryservice.dto.AddBooksToLibraryByIsbnsRequest;
import com.biglibon.libraryservice.mapper.LibraryMapper;
import com.biglibon.libraryservice.model.Library;
import com.biglibon.libraryservice.repository.LibraryRepository;
import com.biglibon.sharedlibrary.client.BookServiceClient;
import com.biglibon.sharedlibrary.constant.KafkaConstants;
import com.biglibon.sharedlibrary.consumer.KafkaEvent;
import com.biglibon.sharedlibrary.dto.BookDto;
import com.biglibon.sharedlibrary.dto.CreateLibraryRequest;
import com.biglibon.sharedlibrary.dto.LibraryDto;
import com.biglibon.sharedlibrary.exception.BookNotFoundException;
import com.biglibon.sharedlibrary.producer.KafkaEventProducer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LibraryServiceTest {

    @Mock
    private LibraryRepository repository;

    @Mock
    private BookServiceClient bookServiceClient;

    @Mock
    private LibraryMapper libraryMapper;

    @Mock
    private KafkaEventProducer kafkaEventProducer;

    @InjectMocks
    private LibraryService libraryService;

    @Test
    void createLibrary_whenValidRequest() {
        CreateLibraryRequest request = new CreateLibraryRequest();
        request.setName("Sivas Merkez Kütüphane");
        request.setCity("Sivas");
        request.setPhone("(0346) 221 11 12");
        request.setBookIsbns(List.of("isbn-1", "isbn-2"));

        Library library = new Library("Sivas Merkez Kütüphane", "Sivas", "(0346) 221 11 12", new ArrayList<>());
        Library savedLibrary = new Library("Sivas Merkez Kütüphane", "Sivas", "(0346) 221 11 12", List.of("book-1", "book-2"));
        savedLibrary.setId(58L);

        Instant now = Instant.parse("2026-02-04T10:04:42.525821Z");
        List<BookDto> books = List.of(
                new BookDto("book-1", "Book One", 2020, "Author One", "Publisher One", "isbn-1", now, now),
                new BookDto("book-2", "Book Two", 2021, "Author Two", "Publisher Two", "isbn-2", now, now)
        );

        when(libraryMapper.toEntityFromCreateLibraryRequest(request)).thenReturn(library);
        when(bookServiceClient.getAllByIsbns(request.getBookIsbns())).thenReturn(ResponseEntity.ok(books));
        when(repository.save(library)).thenReturn(savedLibrary);

        LibraryDto result = libraryService.createLibrary(request);

        assertThat(result.getId()).isEqualTo(58L);
        assertThat(result.getBooks()).containsExactlyElementsOf(books);

        ArgumentCaptor<Library> libraryCaptor = ArgumentCaptor.forClass(Library.class);
        verify(repository).save(libraryCaptor.capture());
        assertThat(libraryCaptor.getValue().getBookIds()).containsExactly("book-1", "book-2");

        ArgumentCaptor<KafkaEvent<LibraryDto>> eventCaptor = ArgumentCaptor.forClass(KafkaEvent.class);
        verify(kafkaEventProducer).send(eventCaptor.capture());
        KafkaEvent<LibraryDto> event = eventCaptor.getValue();
        assertThat(event.getTopic()).isEqualTo(KafkaConstants.Library.TOPIC);
        assertThat(event.getEvent()).isEqualTo(KafkaConstants.Library.ADD_BOOK_TO_LIBRARY_EVENT);
        assertThat(event.getProducer()).isEqualTo(KafkaConstants.Library.PRODUCER);
        assertThat(event.getPayload().getBooks()).containsExactlyElementsOf(books);
    }

    @Test
    void createLibrary_whenBookIsbnsNull_savesLibraryWithoutBookLookup() {
        CreateLibraryRequest request = new CreateLibraryRequest();
        request.setName("Sivas Merkez Kütüphane");
        request.setCity("Ankara"); //:P
        request.setPhone("(0346) 221 11 12");
        // books null

        Library library = new Library("Sivas Merkez Kütüphane", "Ankara", "(0346) 221 11 12");
        Library savedLibrary = new Library("Sivas Merkez Kütüphane", "Ankara", "5(0346) 221 11 12", List.of());
        savedLibrary.setId(58L);

        when(libraryMapper.toEntityFromCreateLibraryRequest(request)).thenReturn(library);
        when(repository.save(library)).thenReturn(savedLibrary);

        LibraryDto result = libraryService.createLibrary(request);

        assertThat(result.getBooks()).isEmpty();
        verifyNoInteractions(bookServiceClient);
        verify(kafkaEventProducer, never()).send(any());
    }

    @Test
    void addBooksToLibraryByIsbns_whenValidRequest() {
        Library library = new Library("Sivas Merkez Kütüphane", "Ankara", "(0346) 221 11 12", new ArrayList<>(List.of("book-1")));
        library.setId(6L);

        Instant now = Instant.parse("2026-02-04T10:04:42.525821Z");
        List<BookDto> validBooks = List.of(
                new BookDto("book-2", "Book Two", 2021, "Author Two", "Publisher Two", "isbn-2", now, now),
                new BookDto("book-3", "Book Three", 2022, "Author Three", "Publisher Three", "isbn-3", now, now)
        );

        Library updatedLibrary = new Library("Sivas Merkez Kütüphane", "Ankara", "(0346) 221 11 12", List.of("book-1", "book-2", "book-3"));
        updatedLibrary.setId(6L);

        LibraryDto libraryDto = new LibraryDto(6L, "Sivas Merkez Kütüphane", "Ankara", "(0346) 221 11 12", null);
        List<BookDto> allBooks = new ArrayList<>();
        allBooks.add(new BookDto("book-1", "Book One", 2020, "Author One", "Publisher One", "isbn-1", now, now));
        allBooks.addAll(validBooks);

        when(repository.findById(6L)).thenReturn(Optional.of(library));
        when(bookServiceClient.getAllByIsbns(List.of("isbn-2", "isbn-3"))).thenReturn(ResponseEntity.ok(validBooks));
        when(repository.save(library)).thenReturn(updatedLibrary);
        when(libraryMapper.toDto(updatedLibrary)).thenReturn(libraryDto);
        when(bookServiceClient.getAllByIds(List.of("book-1", "book-2", "book-3"))).thenReturn(ResponseEntity.ok(allBooks));

        AddBooksToLibraryByIsbnsRequest request = new AddBooksToLibraryByIsbnsRequest(6L, List.of("isbn-2", "isbn-3"));
        LibraryDto result = libraryService.addBooksToLibraryByIsbns(request);

        assertThat(result.getBooks()).containsExactlyElementsOf(allBooks);
        assertThat(result.getId()).isEqualTo(6L);

        ArgumentCaptor<Library> libraryCaptor = ArgumentCaptor.forClass(Library.class);
        verify(repository).save(libraryCaptor.capture());
        assertThat(libraryCaptor.getValue().getBookIds()).containsExactly("book-1", "book-2", "book-3");

        ArgumentCaptor<KafkaEvent<LibraryDto>> eventCaptor = ArgumentCaptor.forClass(KafkaEvent.class);
        verify(kafkaEventProducer).send(eventCaptor.capture());
        KafkaEvent<LibraryDto> event = eventCaptor.getValue();
        assertThat(event.getTopic()).isEqualTo(KafkaConstants.Library.TOPIC);
        assertThat(event.getEvent()).isEqualTo(KafkaConstants.Library.ADD_BOOK_TO_LIBRARY_EVENT);
        assertThat(event.getProducer()).isEqualTo(KafkaConstants.Library.PRODUCER);
        assertThat(event.getPayload().getBooks()).containsExactlyElementsOf(allBooks);
    }

    @Test
    void addBooksToLibraryByIsbns_whenIsbnsEmpty_ThrowsError() {
        Library library = new Library("Sivas Merkez Kütüphane", "Ankara", "(0346) 221 11 12", new ArrayList<>());
        library.setId(6L);

        when(repository.findById(6L)).thenReturn(Optional.of(library));
        assertThatThrownBy(() -> libraryService.addBooksToLibraryByIsbns(new AddBooksToLibraryByIsbnsRequest(6L, List.of())))
                .isInstanceOf(BookNotFoundException.class)
                .hasMessageContaining("There are no ISBNs.");
        verifyNoInteractions(bookServiceClient);
        verifyNoInteractions(kafkaEventProducer);
    }
}