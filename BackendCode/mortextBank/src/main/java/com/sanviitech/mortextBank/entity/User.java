package com.sanviitech.mortextBank.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

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
    
    @Column(nullable = false)
    private String securityPin;
    
    @Override
    @Transient
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_CUSTOMER"));
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
