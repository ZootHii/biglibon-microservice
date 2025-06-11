package com.biglibon.sharedlibrary.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class KafkaEventDispatcher {

    private final Map<String, KafkaEventHandler> handlerMap;

    public KafkaEventDispatcher(List<KafkaEventHandler> handlers) {
        this.handlerMap = handlers.stream()
                .collect(Collectors.toMap(KafkaEventHandler::getKey, h -> h));
        log.info("CONSUME: KafkaEventDispatcher handlers {}, handlerMap {}", handlers, handlerMap);
    }

    public void dispatch(String groupId, String topic, String payload) {
        log.info("CONSUME: KafkaEventDispatcher groupId {}, topic {}, payload {}", groupId, topic, payload);
        String key = groupId + ":" + topic;
        KafkaEventHandler handler = handlerMap.get(key);
        log.info("CONSUME: KafkaEventDispatcher handlerMap.getKey {}", handler);

        if (handler != null) {
            handler.handle(payload);
        } else {
            log.error("No handler found for key: {}", key);
        }
    }
}
