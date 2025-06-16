package com.biglibon.catalogservice.mapper;

import com.biglibon.catalogservice.model.Catalog;
import com.biglibon.sharedlibrary.client.BookServiceClient;
import com.biglibon.sharedlibrary.dto.*;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CatalogMapper {


    //@Mapping(source = "numberOfSeats", target = "seatCount")
    CatalogDto toDto(Catalog catalog);

    List<CatalogDto> toDtoList(List<Catalog> catalogList);

    Catalog toEntity(CatalogDto catalogDto);

    List<Catalog> toEntityList(List<CatalogDto> catalogDtoList);

    @Mapping(target = "bookId", source = "id")
    BookSummaryDto bookDtoToBookSummaryDto(BookDto bookDto);

    @Mapping(target = "libraryId", source = "id")
    LibrarySummaryDto libraryDtoToLibrarySummaryDto(LibraryDto libraryDto);

//    @Mapping(target = "books", source = "bookIds")
//    LibraryDto toDto(Library library, @Context BookServiceClient bookServiceClient);
//
//    @Mapping(target = "books", source = "bookIds")
//    List<LibraryDto> toDtoList(List<Library> libraryList, @Context BookServiceClient bookServiceClient);
//
//    Library toEntity(LibraryDto libraryDto);
//
//    List<Library> toEntityList(List<LibraryDto> libraryDtoList);
//
//    default List<BookDto> mapBookIds(List<String> bookIds, @Context BookServiceClient bookServiceClient) {
//        return bookServiceClient.getAllByIds(bookIds).getBody();
//    }
}
