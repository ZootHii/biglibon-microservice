package com.biglibon.sharedlibrary.dto;

import lombok.Data;

import java.util.List;

@Data
public class CreateLibraryRequest {
    private String name;
    private String city;
    private String phone;
    private List<String> bookIsbns;
}

