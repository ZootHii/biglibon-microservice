package com.biglibon.sharedlibrary.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExceptionDetails {
    private String timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
}
