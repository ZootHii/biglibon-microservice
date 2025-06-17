package com.biglibon.bookservice.mapper;

import com.biglibon.bookservice.model.Book;
import com.biglibon.sharedlibrary.dto.BookDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BookMapper {

    BookDto toDto(Book book);

    List<BookDto> toDtoList(List<Book> bookList);

    Book toEntity(BookDto bookDto);

    List<Book> toEntityList(List<BookDto> bookDtoList);
}
