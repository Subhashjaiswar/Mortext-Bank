package com.sanviitech.mortextBank.service;

import com.sanviitech.mortextBank.dto.ChangePinRequest;
import com.sanviitech.mortextBank.entity.Card;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.util.List;

public interface CardService {
    List<Card> getUserCards(Authentication authentication);
    Card getCardById(Long cardId, Authentication authentication);
    Card freezeCard(Long cardId, Authentication authentication);
    Card unfreezeCard(Long cardId, Authentication authentication);
    Card changePin(ChangePinRequest request, Authentication authentication);
    Card updateCardLimits(Long cardId, BigDecimal dailyLimit, BigDecimal monthlyLimit, Authentication authentication);
}