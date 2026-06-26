package com.sanviitech.mortextBank.mapper;

import com.sanviitech.mortextBank.dto.CardResponse;
import com.sanviitech.mortextBank.entity.Card;
import org.springframework.stereotype.Component;

@Component
public class CardMapper {
    
    public CardResponse toResponse(Card card) {
        if (card == null) {
            return null;
        }
        
        CardResponse response = new CardResponse();
        response.setId(card.getId());
        response.setCardNumber(card.getCardNumber());
        response.setCardHolderName(card.getCardHolderName());
        response.setCardType(card.getCardType());
        response.setCvv(card.getCvv());
        response.setExpiryDate(card.getExpiryDate());
        response.setIsActive(card.getIsActive());
        response.setIsFrozen(card.getIsFrozen());
        response.setIsVirtual(card.getIsVirtual());
        response.setDailyLimit(card.getDailyLimit());
        response.setMonthlyLimit(card.getMonthlyLimit());
        response.setPinAttempts(card.getPinAttempts());
        response.setPinLocked(card.getPinLocked());
        response.setCreatedAt(card.getCreatedAt());
        response.setUpdatedAt(card.getUpdatedAt());
        response.setIssuedDate(card.getIssuedDate());
        return response;
    }
}
