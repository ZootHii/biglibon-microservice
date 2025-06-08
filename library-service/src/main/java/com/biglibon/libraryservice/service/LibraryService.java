package com.biglibon.libraryservice.service;

import com.biglibon.libraryservice.client.BookServiceClient;
import com.biglibon.libraryservice.dto.AddBooksToLibraryByIdsRequestDto;
import com.biglibon.libraryservice.mapper.LibraryMapper;
import com.biglibon.libraryservice.model.Library;
import com.biglibon.libraryservice.dto.LibraryDto;
import com.biglibon.libraryservice.repository.LibraryRepository;
import com.biglibon.sharedlibrary.dto.BookDto;
import com.biglibon.sharedlibrary.exception.LibraryNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class LibraryService {

    private final LibraryRepository repository;
    private final BookServiceClient bookServiceClient;
    private final LibraryMapper libraryMapper;

    public LibraryService(LibraryRepository repository, BookServiceClient bookServiceClient, LibraryMapper libraryMapper) {
        this.repository = repository;
        this.bookServiceClient = bookServiceClient;
        this.libraryMapper = libraryMapper;
    }

    public LibraryDto create(LibraryDto libraryDto) {
        return libraryMapper.toDto(repository.save(libraryMapper.toEntity(libraryDto)), bookServiceClient);
    }

    public List<LibraryDto> findAll() {
        return libraryMapper.toDtoList(repository.findAll(), bookServiceClient);
    }

    public void addBooksToLibraryByIds(AddBooksToLibraryByIdsRequestDto addBooksToLibraryByIdsRequestDto) {
        Library library = repository.findById(addBooksToLibraryByIdsRequestDto.libraryId())
                .orElseThrow(() -> new LibraryNotFoundException("Library not found"));
        addBooksToLibraryByIdsRequestDto.bookId().forEach(bookId -> {
            if (!library.getBookIds().contains(bookId)) {
                library.getBookIds().add(bookId);
            }
        });
        repository.save(library);
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
