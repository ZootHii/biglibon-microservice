package com.biglibon.libraryservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddBooksToLibraryByIsbnsRequest {
    Long libraryId;
    List<String> bookIsbns;
}
