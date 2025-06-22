package com.biglibon.libraryservice.service;

import com.biglibon.sharedlibrary.client.BookServiceClient;
import com.biglibon.libraryservice.dto.AddBooksToLibraryByIdsRequest;
import com.biglibon.libraryservice.mapper.LibraryMapper;
import com.biglibon.libraryservice.model.Library;
import com.biglibon.sharedlibrary.constant.KafkaConstants;
import com.biglibon.sharedlibrary.consumer.KafkaEvent;
import com.biglibon.sharedlibrary.dto.CreateLibraryRequest;
import com.biglibon.sharedlibrary.dto.LibraryDto;
import com.biglibon.libraryservice.repository.LibraryRepository;
import com.biglibon.sharedlibrary.dto.BookDto;
import com.biglibon.sharedlibrary.exception.LibraryNotFoundException;
import com.biglibon.sharedlibrary.producer.KafkaEventProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class LibraryService {

    private final LibraryRepository repository;
    private final BookServiceClient bookServiceClient;
    private final LibraryMapper libraryMapper;
    private final KafkaEventProducer kafkaEventProducer;

    public LibraryService(LibraryRepository repository, BookServiceClient bookServiceClient, LibraryMapper libraryMapper, KafkaEventProducer kafkaEventProducer) {
        this.repository = repository;
        this.bookServiceClient = bookServiceClient;
        this.libraryMapper = libraryMapper;
        this.kafkaEventProducer = kafkaEventProducer;
    }

    public LibraryDto createLibrary(CreateLibraryRequest request) {
        // burada libraryDTO yerine direkt olarak bookid leri de gönderebilmeliyiz
        // library oluştururken bookid yi baştan verebiliriz
        // o yüzden libraryDTO değişecek
        // ve burada bookid ler gerçekten book-servicete var mı bakılacak
        // book id değilde isbn ile devam etmek daha mantıklı olur bana ne bookid den yani

        List<String> originalBookIsbns = request.getBookIsbns();
        List<BookDto> validBookDtos = getValidBooksByBookIsbns(originalBookIsbns);

        Library library = libraryMapper.toEntityFromCreateLibraryRequest(request);
        library.setBookIds(validBookDtos.stream().map(BookDto::id).toList());
        Library savedLibrary = repository.save(library);

        return new LibraryDto(savedLibrary.getId(),
                savedLibrary.getName(),
                savedLibrary.getCity(),
                savedLibrary.getPhone(),
                validBookDtos);
    }

    private List<BookDto> getValidBooksByBookIsbns(List<String> bookIsbns) {
        List<BookDto> validBooks = bookServiceClient.getAllByIsbns(bookIsbns).getBody();

        Set<String> validBookIsbnSet = Objects.requireNonNull(validBooks).stream()
                .map(BookDto::isbn)
                .collect(Collectors.toSet());

        List<String> invalidIsbns = bookIsbns.stream()
                .filter(originalBookIsbn -> !validBookIsbnSet.contains(originalBookIsbn))
                .toList();

        if (!invalidIsbns.isEmpty()) {
            // we can throw error or just notification like there are some invalid ISBNs {} proceeding with valid ISBNs.
            log.warn("There are some invalid ISBNs {} proceeding with valid ISBNs.", invalidIsbns);
        }
        return validBooks;
    }

    public List<LibraryDto> getAllLibraries() {
        return repository.findAll().stream()
                .map(this::replaceBookIdsWithBooks)
                .toList();
    }

    public LibraryDto addBooksToLibraryByIds(AddBooksToLibraryByIdsRequest request) {
        Library library = repository.findById(request.libraryId())
                .orElseThrow(() -> new LibraryNotFoundException("Library not found"));

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

    public LibraryDto findWithBooksById(Long id) {
        Library library = repository.findById(id)
                .orElseThrow(() -> new LibraryNotFoundException("Library could not found by id:" + id));

        return replaceBookIdsWithBooks(library);
    }

    public LibraryDto replaceBookIdsWithBooks(Library library) {
        LibraryDto libraryDto = libraryMapper.toDto(library);

        List<BookDto> books;
        try {
            books = Optional.ofNullable(bookServiceClient.getAllByIds(library.getBookIds()).getBody())
                    .orElse(List.of());
        } catch (Exception e) {
            books = List.of();
        }

        libraryDto.setBooks(books);
        return libraryDto;
    }

    // TESTING
    public List<BookDto> getAllBooksFromLibraryService() {
        return bookServiceClient.getAll().getBody();
    }

    // TESTING
    public BookDto getBookByIdFromLibraryService(String id) {
        return bookServiceClient.getById(id).getBody();
    }

    @Deprecated
    public LibraryDto create(LibraryDto libraryDto) {
        return replaceBookIdsWithBooks(repository.save(libraryMapper.toEntity(libraryDto)));
    }

    @Deprecated
    public List<LibraryDto> findAll() {
        return repository.findAll().stream()
                .map(this::replaceBookIdsWithBooks)
                .toList();
    }
}
