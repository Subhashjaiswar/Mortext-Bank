package com.sanviitech.mortextBank.service.impl;

import com.sanviitech.mortextBank.dto.AddIpRequest;
import com.sanviitech.mortextBank.dto.IpWhitelistResponse;
import com.sanviitech.mortextBank.entity.IpWhitelist;
import com.sanviitech.mortextBank.repository.IpWhitelistRepository;
import com.sanviitech.mortextBank.service.IpWhitelistService;
import com.sanviitech.mortextBank.util.GlobalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class IpWhitelistServiceImpl implements IpWhitelistService {
    
    private final IpWhitelistRepository ipWhitelistRepository;
    

    @Override
    public IpWhitelistResponse addIpToWhitelist(AddIpRequest request, String createdBy) {
        if (ipWhitelistRepository.existsByIpAddress(request.getIpAddress())) {
            throw new IllegalArgumentException("IP address already exists in whitelist");
        }
        
        IpWhitelist ipWhitelist = new IpWhitelist();
        ipWhitelist.setIpAddress(request.getIpAddress());
        ipWhitelist.setDescription(request.getDescription());
        ipWhitelist.setActive(true);
        ipWhitelist.setCreatedBy(createdBy);
        
        IpWhitelist saved = ipWhitelistRepository.save(ipWhitelist);
        return IpWhitelistResponse.fromEntity(saved);
    }
    
    @Override
    @Transactional(readOnly = true)
    public IpWhitelistResponse getIpById(Long id) {
        IpWhitelist ipWhitelist = ipWhitelistRepository.findById(id)
            .orElseThrow(() -> GlobalException.resourceNotFound("IP whitelist entry", "id", id));
        return IpWhitelistResponse.fromEntity(ipWhitelist);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<IpWhitelistResponse> getAllActiveIps() {
        return ipWhitelistRepository.findByActiveTrue().stream()
            .map(IpWhitelistResponse::fromEntity)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<IpWhitelistResponse> getAllIps(Pageable pageable) {
        return ipWhitelistRepository.findAll(pageable)
            .map(IpWhitelistResponse::fromEntity);
    }
    
    @Override
    public IpWhitelistResponse toggleIpStatus(Long id) {
        IpWhitelist ipWhitelist = ipWhitelistRepository.findById(id)
            .orElseThrow(() -> GlobalException.resourceNotFound("IP whitelist entry", "id", id));
        
        ipWhitelist.setActive(!ipWhitelist.getActive());
        IpWhitelist updated = ipWhitelistRepository.save(ipWhitelist);
        return IpWhitelistResponse.fromEntity(updated);
    }
    
    @Override
    public void removeIpFromWhitelist(Long id) {
        if (!ipWhitelistRepository.existsById(id)) {
            throw GlobalException.resourceNotFound("IP whitelist entry", "id", id);
        }
        ipWhitelistRepository.deleteById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean isIpWhitelisted(String ipAddress) {
        return ipWhitelistRepository.findByIpAddress(ipAddress)
            .map(IpWhitelist::getActive)
            .orElse(false);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<String> getAllWhitelistedIpAddresses() {
        return ipWhitelistRepository.findByActiveTrue().stream()
            .map(IpWhitelist::getIpAddress)
            .collect(Collectors.toList());
    }
    
    @Override
    public int cleanupOldIpAddresses() {
        LocalDateTime thirtyMinutesAgo = LocalDateTime.now().minusMinutes(30);
        List<IpWhitelist> oldIpEntries = ipWhitelistRepository.findByCreatedAtBefore(thirtyMinutesAgo);
        
        if (oldIpEntries.isEmpty()) {
            log.info("No IP addresses older than 30 minutes found for cleanup");
            return 0;
        }
        
        log.info("Found {} IP addresses older than 30 minutes, starting cleanup", oldIpEntries.size());
        ipWhitelistRepository.deleteAll(oldIpEntries);
        log.info("Successfully deleted {} old IP addresses", oldIpEntries.size());
        
        return oldIpEntries.size();
    }
    
    @Scheduled(cron = "0 */30 * * * *")
    @Transactional
    public void scheduledCleanupOldIpAddresses() {
        log.info("Starting scheduled cleanup of IP addresses older than 30 minutes");
        int deletedCount = cleanupOldIpAddresses();
        log.info("Scheduled cleanup completed. Total IP addresses deleted: {}", deletedCount);
    }
}
