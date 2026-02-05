package com.biglibon.libraryservice.service;

import com.biglibon.libraryservice.dto.AddBooksToLibraryByBookIdsRequest;
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