package com.biglibon.sharedlibrary.consumer;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

//@Component
//public class KafkaEventConsumerRegistry {
//
//    private final Map<String, KafkaEventConsumer<?>> handlers = new HashMap<>();
//    private final Map<String, Class<?>> types = new HashMap<>();
//
//    public <T> void register(String topic, Class<T> clazz, KafkaEventConsumer<T> handler) {
//        handlers.put(topic, handler);
//        types.put(topic, clazz);
//    }
//
//    public Optional<KafkaEventConsumer<?>> getHandler(String topic) {
//        return Optional.ofNullable(handlers.get(topic));
//    }
//
//    public Optional<Class<?>> getType(String topic) {
//        return Optional.ofNullable(types.get(topic));
//    }
//
//}

@Component
@Slf4j
public class KafkaEventConsumerRegistry {

    // register consumers and events by topic

    private final Map<String, KafkaEventConsumer<?>> topicKafkaEventConsumers = new HashMap<>();
    private final Map<String, Class<?>> topicKafkaEvents = new HashMap<>();

    public <T> void register(String topic, Class<T> kafkaEvent, KafkaEventConsumer<T> kafkaEventConsumer) {
        topicKafkaEventConsumers.put(topic, kafkaEventConsumer);
        topicKafkaEvents.put(topic, kafkaEvent);
    }

    public Optional<KafkaEventConsumer<?>> getKafkaEventConsumer(String topic) {
        return Optional.ofNullable(topicKafkaEventConsumers.get(topic));
    }

    public Optional<Class<?>> getKafkaEvent(String topic) {
        return Optional.ofNullable(topicKafkaEvents.get(topic));
    }

    @PostConstruct
    public void debugRegisteredConsumers() {
        log.info("Consumers registered: {}", topicKafkaEventConsumers.keySet());
    }

}
