package com.sanviitech.mortextBank.service;

import com.sanviitech.mortextBank.dto.ChangePinRequest;
import com.sanviitech.mortextBank.entity.Card;
import com.sanviitech.mortextBank.entity.User;
import com.sanviitech.mortextBank.exception.BadRequestException;
import com.sanviitech.mortextBank.exception.ResourceNotFoundException;
import com.sanviitech.mortextBank.repository.CardRepository;
import com.sanviitech.mortextBank.repository.UserRepository;
import com.sanviitech.mortextBank.util.OTPUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CardService {
    
    private final UserRepository userRepository;
    private final CardRepository cardRepository;
    private final PasswordEncoder passwordEncoder;
    
    public List<Card> getUserCards(Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", authentication.getName()));
        return cardRepository.findByUserId(user.getId());
    }
    
    public Card getCardById(Long cardId, Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", authentication.getName()));
        
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("Card", "id", cardId));
        
        if (!card.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access to card");
        }
        
        return card;
    }
    
    public Card freezeCard(Long cardId, Authentication authentication) {
        Card card = getCardById(cardId, authentication);
        card.setIsFrozen(true);
        return cardRepository.save(card);
    }
    
    public Card unfreezeCard(Long cardId, Authentication authentication) {
        Card card = getCardById(cardId, authentication);
        card.setIsFrozen(false);
        return cardRepository.save(card);
    }
    
    public Card changePin(ChangePinRequest request, Authentication authentication) {
        Card card = getCardById(request.getCardId(), authentication);
        
        if (card.getPinLocked()) {
            throw new BadRequestException("Card PIN is locked. Please contact customer support.");
        }
        
        if (!passwordEncoder.matches(request.getCurrentPin(), card.getCvv())) {
            card.setPinAttempts(card.getPinAttempts() + 1);
            if (card.getPinAttempts() >= 3) {
                card.setPinLocked(true);
            }
            cardRepository.save(card);
            throw new BadRequestException("Invalid current PIN");
        }
        
        card.setCvv(passwordEncoder.encode(request.getNewPin()));
        card.setPinAttempts(0);
        card.setPinLocked(false);
        return cardRepository.save(card);
    }
    
    public Card updateCardLimits(Long cardId, java.math.BigDecimal dailyLimit, java.math.BigDecimal monthlyLimit, Authentication authentication) {
        Card card = getCardById(cardId, authentication);
        card.setDailyLimit(dailyLimit);
        card.setMonthlyLimit(monthlyLimit);
        return cardRepository.save(card);
    }
}
