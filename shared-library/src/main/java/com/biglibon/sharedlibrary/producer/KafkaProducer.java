package com.biglibon.sharedlibrary.producer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class KafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(String topic, String message) {
        final CompletableFuture<SendResult<String, String>> futureResult = kafkaTemplate.send(topic, message);

        futureResult.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Unable to deliver message to Kafka", ex);
                return;
            }
            if (Objects.isNull(result)) {
                log.info("Empty result on success for topic {} and payload {}", topic, message);
                return;
            }
            log.info("Message :{} published, topic : {}, partition : {} and offset : {}",
                    message,
                    result.getRecordMetadata().topic(),
                    result.getRecordMetadata().partition(),
                    result.getRecordMetadata().offset());
        });
    }
}
