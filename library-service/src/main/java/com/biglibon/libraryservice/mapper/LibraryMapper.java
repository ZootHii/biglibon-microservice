package com.biglibon.libraryservice.mapper;

import com.biglibon.sharedlibrary.dto.LibraryDto;
import com.biglibon.libraryservice.model.Library;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LibraryMapper {

    LibraryDto toDto(Library library);

    Library toEntity(LibraryDto libraryDto);
}
