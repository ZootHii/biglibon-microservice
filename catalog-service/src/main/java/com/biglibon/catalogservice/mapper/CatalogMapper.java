package com.biglibon.catalogservice.mapper;

import com.biglibon.catalogservice.model.Catalog;
import com.biglibon.sharedlibrary.dto.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CatalogMapper {

    CatalogDto toDto(Catalog catalog);

    List<CatalogDto> toDtoList(List<Catalog> catalogList);

    Catalog toEntity(CatalogDto catalogDto);

    List<Catalog> toEntityList(List<CatalogDto> catalogDtoList);

    @Mapping(target = "bookId", source = "id")
    BookSummaryDto bookDtoToBookSummaryDto(BookDto bookDto);

    @Mapping(target = "libraryId", source = "id")
    LibrarySummaryDto libraryDtoToLibrarySummaryDto(LibraryDto libraryDto);
}
