package com.biglibon.sharedlibrary.config;

import com.biglibon.sharedlibrary.constant.KafkaConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Slf4j
@Configuration
public class KafkaConfig {

    private static final long RETRY_INTERVAL_MS = 1_000L;
    private static final long RETRY_COUNT = 3L;

    @Bean
    public NewTopic bookEventsTopic() {
        return TopicBuilder.name(KafkaConstants.Book.TOPIC)
                // Since we have 2 microservice replicas each service can register one partition
                .partitions(2)
                // Since we are running 4 brokers we can have copy of the topic in each broker for more redundancy
                .replicas(4)
                .build();
    }

    @Bean
    public NewTopic libraryEventsTopic() {
        return TopicBuilder.name(KafkaConstants.Library.TOPIC)
                .partitions(2)
                .replicas(4)
                .build();
    }

    @Bean
    public DefaultErrorHandler kafkaErrorHandler(KafkaTemplate<String, String> kafkaTemplate) {
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(kafkaTemplate);
        // Consumer exception fırlatırsa Kafka önce retry dener, sonra mesajı *.DLT topic'ine taşır.
        return new DefaultErrorHandler(recoverer, new FixedBackOff(RETRY_INTERVAL_MS, RETRY_COUNT));
    }

    @Bean
    public NewTopic bookEventsDltTopic() {
        return TopicBuilder.name(KafkaConstants.Book.TOPIC + ".DLT")
                .partitions(2)
                .replicas(4)
                .build();
    }

    @Bean
    public NewTopic libraryEventsDltTopic() {
        return TopicBuilder.name(KafkaConstants.Library.TOPIC + ".DLT")
                .partitions(2)
                .replicas(4)
                .build();
    }
}
