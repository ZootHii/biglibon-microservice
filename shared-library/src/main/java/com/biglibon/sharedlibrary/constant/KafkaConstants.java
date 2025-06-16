package com.biglibon.sharedlibrary.constant;

public final class KafkaConstants {

    public static final class Book {
        public static final String TOPIC = "book-events";
        public static final String PRODUCER = "book-service";
        public static final String ADD_BOOK_EVENT = "add-book";
        public static final String CONSUMER_GROUP = "book-service-consumer-group";
    }

    public static final class Library {
        public static final String TOPIC = "library-events";
        public static final String PRODUCER = "library-service";
        public static final String ADD_BOOK_TO_LIBRARY_EVENT = "add-book-to-library";
        public static final String CONSUMER_GROUP = "library-service-consumer-group";
    }

    public static final class Catalog {
        public static final String TOPIC = "catalog-events";
        public static final String PRODUCER = "catalog-service";
        public static final String CONSUMER_GROUP = "catalog-service-consumer-group";
    }
}
