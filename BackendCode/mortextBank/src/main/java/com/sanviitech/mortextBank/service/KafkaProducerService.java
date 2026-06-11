package com.sanviitech.mortextBank.service;

import com.sanviitech.mortextBank.dto.TransactionEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, TransactionEvent> kafkaTemplate;

    @Value("${kafka.topic.transaction}")
    private String transactionTopic;

    public void sendTransactionEvent(TransactionEvent event) {
        try {
            log.info("Sending transaction event to Kafka - Transaction ID: {}, Topic: {}", event.getTransactionId(), transactionTopic);
            kafkaTemplate.send(transactionTopic, event.getTransactionId(), event);
            log.info("Transaction event sent successfully - Transaction ID: {}", event.getTransactionId());
        } catch (Exception e) {
            log.error("Failed to send transaction event to Kafka - Transaction ID: {}, Error: {}", event.getTransactionId(), e.getMessage(), e);
        }
    }
}
