package com.biglibon.sharedlibrary.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LibrarySummaryDto {
    private Long libraryId;
    private String name;
    private String city;
    private String phone;
    // private boolean availability;
}
