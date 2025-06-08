package com.biglibon.sharedlibrary.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FeignErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder errorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        ExceptionDetails exceptionDetails;
        try (InputStream body = response.body().asInputStream()) {
            ObjectMapper objectMapper = new ObjectMapper();
            exceptionDetails = objectMapper.readValue(body.readAllBytes(), ExceptionDetails.class);
        } catch (IOException e) {
            return new Exception(e.getMessage());
        }
        switch (response.status()) {
            case 404:
                throw new BookNotFoundException(exceptionDetails.getMessage());
            default:
                return errorDecoder.decode(methodKey, response);
        }
    }
}
