package com.biglibon.sharedlibrary.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;

@Slf4j
public abstract class KafkaConsumer {

    protected final KafkaEventDispatcher dispatcher;

    public KafkaConsumer(KafkaEventDispatcher dispatcher) {
        log.info("CONSUME: KafkaConsumer dispatcher {}", dispatcher);
        this.dispatcher = dispatcher;
    }

    public abstract void consume(ConsumerRecord<String, String> record);

//    @KafkaListener(
//            topics = {KafkaTopics.BS_BOOK_ADDED, KafkaTopics.LS_LIBRARY_UPDATED},
//            groupId = "biglibon-consumer-group"
//    )
//    public void consumeMessage(ConsumerRecord<String, ?> consumerRecord) {
//        String topic = consumerRecord.topic();
//        Object event = consumerRecord.value();
//
//        kafkaEventConsumerRegistry.getKafkaEvent(topic).ifPresentOrElse(kafkaEvent -> {
//            try {
//                Object eventData = objectMapper.readValue((JsonParser) event, kafkaEvent);
//                kafkaEventConsumerRegistry.getKafkaEventConsumer(topic).ifPresent(kafkaEventConsumer -> {
//                    ((KafkaEventConsumer<Object>) kafkaEventConsumer).consumeEvent(eventData);
//                });
//                log.info("UserCreatedEventConsumer.consumeApprovalRequestResultedEvent consumed EVENT :{} " +
//                                "from partition : {} " +
//                                "with offset : {} " +
//                                "thread : {} " +
//                                "for message key: {}",
//                        eventData, consumerRecord.partition(), consumerRecord.offset(), Thread.currentThread().getName(), consumerRecord.key());
//            } catch (Exception e) {
//                System.err.println("Deserialization error: " + e.getMessage());
//            }
//        }, () -> System.err.println("No kafka event consumer registered for topic: " + topic));
//    }
}
