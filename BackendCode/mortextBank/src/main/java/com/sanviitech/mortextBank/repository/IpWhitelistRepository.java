package com.sanviitech.mortextBank.repository;

import com.sanviitech.mortextBank.entity.IpWhitelist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface IpWhitelistRepository extends JpaRepository<IpWhitelist, Long> {
    
    Optional<IpWhitelist> findByIpAddress(String ipAddress);
    
    boolean existsByIpAddress(String ipAddress);
    
    List<IpWhitelist> findByActiveTrue();
    
    List<IpWhitelist> findByActiveFalse();
    
    List<IpWhitelist> findByCreatedAtBefore(LocalDateTime dateTime);
}
