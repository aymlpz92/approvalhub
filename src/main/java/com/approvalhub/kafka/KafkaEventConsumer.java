package com.approvalhub.kafka;

import com.approvalhub.dto.status.DocumentStatusEvent;
import com.approvalhub.service.DocumentService;
import com.approvalhub.service.StatusHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaEventConsumer {

    private final DocumentService documentService;
    private final StatusHistoryService statusHistoryService;

    @KafkaListener(
            topics = "${app.kafka.topics}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consume(
            // Status désérialisé
            DocumentStatusEvent event,
            // Métadonnées kafka injectées
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) Long offset,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic
            ) {

        statusHistoryService.updateDocumentStatus(event);
        log.info("Received event {}", event);

    }

}
