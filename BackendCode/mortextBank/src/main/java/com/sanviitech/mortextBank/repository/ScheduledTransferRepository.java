package com.sanviitech.mortextBank.repository;

import com.sanviitech.mortextBank.entity.ScheduledTransfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ScheduledTransferRepository extends JpaRepository<ScheduledTransfer, Long> {
    
    List<ScheduledTransfer> findByUserId(Long userId);
    
    List<ScheduledTransfer> findByFromAccountId(Long fromAccountId);
    
    List<ScheduledTransfer> findByUserIdAndStatus(Long userId, ScheduledTransfer.TransferStatus status);
    
    List<ScheduledTransfer> findByScheduledDateBeforeAndStatus(LocalDateTime dateTime, ScheduledTransfer.TransferStatus status);
}
