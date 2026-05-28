package com.sanviitech.mortextBank.controller;

import com.sanviitech.mortextBank.dto.ApiResponse;
import com.sanviitech.mortextBank.dto.ChangePinRequest;
import com.sanviitech.mortextBank.entity.Card;
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
    public ResponseEntity<ApiResponse<List<Card>>> getUserCards(Authentication authentication) {
        List<Card> cards = cardService.getUserCards(authentication);
        return ResponseEntity.ok(ApiResponse.success("Cards retrieved successfully", cards));
    }
    
    @GetMapping("/{cardId}")
    @Operation(summary = "Get card by ID")
    public ResponseEntity<ApiResponse<Card>> getCardById(@PathVariable Long cardId, Authentication authentication) {
        Card card = cardService.getCardById(cardId, authentication);
        return ResponseEntity.ok(ApiResponse.success("Card retrieved successfully", card));
    }
    
    @PostMapping("/{cardId}/freeze")
    @Operation(summary = "Freeze card")
    public ResponseEntity<ApiResponse<Card>> freezeCard(@PathVariable Long cardId, Authentication authentication) {
        Card card = cardService.freezeCard(cardId, authentication);
        return ResponseEntity.ok(ApiResponse.success("Card frozen successfully", card));
    }
    
    @PostMapping("/{cardId}/unfreeze")
    @Operation(summary = "Unfreeze card")
    public ResponseEntity<ApiResponse<Card>> unfreezeCard(@PathVariable Long cardId, Authentication authentication) {
        Card card = cardService.unfreezeCard(cardId, authentication);
        return ResponseEntity.ok(ApiResponse.success("Card unfrozen successfully", card));
    }
    
    @PostMapping("/change-pin")
    @Operation(summary = "Change card PIN")
    public ResponseEntity<ApiResponse<Card>> changePin(@Valid @RequestBody ChangePinRequest request, Authentication authentication) {
        Card card = cardService.changePin(request, authentication);
        return ResponseEntity.ok(ApiResponse.success("PIN changed successfully", card));
    }
    
    @PutMapping("/{cardId}/limits")
    @Operation(summary = "Update card limits")
    public ResponseEntity<ApiResponse<Card>> updateCardLimits(
            @PathVariable Long cardId,
            @RequestParam BigDecimal dailyLimit,
            @RequestParam BigDecimal monthlyLimit,
            Authentication authentication) {
        Card card = cardService.updateCardLimits(cardId, dailyLimit, monthlyLimit, authentication);
        return ResponseEntity.ok(ApiResponse.success("Card limits updated successfully", card));
    }
}
