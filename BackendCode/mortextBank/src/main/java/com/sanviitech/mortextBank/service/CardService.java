package com.sanviitech.mortextBank.service;

import com.sanviitech.mortextBank.dto.CardResponse;
import com.sanviitech.mortextBank.dto.ChangePinRequest;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.util.List;

public interface CardService {
    List<CardResponse> getUserCards(Authentication authentication);
    CardResponse getCardById(Long cardId, Authentication authentication);
    CardResponse freezeCard(Long cardId, Authentication authentication);
    CardResponse unfreezeCard(Long cardId, Authentication authentication);
    CardResponse changePin(ChangePinRequest request, Authentication authentication);
    CardResponse updateCardLimits(Long cardId, BigDecimal dailyLimit, BigDecimal monthlyLimit, Authentication authentication);
}