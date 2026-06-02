package com.sanviitech.mortextBank.service;

import org.springframework.security.core.Authentication;

import java.util.Map;

public interface DashboardService {
    Map<String, Object> getDashboardData(Authentication authentication);
}