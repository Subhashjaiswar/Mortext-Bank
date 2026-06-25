package com.sanviitech.mortextBank.controller;

import com.sanviitech.mortextBank.dto.AddIpRequest;
import com.sanviitech.mortextBank.dto.ApiResponse;
import com.sanviitech.mortextBank.dto.IpWhitelistResponse;
import com.sanviitech.mortextBank.service.IpWhitelistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/ip-whitelist")
@RequiredArgsConstructor
@Tag(name = "IP Whitelist Management", description = "APIs for managing IP whitelist")
public class IpWhitelistController {
    
    private final IpWhitelistService ipWhitelistService;
    
    @PostMapping
    @Operation(summary = "Add IP address to whitelist")
    public ResponseEntity<ApiResponse<IpWhitelistResponse>> addIpToWhitelist(
            @Valid @RequestBody AddIpRequest request,
            Authentication authentication) {
        String createdBy = authentication.getName();
        IpWhitelistResponse response = ipWhitelistService.addIpToWhitelist(request, createdBy);
        return ResponseEntity.ok(ApiResponse.success("IP address added to whitelist successfully", response));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get IP whitelist entry by ID")
    public ResponseEntity<ApiResponse<IpWhitelistResponse>> getIpById(@PathVariable Long id) {
        IpWhitelistResponse response = ipWhitelistService.getIpById(id);
        return ResponseEntity.ok(ApiResponse.success("IP whitelist entry retrieved successfully", response));
    }
    
    @GetMapping("/active")
    @Operation(summary = "Get all active IP addresses")
    public ResponseEntity<ApiResponse<List<IpWhitelistResponse>>> getAllActiveIps() {
        List<IpWhitelistResponse> responses = ipWhitelistService.getAllActiveIps();
        return ResponseEntity.ok(ApiResponse.success("Active IP addresses retrieved successfully", responses));
    }
    
    @GetMapping
    @Operation(summary = "Get all IP addresses with pagination")
    public ResponseEntity<ApiResponse<Page<IpWhitelistResponse>>> getAllIps(Pageable pageable) {
        Page<IpWhitelistResponse> responses = ipWhitelistService.getAllIps(pageable);
        return ResponseEntity.ok(ApiResponse.success("IP addresses retrieved successfully", responses));
    }
    
    @PostMapping("/{id}/toggle-status")
    @Operation(summary = "Toggle IP address active status")
    public ResponseEntity<ApiResponse<IpWhitelistResponse>> toggleIpStatus(@PathVariable Long id) {
        IpWhitelistResponse response = ipWhitelistService.toggleIpStatus(id);
        return ResponseEntity.ok(ApiResponse.success("IP address status toggled successfully", response));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Remove IP address from whitelist")
    public ResponseEntity<ApiResponse<Void>> removeIpFromWhitelist(@PathVariable Long id) {
        ipWhitelistService.removeIpFromWhitelist(id);
        return ResponseEntity.ok(ApiResponse.success("IP address removed from whitelist successfully", null));
    }
    
    @PostMapping("/apply")
    @Operation(summary = "Apply IP whitelist changes")
    public ResponseEntity<ApiResponse<List<String>>> applyIpWhitelist() {
        List<String> activeIps = ipWhitelistService.getAllWhitelistedIpAddresses();
        return ResponseEntity.ok(ApiResponse.success("IP whitelist applied successfully. Active IPs: " + activeIps.size(), activeIps));
    }
    
    @PostMapping("/cleanup")
    @Operation(summary = "Manually trigger cleanup of IP addresses older than 30 minutes")
    public ResponseEntity<ApiResponse<String>> cleanupOldIpAddresses() {
        int deletedCount = ipWhitelistService.cleanupOldIpAddresses();
        return ResponseEntity.ok(ApiResponse.success("Cleanup completed. Deleted " + deletedCount + " old IP addresses", null));
    }
}
