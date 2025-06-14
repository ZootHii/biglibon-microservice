package com.biglibon.sharedlibrary.config;

import com.biglibon.sharedlibrary.constant.KafkaTopics;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Slf4j
@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic bookAddedTopic() {
        return TopicBuilder.name(KafkaTopics.BS_BOOK_ADDED)
                .partitions(2) // since we have 2 microservice replicas each service can register one partition
                .replicas(4) // since we are running 4 brokers we can have copy of the topic in each broker more redundancy
                .build();
    }

    @Bean
    public NewTopic libraryUpdatedTopic() {
        return TopicBuilder.name(KafkaTopics.LS_LIBRARY_UPDATED)
                .partitions(2)
                .replicas(4)
                .build();
    }
}


