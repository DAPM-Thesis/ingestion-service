// src/main/java/com/dapm2/ingestion_service/kafka/KafkaProducerService.java
package com.dapm2.ingestion_service.kafka;

import communication.message.impl.event.Event;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Just swap in the Event‐typed template; no core‐project changes.
 */
@Service
public class KafkaProducerService {

    private final KafkaTemplate<String, Event> kafkaTemplate;

    public KafkaProducerService(KafkaTemplate<String, Event> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEvent(String topic, Event event) {
        kafkaTemplate
                .send(topic, event)                             // returns CompletableFuture<SendResult<...>>
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        System.err.println("Failed to send Event: " + ex.getMessage());
                    } else {
                        System.out.printf(
                                "Sent Event to %s [partition=%d, offset=%d]%n",
                                topic,
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset()
                        );
                    }
                });
    }
}

