package com.sanviitech.mortextBank.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String fullName;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @JsonIgnore
    @Column(nullable = false)
    private String securityPin;
    
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    private List<String> roles;
    
    @Override
    @Transient
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (roles == null || roles.isEmpty()) {
            return List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER"));
        }
        return roles.stream()
            .map(role -> new SimpleGrantedAuthority(role.startsWith("ROLE_") ? role : "ROLE_" + role))
            .collect(Collectors.toList());
    }
    
    @Override
    @Transient
    public String getPassword() {
        return securityPin;
    }
    
    @Override
    @Transient
    public String getUsername() {
        return email;
    }
    
    @Override
    @Transient
    public boolean isAccountNonExpired() {
        return true;
    }
    
    @Override
    @Transient
    public boolean isAccountNonLocked() {
        return true;
    }
    
    @Override
    @Transient
    public boolean isCredentialsNonExpired() {
        return true;
    }
    
    @Override
    @Transient
    public boolean isEnabled() {
        return true;
    }

}
