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
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final UserRepository userRepository;
    private final CardRepository cardRepository;
    private final PasswordEncoder passwordEncoder;
    private final CardMapper cardMapper;
    @Override
    public List<CardResponse> getUserCards(Authentication authentication) {
        long startTime = System.currentTimeMillis();
        String email = authentication.getName();
        log.info("Fetching user cards for email: {}", email);
        
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> {
                        log.warn("User not found while fetching cards - Email: {}", email);
                        return GlobalException.resourceNotFound("User", "email", email);
                    });
            List<Card> cards = cardRepository.findByUserId(user.getId());
            List<CardResponse> responses = cards.stream()
                    .map(cardMapper::toResponse)
                    .collect(Collectors.toList());
            
            long endTime = System.currentTimeMillis();
            log.info("Successfully fetched {} cards for user: {} in {} ms", responses.size(), email, (endTime - startTime));
            return responses;
        } catch (Exception e) {
            log.error("Failed to fetch cards for user: {} - Error: {}", email, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public CardResponse getCardById(Long cardId, Authentication authentication) {
        long startTime = System.currentTimeMillis();
        String email = authentication.getName();
        log.info("Fetching card by ID: {} for user: {}", cardId, email);
        
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> {
                        log.warn("User not found while fetching card - Email: {}", email);
                        return GlobalException.resourceNotFound("User", "email", email);
                    });

            Card card = cardRepository.findById(cardId)
                    .orElseThrow(() -> {
                        log.warn("Card not found with ID: {} for user: {}", cardId, email);
                        return GlobalException.resourceNotFound("Card", "id", cardId);
                    });

            if (!card.getUser().getId().equals(user.getId())) {
                log.warn("Unauthorized access attempt to card: {} by user: {}", cardId, email);
                throw new RuntimeException(ValidationConstants.UNAUTHORIZED_ACCESS_TO_CARD);
            }

            long endTime = System.currentTimeMillis();
            log.info("Successfully fetched card: {} for user: {} in {} ms", cardId, email, (endTime - startTime));
            return cardMapper.toResponse(card);
        } catch (Exception e) {
            log.error("Failed to fetch card: {} for user: {} - Error: {}", cardId, email, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public CardResponse freezeCard(Long cardId, Authentication authentication) {
        long startTime = System.currentTimeMillis();
        log.info("Freezing card with ID: {}", cardId);
        
        try {
            Card card = cardRepository.findById(cardId)
                    .orElseThrow(() -> {
                        log.warn("Card not found for freezing - ID: {}", cardId);
                        return GlobalException.resourceNotFound("Card", "id", cardId);
                    });
            card.setIsFrozen(true);
            Card savedCard = cardRepository.save(card);
            
            long endTime = System.currentTimeMillis();
            log.info("Card frozen successfully - ID: {} in {} ms", cardId, (endTime - startTime));
            return cardMapper.toResponse(savedCard);
        } catch (Exception e) {
            log.error("Failed to freeze card: {} - Error: {}", cardId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public CardResponse unfreezeCard(Long cardId, Authentication authentication) {
        long startTime = System.currentTimeMillis();
        log.info("Unfreezing card with ID: {}", cardId);
        
        try {
            Card card = cardRepository.findById(cardId)
                    .orElseThrow(() -> {
                        log.warn("Card not found for unfreezing - ID: {}", cardId);
                        return GlobalException.resourceNotFound("Card", "id", cardId);
                    });
            card.setIsFrozen(false);
            Card savedCard = cardRepository.save(card);
            
            long endTime = System.currentTimeMillis();
            log.info("Card unfrozen successfully - ID: {} in {} ms", cardId, (endTime - startTime));
            return cardMapper.toResponse(savedCard);
        } catch (Exception e) {
            log.error("Failed to unfreeze card: {} - Error: {}", cardId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public CardResponse changePin(ChangePinRequest request, Authentication authentication) {
        long startTime = System.currentTimeMillis();
        log.info("Changing PIN for card ID: {}", request.getCardId());
        
        try {
            Card card = cardRepository.findById(request.getCardId())
                    .orElseThrow(() -> {
                        log.warn("Card not found for PIN change - ID: {}", request.getCardId());
                        return GlobalException.resourceNotFound("Card", "id", request.getCardId());
                    });

            if (card.getPinLocked()) {
                log.warn("Attempt to change PIN on locked card - ID: {}", request.getCardId());
                throw GlobalException.badRequest(ValidationConstants.CARD_PIN_LOCKED);
            }

            if (!passwordEncoder.matches(request.getCurrentPin(), card.getCvv())) {
                card.setPinAttempts(card.getPinAttempts() + 1);
                if (card.getPinAttempts() >= 3) {
                    card.setPinLocked(true);
                    log.warn("Card PIN locked due to too many failed attempts - ID: {}", request.getCardId());
                }
                cardRepository.save(card);
                log.warn("Invalid current PIN attempt for card - ID: {}, Attempts: {}", request.getCardId(), card.getPinAttempts());
                throw GlobalException.badRequest(ValidationConstants.INVALID_CURRENT_PIN);
            }

            card.setCvv(passwordEncoder.encode(request.getNewPin()));
            card.setPinAttempts(0);
            card.setPinLocked(false);
            Card savedCard = cardRepository.save(card);
            
            long endTime = System.currentTimeMillis();
            log.info("PIN changed successfully for card - ID: {} in {} ms", request.getCardId(), (endTime - startTime));
            return cardMapper.toResponse(savedCard);
        } catch (Exception e) {
            log.error("Failed to change PIN for card: {} - Error: {}", request.getCardId(), e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public CardResponse updateCardLimits(Long cardId, BigDecimal dailyLimit, BigDecimal monthlyLimit, Authentication authentication) {
        long startTime = System.currentTimeMillis();
        log.info("Updating card limits - ID: {}, Daily: {}, Monthly: {}", cardId, dailyLimit, monthlyLimit);
        
        try {
            Card card = cardRepository.findById(cardId)
                    .orElseThrow(() -> {
                        log.warn("Card not found for limit update - ID: {}", cardId);
                        return GlobalException.resourceNotFound("Card", "id", cardId);
                    });
            card.setDailyLimit(dailyLimit);
            card.setMonthlyLimit(monthlyLimit);
            Card savedCard = cardRepository.save(card);
            
            long endTime = System.currentTimeMillis();
            log.info("Card limits updated successfully - ID: {} in {} ms", cardId, (endTime - startTime));
            return cardMapper.toResponse(savedCard);
        } catch (Exception e) {
            log.error("Failed to update card limits: {} - Error: {}", cardId, e.getMessage(), e);
            throw e;
        }
    }
}