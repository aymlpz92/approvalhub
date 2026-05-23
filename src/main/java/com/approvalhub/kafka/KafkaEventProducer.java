package com.approvalhub.kafka;

import com.approvalhub.dto.status.DocumentStatusEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaEventProducer {

    private final KafkaTemplate<String, DocumentStatusEvent> kafkaTemplate;

    // Topic de destination (externalisé dans le properties)
    @Value("${app.kafka.topics}")
    private String topics;

    // Publier un évènement
    public void sendEvent(DocumentStatusEvent documentStatusEvent) {
        // Publie un évènement dans le topic
        kafkaTemplate.send(topics, documentStatusEvent);
        // Log info
        log.info("Sent event {}", documentStatusEvent);
    }

}
