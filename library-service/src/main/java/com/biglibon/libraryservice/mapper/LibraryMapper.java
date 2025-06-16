package com.biglibon.libraryservice.mapper;

import com.biglibon.sharedlibrary.client.BookServiceClient;

import com.biglibon.sharedlibrary.dto.LibraryDto;
import com.biglibon.libraryservice.model.Library;
import com.biglibon.sharedlibrary.dto.BookDto;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LibraryMapper {


    @Mapping(target = "books", source = "bookIds")
    LibraryDto toDto(Library library, @Context BookServiceClient bookServiceClient);

    // feign client buradan kaldırılacak service içinde handle edilecek burada sadece map
    @Mapping(target = "books", source = "bookIds")
    List<LibraryDto> toDtoList(List<Library> libraryList, @Context BookServiceClient bookServiceClient);

    Library toEntity(LibraryDto libraryDto);

    List<Library> toEntityList(List<LibraryDto> libraryDtoList);

    default List<BookDto> mapBookIds(List<String> bookIds, @Context BookServiceClient bookServiceClient) {
        return bookServiceClient.getAllByIds(bookIds).getBody();
    }


//    default BookDto mapBookId(String bookId, @Context BookServiceClient bookServiceClient) {
//        return bookServiceClient.getById(bookId).getBody();
//    }


}
