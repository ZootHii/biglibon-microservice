package com.biglibon.libraryservice.service;

import com.biglibon.libraryservice.dto.AddBooksToLibraryByIsbnsRequest;
import com.biglibon.sharedlibrary.client.BookServiceClient;
import com.biglibon.libraryservice.dto.AddBooksToLibraryByBookIdsRequest;
import com.biglibon.libraryservice.mapper.LibraryMapper;
import com.biglibon.libraryservice.model.Library;
import com.biglibon.sharedlibrary.constant.KafkaConstants;
import com.biglibon.sharedlibrary.consumer.KafkaEvent;
import com.biglibon.sharedlibrary.dto.CreateLibraryRequest;
import com.biglibon.sharedlibrary.dto.LibraryDto;
import com.biglibon.libraryservice.repository.LibraryRepository;
import com.biglibon.sharedlibrary.dto.BookDto;
import com.biglibon.sharedlibrary.exception.BookNotFoundException;
import com.biglibon.sharedlibrary.exception.LibraryNotFoundException;
import com.biglibon.sharedlibrary.producer.KafkaEventProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class LibraryService {

    private final LibraryRepository repository;
    private final BookServiceClient bookServiceClient;
    private final LibraryMapper libraryMapper;
    private final KafkaEventProducer kafkaEventProducer;

    public LibraryService(LibraryRepository repository, BookServiceClient bookServiceClient,
                          LibraryMapper libraryMapper, KafkaEventProducer kafkaEventProducer) {
        this.repository = repository;
        this.bookServiceClient = bookServiceClient;
        this.libraryMapper = libraryMapper;
        this.kafkaEventProducer = kafkaEventProducer;
    }

    // OUTBOX pattern gerekir
    @Transactional
    public LibraryDto createLibrary(CreateLibraryRequest request) {
        Library library = libraryMapper.toEntityFromCreateLibraryRequest(request);

        // check library exists / later

        List<String> requestedIsbns = request.getBookIsbns();
        List<BookDto> validBookDtos = requestedIsbns.isEmpty()
                ? List.of()
                : getValidBooksByBookIsbns(requestedIsbns);

        library.setBookIds(validBookDtos.stream()
                .map(BookDto::id)
                .toList());

        Library savedLibrary = repository.save(library);
        LibraryDto libraryDto = new LibraryDto(savedLibrary.getId(),
                savedLibrary.getName(),
                savedLibrary.getCity(),
                savedLibrary.getPhone(),
                validBookDtos);

        publishLibraryEventIfNeeded(libraryDto, validBookDtos);

        return libraryDto;
    }

    private void publishLibraryEventIfNeeded(LibraryDto libraryDto, List<BookDto> validBookDtos) {
        if (validBookDtos.isEmpty()) {
            return;
        }

        kafkaEventProducer.send(new KafkaEvent<>(
                KafkaConstants.Library.TOPIC,
                KafkaConstants.Library.ADD_BOOK_TO_LIBRARY_EVENT,
                KafkaConstants.Library.PRODUCER,
                libraryDto
        ));
    }

    // o n+1 ama çokta önemli değil şimdilik / tüm bookid ler ile tek call yapıp hashmap içinde tutup library e map edilebilir
    public List<LibraryDto> getAllLibraries() {
        return repository.findAll().stream()
                .map(this::replaceBookIdsWithBooks)
                .toList();
    }

    // outbox pattern gerekir
    // optimistic lock entity içinde long version @Version
    // hızlandırmak için library içinde belki id nin yanında isbnleri de tutabiliriz database de, denormalize
    @Transactional
    public LibraryDto addBooksToLibraryByIsbns(AddBooksToLibraryByIsbnsRequest request) {
        Library library = getLibraryById(request.getLibraryId());

        // get valid book with correct isbn
        List<BookDto> validBooks = getValidBooksByBookIsbns(request.getBookIsbns());

        // hash used for better performance for lookup O(1)
        Set<String> existingBookIds = new HashSet<>(library.getBookIds());

        // list of valid book ids
        List<String> validBookIds = validBooks.stream()
                .map(BookDto::id)
                .toList();

        // Only add bookIds not already present
        validBookIds.stream()
                .filter(existingBookIds::add)  // add returns false if element already exists > filter removes duplicates
                .forEach(library.getBookIds()::add);

        Library updatedLibrary = repository.save(library);

        // change ids with books
        LibraryDto libraryDto = replaceBookIdsWithBooks(updatedLibrary);

        // send kafka event
        kafkaEventProducer.send(new KafkaEvent<>(
                KafkaConstants.Library.TOPIC,
                KafkaConstants.Library.ADD_BOOK_TO_LIBRARY_EVENT,
                KafkaConstants.Library.PRODUCER,
                libraryDto));

        return libraryDto;
    }

    @Transactional
    public LibraryDto addBooksToLibraryByBookIds(AddBooksToLibraryByBookIdsRequest request) {
        Library library = getLibraryById(request.libraryId());

        // hash used for better performance for lookup O(1)
        Set<String> existingBookIds = new HashSet<>(library.getBookIds());

        // Only add bookIds not already present
        request.bookIds().stream()
                .filter(existingBookIds::add)  // add returns false if element already exists > filter removes duplicates
                .forEach(library.getBookIds()::add);


        Library updatedLibrary = repository.save(library);

        // change ids with books
        LibraryDto libraryDto = replaceBookIdsWithBooks(updatedLibrary);

        // send kafka event
        kafkaEventProducer.send(new KafkaEvent<>(
                KafkaConstants.Library.TOPIC,
                KafkaConstants.Library.ADD_BOOK_TO_LIBRARY_EVENT,
                KafkaConstants.Library.PRODUCER,
                libraryDto));

        return libraryDto;
    }

    public LibraryDto findLibraryWithBooksById(Long id) {
        Library library = getLibraryById(id);
        return replaceBookIdsWithBooks(library);
    }

    private Library getLibraryById(Long libraryId) {
        return repository.findById(libraryId)
                .orElseThrow(() -> new LibraryNotFoundException("Library could not found by id:" + libraryId));
    }

    public LibraryDto replaceBookIdsWithBooks(Library library) {
        LibraryDto libraryDto = libraryMapper.toDto(library);

        List<BookDto> books = bookServiceClient.getAllByIds(library.getBookIds()).getBody();

        libraryDto.setBooks(books);
        return libraryDto;
    }

    private List<BookDto> getValidBooksByBookIsbns(List<String> bookIsbns) {
        List<BookDto> validBooks = bookServiceClient.getAllByIsbns(bookIsbns).getBody();

        Set<String> validBookIsbnSet = Objects.requireNonNull(validBooks).stream()
                .map(BookDto::isbn)
                .collect(Collectors.toSet());

        if (validBookIsbnSet.isEmpty()) {
            log.warn("There are no valid ISBNs.");
            throw new BookNotFoundException("There are no valid ISBNs.");
        }

        List<String> invalidIsbns = bookIsbns.stream()
                .filter(originalBookIsbn -> !validBookIsbnSet.contains(originalBookIsbn))
                .toList();

        if (!invalidIsbns.isEmpty()) {
            // we can throw error or just notification like there are some invalid ISBNs {} proceeding with valid ISBNs.
            log.warn("There are some invalid ISBNs {} proceeding with valid ISBNs.", invalidIsbns);
        }

        return validBooks;
    }

    // TESTING
    public List<BookDto> getAllBooksFromLibraryService() {
        return bookServiceClient.getAll().getBody();
    }

    // TESTING
    public BookDto getBookByIdFromLibraryService(String id) {
        return bookServiceClient.getById(id).getBody();
    }
}
