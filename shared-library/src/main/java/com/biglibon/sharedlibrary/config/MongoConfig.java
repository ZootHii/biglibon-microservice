package com.biglibon.sharedlibrary.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
// Mongo will add create and update times automatically
@EnableMongoAuditing
public class MongoConfig {
}
