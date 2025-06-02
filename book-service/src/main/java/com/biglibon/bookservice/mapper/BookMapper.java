//package com.biglibon.bookservice.mapper;
//
//import com.biglibon.bookservice.dto.BookDto;
//import com.biglibon.bookservice.model.Book;
//import org.mapstruct.Mapper;
//import org.mapstruct.factory.Mappers;
//
//import java.util.List;
//
//@Mapper(componentModel = "spring")
//public interface BookMapper {
//
//    BookMapper INSTANCE = Mappers.getMapper(BookMapper.class);
//
//    //@Mapping(source = "numberOfSeats", target = "seatCount")
//    BookDto toDto(Book book);
//
//    List<BookDto> toDtoList(List<Book> bookList);
//
//    Book toEntity(BookDto bookDto);
//
//    List<Book> toEntityList(List<BookDto> bookDtoList);
//}
