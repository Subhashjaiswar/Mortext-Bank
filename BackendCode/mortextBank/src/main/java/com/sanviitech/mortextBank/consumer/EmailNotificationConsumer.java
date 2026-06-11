package com.sanviitech.mortextBank.consumer;

import com.sanviitech.mortextBank.dto.TransactionEvent;
import com.sanviitech.mortextBank.util.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailNotificationConsumer {

    private final EmailService emailService;

    @KafkaListener(topics = "${kafka.topic.transaction}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeTransactionEvent(TransactionEvent event) {
        try {
            log.info("Received transaction event for email notification - Transaction ID: {}, User: {}, Amount: {}", 
                    event.getTransactionId(), event.getUserEmail(), event.getAmount());
            
            String transactionType = event.getTransactionType().toLowerCase();
            String action = "completed";
            
            if (transactionType.contains("debit") || transactionType.contains("transfer") || transactionType.contains("withdrawal")) {
                action = "debited from";
            } else if (transactionType.contains("credit")) {
                action = "credited to";
            }
            
            emailService.sendTransactionAlert(
                    event.getUserEmail(),
                    event.getTransactionId(),
                    event.getAmount().toString(),
                    action
            );
            
            log.info("Email notification sent successfully for transaction - Transaction ID: {}", event.getTransactionId());
        } catch (Exception e) {
            log.error("Failed to send email notification for transaction - Transaction ID: {}, Error: {}", 
                    event.getTransactionId(), e.getMessage(), e);
        }
    }
}
