package com.sanviitech.mortextBank.service.impl;

import com.sanviitech.mortextBank.dto.CardResponse;
import com.sanviitech.mortextBank.dto.ChangePinRequest;
import com.sanviitech.mortextBank.entity.Card;
import com.sanviitech.mortextBank.entity.User;
import com.sanviitech.mortextBank.constants.ValidationConstants;
import com.sanviitech.mortextBank.util.GlobalException;
import com.sanviitech.mortextBank.mapper.CardMapper;
import com.sanviitech.mortextBank.repository.CardRepository;
import com.sanviitech.mortextBank.repository.UserRepository;
import com.sanviitech.mortextBank.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final UserRepository userRepository;
    private final CardRepository cardRepository;
    private final PasswordEncoder passwordEncoder;
    private final CardMapper cardMapper;

    @Override
    public List<CardResponse> getUserCards(Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> GlobalException.resourceNotFound("User", "email", authentication.getName()));
        List<Card> cards = cardRepository.findByUserId(user.getId());
        return cards.stream()
                .map(cardMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CardResponse getCardById(Long cardId, Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> GlobalException.resourceNotFound("User", "email", authentication.getName()));

        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> GlobalException.resourceNotFound("Card", "id", cardId));

        if (!card.getUser().getId().equals(user.getId())) {
            throw new RuntimeException(ValidationConstants.UNAUTHORIZED_ACCESS_TO_CARD);
        }

        return cardMapper.toResponse(card);
    }

    @Override
    public CardResponse freezeCard(Long cardId, Authentication authentication) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> GlobalException.resourceNotFound("Card", "id", cardId));
        card.setIsFrozen(true);
        Card savedCard = cardRepository.save(card);
        return cardMapper.toResponse(savedCard);
    }

    @Override
    public CardResponse unfreezeCard(Long cardId, Authentication authentication) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> GlobalException.resourceNotFound("Card", "id", cardId));
        card.setIsFrozen(false);
        Card savedCard = cardRepository.save(card);
        return cardMapper.toResponse(savedCard);
    }

    @Override
    public CardResponse changePin(ChangePinRequest request, Authentication authentication) {
        Card card = cardRepository.findById(request.getCardId())
                .orElseThrow(() -> GlobalException.resourceNotFound("Card", "id", request.getCardId()));

        if (card.getPinLocked()) {
            throw GlobalException.badRequest(ValidationConstants.CARD_PIN_LOCKED);
        }

        if (!passwordEncoder.matches(request.getCurrentPin(), card.getCvv())) {
            card.setPinAttempts(card.getPinAttempts() + 1);
            if (card.getPinAttempts() >= 3) {
                card.setPinLocked(true);
            }
            cardRepository.save(card);
            throw GlobalException.badRequest(ValidationConstants.INVALID_CURRENT_PIN);
        }

        card.setCvv(passwordEncoder.encode(request.getNewPin()));
        card.setPinAttempts(0);
        card.setPinLocked(false);
        Card savedCard = cardRepository.save(card);
        return cardMapper.toResponse(savedCard);
    }

    @Override
    public CardResponse updateCardLimits(Long cardId, BigDecimal dailyLimit, BigDecimal monthlyLimit, Authentication authentication) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> GlobalException.resourceNotFound("Card", "id", cardId));
        card.setDailyLimit(dailyLimit);
        card.setMonthlyLimit(monthlyLimit);
        Card savedCard = cardRepository.save(card);
        return cardMapper.toResponse(savedCard);
    }
}