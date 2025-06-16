package com.biglibon.sharedlibrary.config;

import com.biglibon.sharedlibrary.constant.KafkaConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Slf4j
@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic bookEventsTopic() {
        return TopicBuilder.name(KafkaConstants.Book.TOPIC)
                .partitions(2) // since we have 2 microservice replicas each service can register one partition
                .replicas(4) // since we are running 4 brokers we can have copy of the topic in each broker more redundancy
                .build();
    }

    @Bean
    public NewTopic libraryEventsTopic() {
        return TopicBuilder.name(KafkaConstants.Library.TOPIC)
                .partitions(2)
                .replicas(4)
                .build();
    }
}


