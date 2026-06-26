package com.sanviitech.mortextBank.controller;

import com.sanviitech.mortextBank.dto.ApiResponse;
import com.sanviitech.mortextBank.dto.CardResponse;
import com.sanviitech.mortextBank.dto.ChangePinRequest;
import com.sanviitech.mortextBank.service.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
@Tag(name = "Card Management", description = "Card Management APIs")
public class CardController {
    
    private final CardService cardService;
    
    @GetMapping
    @Operation(summary = "Get all user cards")
    public ResponseEntity<ApiResponse<List<CardResponse>>> getUserCards(Authentication authentication) {
        List<CardResponse> cards = cardService.getUserCards(authentication);
        return ResponseEntity.ok(ApiResponse.success("Cards retrieved successfully", cards));
    }
    
    @GetMapping("/{cardId}")
    @Operation(summary = "Get card by ID")
    public ResponseEntity<ApiResponse<CardResponse>> getCardById(@PathVariable Long cardId, Authentication authentication) {
        CardResponse card = cardService.getCardById(cardId, authentication);
        return ResponseEntity.ok(ApiResponse.success("Card retrieved successfully", card));
    }
    
    @PostMapping("/{cardId}/freeze")
    @Operation(summary = "Freeze card")
    public ResponseEntity<ApiResponse<CardResponse>> freezeCard(@PathVariable Long cardId, Authentication authentication) {
        CardResponse card = cardService.freezeCard(cardId, authentication);
        return ResponseEntity.ok(ApiResponse.success("Card frozen successfully", card));
    }
    
    @PostMapping("/{cardId}/unfreeze")
    @Operation(summary = "Unfreeze card")
    public ResponseEntity<ApiResponse<CardResponse>> unfreezeCard(@PathVariable Long cardId, Authentication authentication) {
        CardResponse card = cardService.unfreezeCard(cardId, authentication);
        return ResponseEntity.ok(ApiResponse.success("Card unfrozen successfully", card));
    }
    
    @PostMapping("/change-pin")
    @Operation(summary = "Change card PIN")
    public ResponseEntity<ApiResponse<CardResponse>> changePin(@Valid @RequestBody ChangePinRequest request, Authentication authentication) {
        CardResponse card = cardService.changePin(request, authentication);
        return ResponseEntity.ok(ApiResponse.success("PIN changed successfully", card));
    }
    
    @PutMapping("/{cardId}/limits")
    @Operation(summary = "Update card limits")
    public ResponseEntity<ApiResponse<CardResponse>> updateCardLimits(
            @PathVariable Long cardId,
            @RequestParam BigDecimal dailyLimit,
            @RequestParam BigDecimal monthlyLimit,
            Authentication authentication) {
        CardResponse card = cardService.updateCardLimits(cardId, dailyLimit, monthlyLimit, authentication);
        return ResponseEntity.ok(ApiResponse.success("Card limits updated successfully", card));
    }
}
