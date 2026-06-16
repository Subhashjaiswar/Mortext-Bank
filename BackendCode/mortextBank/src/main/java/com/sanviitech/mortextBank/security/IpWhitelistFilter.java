package com.sanviitech.mortextBank.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class IpWhitelistFilter extends OncePerRequestFilter {

    @Value("${security.ip.whitelist.enabled:false}")
    private boolean ipWhitelistEnabled;

    @Value("${security.ip.whitelist.allowed-ips:}")
    private String allowedIps;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        
        if (!ipWhitelistEnabled) {
            filterChain.doFilter(request, response);
            return;
        }

        String clientIp = getClientIp(request);
        
        if (isIpAllowed(clientIp)) {
            filterChain.doFilter(request, response);
        } else {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("{\"error\": \"Access denied. Your IP address is not whitelisted.\"}");
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        
        // Handle multiple IPs in X-Forwarded-For (take the first one)
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        
        return ip;
    }

    private boolean isIpAllowed(String clientIp) {
        if (allowedIps == null || allowedIps.trim().isEmpty()) {
            return false;
        }
        
        boolean isClientIpv6 = isIPv6(clientIp);
        List<String> allowedIpList = Arrays.asList(allowedIps.split(","));
        
        for (String allowedIp : allowedIpList) {
            allowedIp = allowedIp.trim();
            
            // Exact match
            if (allowedIp.equals(clientIp)) {
                return true;
            }
            
            // For IPv6, only exact matching is supported (skip CIDR and wildcard)
            if (isClientIpv6) {
                continue;
            }
            
            // CIDR notation support (e.g., 192.168.1.0/24) - IPv4 only
            if (allowedIp.contains("/")) {
                if (isIpInCidrRange(clientIp, allowedIp)) {
                    return true;
                }
            }
            
            // Wildcard support (e.g., 192.168.1.*) - IPv4 only
            if (allowedIp.contains("*")) {
                String regex = allowedIp.replace(".", "\\.").replace("*", ".*");
                if (clientIp.matches(regex)) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    private boolean isIPv6(String ip) {
        return ip.contains(":");
    }

    private boolean isIpInCidrRange(String ip, String cidr) {
        String[] parts = cidr.split("/");
        String networkAddress = parts[0];
        int prefixLength = Integer.parseInt(parts[1]);
        
        long ipLong = ipToLong(ip);
        long networkLong = ipToLong(networkAddress);
        long mask = (0xFFFFFFFFL << (32 - prefixLength)) & 0xFFFFFFFFL;
        
        return (ipLong & mask) == (networkLong & mask);
    }

    private long ipToLong(String ip) {
        String[] octets = ip.split("\\.");
        long result = 0;
        for (int i = 0; i < 4; i++) {
            result = result * 256 + Long.parseLong(octets[i]);
        }
        return result;
    }
}
