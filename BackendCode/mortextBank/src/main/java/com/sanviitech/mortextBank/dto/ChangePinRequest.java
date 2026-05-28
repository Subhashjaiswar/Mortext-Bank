package com.sanviitech.mortextBank.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangePinRequest {
    
    @NotBlank(message = "Card ID is required")
    private Long cardId;
    
    @NotBlank(message = "Current PIN is required")
    @Size(min = 4, max = 4, message = "PIN must be 4 digits")
    private String currentPin;
    
    @NotBlank(message = "New PIN is required")
    @Size(min = 4, max = 4, message = "PIN must be 4 digits")
    private String newPin;
}
