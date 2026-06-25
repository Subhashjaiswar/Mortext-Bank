package com.sanviitech.mortextBank.service;

import com.sanviitech.mortextBank.dto.AddIpRequest;
import com.sanviitech.mortextBank.dto.IpWhitelistResponse;
import com.sanviitech.mortextBank.entity.IpWhitelist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IpWhitelistService {
    
    IpWhitelistResponse addIpToWhitelist(AddIpRequest request, String createdBy);
    
    IpWhitelistResponse getIpById(Long id);
    
    List<IpWhitelistResponse> getAllActiveIps();
    
    Page<IpWhitelistResponse> getAllIps(Pageable pageable);
    
    IpWhitelistResponse toggleIpStatus(Long id);
    
    void removeIpFromWhitelist(Long id);
    
    boolean isIpWhitelisted(String ipAddress);
    
    List<String> getAllWhitelistedIpAddresses();
    
    int cleanupOldIpAddresses();
}
