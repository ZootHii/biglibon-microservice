package com.biglibon.libraryservice.service;

import com.biglibon.sharedlibrary.client.BookServiceClient;
import com.biglibon.libraryservice.dto.AddBooksToLibraryByIdsRequest;
import com.biglibon.libraryservice.mapper.LibraryMapper;
import com.biglibon.libraryservice.model.Library;
import com.biglibon.sharedlibrary.constant.KafkaConstants;
import com.biglibon.sharedlibrary.consumer.KafkaEvent;
import com.biglibon.sharedlibrary.dto.LibraryDto;
import com.biglibon.libraryservice.repository.LibraryRepository;
import com.biglibon.sharedlibrary.dto.BookDto;
import com.biglibon.sharedlibrary.exception.LibraryNotFoundException;
import com.biglibon.sharedlibrary.producer.KafkaEventProducer;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

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

    public LibraryDto create(LibraryDto libraryDto) {
        return libraryMapper.toDto(repository.save(libraryMapper.toEntity(libraryDto)), bookServiceClient);
    }

    public List<LibraryDto> findAll() {
        return libraryMapper.toDtoList(repository.findAll(), bookServiceClient);
    }

    public void addBooksToLibraryByIds(AddBooksToLibraryByIdsRequest addBooksToLibraryByIdsRequest) {
        Library library = repository.findById(addBooksToLibraryByIdsRequest.libraryId())
                .orElseThrow(() -> new LibraryNotFoundException("Library not found"));

        addBooksToLibraryByIdsRequest.bookId().forEach(bookId -> {
            if (!library.getBookIds().contains(bookId)) {
                library.getBookIds().add(bookId);
            }
        });

        Library updatedLibrary = repository.save(library);

        // change ids with books
        LibraryDto libraryDto = libraryMapper.toDto(updatedLibrary, bookServiceClient);
        kafkaEventProducer.send(new KafkaEvent<>(
                KafkaConstants.Library.TOPIC,
                KafkaConstants.Library.ADD_BOOK_TO_LIBRARY_EVENT,
                KafkaConstants.Library.PRODUCER,
                libraryDto));
    }

    public LibraryDto findWithBooksById(Long id) {
        Library library = repository.findById(id)
                .orElseThrow(() -> new LibraryNotFoundException("Library could not found by id:" + id));
        return libraryMapper.toDto(library, bookServiceClient);
    }

    public List<BookDto> getAllBooksFromLibraryService() {
        return bookServiceClient.getAll().getBody();
    }

    public BookDto getBookByIdFromLibraryService(String id) {
        return bookServiceClient.getById(id).getBody();
    }




//    public List<LibraryDto> replaceBookIdsWithBooks(List<Library> libraries) {
//
//        bookServiceClient.getAllByIds(libraries)
//
//        // Feign çağrısı ve fallback kontrol burada
//        List<BookDto> books;
//        try {
//            books = Optional.ofNullable(bookServiceClient.getAllByIds(library.getBookIds()).getBody())
//                    .orElse(Collections.emptyList());
//        } catch (Exception e) {
//            // fallback veya default boş liste
//            books = Collections.emptyList();
//        }
//
//        dto.setBooks(books);
//        return dto;
//    }

//    public void addBookToLibraryByIsbn(AddBookRequestDto addBookRequestDto) {
//        String isbn = bookServiceClient.getByIsbn(addBookRequestDto.isbn()).getBody().isbn();
//        Library library = repository.findById(addBookRequestDto.libraryId())
//                .orElseThrow(() -> new LibraryNotFoundException("Library could not found by id:" + addBookRequestDto.libraryId()));
//        library.getBooks().add(isbn);
//        repository.save(library);
//    }


//    public LibraryDto findAllBooksInLibraryById(Long id) {
//        Library library = repository.findById(id)
//                .orElseThrow(() -> new LibraryNotFoundException("Library could not found by id:" + id));
//        library.getBooks()
//                .forEach(isbn -> bookServiceClient.getByIsbn(isbn).getBody());
//
//
//        List<String> isbnList = library.getBooks();
//        List<BookDto> bookDtos = new ArrayList<>();
//
//        for (String isbn : isbnList) {
//            bookDtos.add(bookServiceClient.getByIsbn(isbn).getBody());
//        }
//
//
//        return new LibraryDto(id, bookDtos);
//    }
//


}
