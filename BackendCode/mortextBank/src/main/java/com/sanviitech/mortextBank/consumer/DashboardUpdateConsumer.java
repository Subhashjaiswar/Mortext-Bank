package com.sanviitech.mortextBank.consumer;

import com.sanviitech.mortextBank.dto.TransactionEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardUpdateConsumer {

    @KafkaListener(topics = "${kafka.topic.transaction}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeTransactionEvent(TransactionEvent event) {
        try {
            log.info("Received transaction event for dashboard update - Transaction ID: {}, User ID: {}, Amount: {}", 
                    event.getTransactionId(), event.getUserId(), event.getAmount());
            
            // Here you can implement dashboard update logic
            // For example:
            // - Update user's transaction statistics
            // - Update real-time dashboard cache
            // - Push notification to frontend via WebSocket
            // - Update analytics/metrics
            
            log.info("Dashboard updated successfully for transaction - Transaction ID: {}, User ID: {}", 
                    event.getTransactionId(), event.getUserId());
        } catch (Exception e) {
            log.error("Failed to update dashboard for transaction - Transaction ID: {}, Error: {}", 
                    event.getTransactionId(), e.getMessage(), e);
        }
    }
}
