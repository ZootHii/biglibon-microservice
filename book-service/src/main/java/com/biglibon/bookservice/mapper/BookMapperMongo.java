package com.biglibon.bookservice.mapper;

import com.biglibon.bookservice.dto.BookDtoMongo;
import com.biglibon.bookservice.model.BookMongo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BookMapperMongo {

    BookMapperMongo INSTANCE = Mappers.getMapper(BookMapperMongo.class);

    //@Mapping(source = "numberOfSeats", target = "seatCount")
    BookDtoMongo toDto(BookMongo book);

    List<BookDtoMongo> toDtoList(List<BookMongo> bookList);

    BookMongo toEntity(BookDtoMongo bookDto);

    List<BookMongo> toEntityList(List<BookDtoMongo> bookDtoList);
}
