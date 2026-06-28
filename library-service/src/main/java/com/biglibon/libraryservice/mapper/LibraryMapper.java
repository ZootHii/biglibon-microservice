package com.biglibon.libraryservice.mapper;

import com.biglibon.sharedlibrary.dto.CreateLibraryRequest;
import com.biglibon.sharedlibrary.dto.LibraryDto;
import com.biglibon.libraryservice.model.Library;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LibraryMapper {

    @Mapping(target = "books", ignore = true)
    LibraryDto toDto(Library library);

    @Mapping(target = "bookIds", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Library toEntity(LibraryDto libraryDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "bookIds", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Library toEntityFromCreateLibraryRequest(CreateLibraryRequest request);
}
