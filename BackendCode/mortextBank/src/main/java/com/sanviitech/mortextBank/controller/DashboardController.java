package com.sanviitech.mortextBank.controller;

import com.sanviitech.mortextBank.dto.ApiResponse;
import com.sanviitech.mortextBank.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Dashboard APIs")
public class DashboardController {
    
    private final DashboardService dashboardService;
    
    @GetMapping
    @Operation(summary = "Get dashboard data")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboard(Authentication authentication) {
        Map<String, Object> data = dashboardService.getDashboardData(authentication);
        return ResponseEntity.ok(ApiResponse.success("Dashboard data retrieved successfully", data));
    }
}
