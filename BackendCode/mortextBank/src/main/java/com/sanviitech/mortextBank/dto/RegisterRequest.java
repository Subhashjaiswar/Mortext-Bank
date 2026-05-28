package com.sanviitech.mortextBank.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    
    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    private String fullName;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;
    
    @NotBlank(message = "Security pin is required")
    @Size(min = 4, max = 6, message = "Security pin must be between 4 and 6 digits")
    @Pattern(regexp = "^\\d{4,6}$", message = "Security pin must be numeric")
    private String securityPin;
}
