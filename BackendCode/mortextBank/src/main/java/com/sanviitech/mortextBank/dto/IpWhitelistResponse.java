package com.sanviitech.mortextBank.dto;

import com.sanviitech.mortextBank.entity.IpWhitelist;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IpWhitelistResponse {
    
    private Long id;
    private String ipAddress;
    private String description;
    private Boolean active;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public static IpWhitelistResponse fromEntity(IpWhitelist ipWhitelist) {
        return new IpWhitelistResponse(
            ipWhitelist.getId(),
            ipWhitelist.getIpAddress(),
            ipWhitelist.getDescription(),
            ipWhitelist.getActive(),
            ipWhitelist.getCreatedBy(),
            ipWhitelist.getCreatedAt(),
            ipWhitelist.getUpdatedAt()
        );
    }
}
