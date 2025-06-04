//package com.biglibon.libraryservice.client;
//
//import com.biglibon.libraryservice.exception.BookNotFoundException;
//import feign.Response;
//import feign.codec.ErrorDecoder;
//import org.apache.commons.io.IOUtils;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpStatus;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.nio.charset.StandardCharsets;
//
//@Configuration
//public class RetreiveMessageErrorDecoder implements ErrorDecoder {
//
//    private final ErrorDecoder errorDecoder = new Default();
//
//    @Override
//    public Exception decode(String methodKey, Response response) {
//        ExceptionMessage message;
//        try (InputStream body = response.body().asInputStream()) {
//            message = new ExceptionMessage(
//                    response.headers().get("date").toArray()[0].toString(),
//                    response.status(),
//                    HttpStatus.resolve(response.status()).getReasonPhrase(),
//                    IOUtils.toString(body, StandardCharsets.UTF_8),
//                    response.request().url());
//
//        } catch (IOException e) {
//            return new Exception(e.getMessage());
//        }
//        switch (response.status()) {
//            case 404:
//                throw new BookNotFoundException(message);
//            default:
//                return errorDecoder.decode(methodKey, response);
//        }
//    }
//}
