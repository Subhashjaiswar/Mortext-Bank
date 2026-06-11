package com.sanviitech.mortextBank.service.impl;

import com.sanviitech.mortextBank.dto.AddBeneficiaryRequest;
import com.sanviitech.mortextBank.dto.ScheduledTransferRequest;
import com.sanviitech.mortextBank.dto.TransferRequest;
import com.sanviitech.mortextBank.dto.TransactionEvent;
import com.sanviitech.mortextBank.entity.*;
import com.sanviitech.mortextBank.constants.ValidationConstants;
import com.sanviitech.mortextBank.util.GlobalException;
import com.sanviitech.mortextBank.repository.*;
import com.sanviitech.mortextBank.service.TransferService;
import com.sanviitech.mortextBank.service.KafkaProducerService;
import com.sanviitech.mortextBank.util.OTPUtil;
import com.sanviitech.mortextBank.validator.AccountNumberValidator;
import com.sanviitech.mortextBank.validator.AmountValidator;
import com.sanviitech.mortextBank.validator.BeneficiaryTypeValidator;
import com.sanviitech.mortextBank.validator.IFSCValidator;
import com.sanviitech.mortextBank.validator.NameValidator;
import com.sanviitech.mortextBank.validator.SizeValidator;
import com.sanviitech.mortextBank.validator.TransferTypeValidator;
import com.sanviitech.mortextBank.validator.UPIValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransferServiceImpl implements TransferService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final BeneficiaryRepository beneficiaryRepository;
    private final ScheduledTransferRepository scheduledTransferRepository;
    private final KafkaProducerService kafkaProducerService;

    @Autowired
    private AccountNumberValidator accountNumberValidator;

    @Autowired
    private AmountValidator amountValidator;

    @Autowired
    private BeneficiaryTypeValidator beneficiaryTypeValidator;

    @Autowired
    private IFSCValidator ifscValidator;

    @Autowired
    private NameValidator nameValidator;

    @Autowired
    private SizeValidator sizeValidator;

    @Autowired
    private TransferTypeValidator transferTypeValidator;

    @Autowired
    private UPIValidator upiValidator;

    @Override
    @Transactional
    public Transaction transferMoney(TransferRequest request, Authentication authentication) {
        long startTime = System.currentTimeMillis();
        String email = authentication.getName();
        log.info("Money transfer initiated by user: {} - Amount: {}, To Account: {}", email, request.getAmount(), request.getToAccountNumber());
        
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> {
                        log.warn("User not found for money transfer - Email: {}", email);
                        return GlobalException.resourceNotFound("User", "email", email);
                    });

            final String fromAccountNumber;
            if (request.getFromAccountNumber() == null || request.getFromAccountNumber().trim().isEmpty()) {
                Account defaultAccount = accountRepository.findByUserIdAndAccountType(user.getId(), Account.AccountType.SAVINGS)
                        .orElseThrow(() -> {
                            log.warn("Default savings account not found for user: {}", user.getId());
                            return GlobalException.resourceNotFound("Savings Account", "user_id", user.getId());
                        });
                fromAccountNumber = defaultAccount.getAccountNumber();
            } else {
                fromAccountNumber = request.getFromAccountNumber();
            }

            String fromAccountError = accountNumberValidator.validate(fromAccountNumber);
            if (fromAccountError != null) {
                log.warn("From account validation failed: {} - Error: {}", fromAccountNumber, fromAccountError);
                throw GlobalException.badRequest(fromAccountError);
            }

            String toAccountError = accountNumberValidator.validate(request.getToAccountNumber());
            if (toAccountError != null) {
                log.warn("To account validation failed: {} - Error: {}", request.getToAccountNumber(), toAccountError);
                throw GlobalException.badRequest(toAccountError);
            }

            String amountError = amountValidator.validate(request.getAmount());
            if (amountError != null) {
                log.warn("Amount validation failed: {} - Error: {}", request.getAmount(), amountError);
                throw GlobalException.badRequest(amountError);
            }

            String transferTypeError = transferTypeValidator.validate(request.getTransferType());
            if (transferTypeError != null) {
                log.warn("Transfer type validation failed: {} - Error: {}", request.getTransferType(), transferTypeError);
                throw GlobalException.badRequest(transferTypeError);
            }

            if (request.getRemarks() != null) {
                String remarksError = sizeValidator.validate(request.getRemarks(), "Remarks", 500);
                if (remarksError != null) {
                    log.warn("Remarks validation failed - Error: {}", remarksError);
                    throw GlobalException.badRequest(remarksError);
                }
            }

            Account fromAccount = accountRepository.findByAccountNumber(fromAccountNumber)
                    .orElseThrow(() -> {
                        log.warn("From account not found: {}", fromAccountNumber);
                        return GlobalException.resourceNotFound("Account", "accountNumber", fromAccountNumber);
                    });

            if (!fromAccount.getUser().getId().equals(user.getId())) {
                log.warn("Unauthorized transfer attempt from account: {} by user: {}", fromAccountNumber, email);
                throw new RuntimeException(ValidationConstants.UNAUTHORIZED_ACCESS_TO_ACCOUNT);
            }

            if (fromAccount.getAvailableBalance().compareTo(request.getAmount()) < 0) {
                log.warn("Insufficient balance in account: {} - Available: {}, Required: {}", fromAccountNumber, fromAccount.getAvailableBalance(), request.getAmount());
                throw GlobalException.insufficientBalance(ValidationConstants.INSUFFICIENT_BALANCE);
            }

            fromAccount.setAvailableBalance(fromAccount.getAvailableBalance().subtract(request.getAmount()));
            fromAccount.setBalance(fromAccount.getBalance().subtract(request.getAmount()));
            accountRepository.save(fromAccount);

            Transaction transaction = new Transaction();
            transaction.setTransactionId(OTPUtil.generateTransactionId());
            transaction.setAccount(fromAccount);
            transaction.setUser(user);
            transaction.setTransactionType(Transaction.TransactionType.TRANSFER);
            transaction.setStatus(Transaction.TransactionStatus.COMPLETED);
            transaction.setAmount(request.getAmount());
            transaction.setBalanceAfter(fromAccount.getBalance());
            transaction.setDescription(request.getRemarks());
            transaction.setBeneficiaryAccountNumber(request.getToAccountNumber());
            transaction.setTransactionDate(LocalDateTime.now());

            Transaction savedTransaction = transactionRepository.save(transaction);
            
            // Send transaction event to Kafka
            TransactionEvent event = TransactionEvent.builder()
                    .transactionId(savedTransaction.getTransactionId())
                    .userId(user.getId())
                    .userEmail(user.getEmail())
                    .userName(user.getFullName())
                    .accountId(fromAccount.getId())
                    .accountNumber(fromAccount.getAccountNumber())
                    .transactionType(savedTransaction.getTransactionType().name())
                    .status(savedTransaction.getStatus().name())
                    .amount(savedTransaction.getAmount())
                    .balanceAfter(savedTransaction.getBalanceAfter())
                    .description(savedTransaction.getDescription())
                    .beneficiaryAccountNumber(savedTransaction.getBeneficiaryAccountNumber())
                    .transactionDate(savedTransaction.getTransactionDate())
                    .createdAt(savedTransaction.getCreatedAt())
                    .build();
            
            kafkaProducerService.sendTransactionEvent(event);
            
            long endTime = System.currentTimeMillis();
            log.info("Money transfer completed successfully - Transaction ID: {}, From: {}, To: {}, Amount: {} in {} ms", 
                    savedTransaction.getTransactionId(), fromAccountNumber, request.getToAccountNumber(), request.getAmount(), (endTime - startTime));
            
            return savedTransaction;
        } catch (Exception e) {
            log.error("Money transfer failed for user: {} - Error: {}", email, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public Beneficiary addBeneficiary(AddBeneficiaryRequest request, Authentication authentication) {
        long startTime = System.currentTimeMillis();
        String email = authentication.getName();
        log.info("Adding beneficiary for user: {} - Name: {}, Account: {}", email, request.getBeneficiaryName(), request.getAccountNumber());
        
        try {
            String nameError = nameValidator.validate(request.getBeneficiaryName(), "Beneficiary name", 2, 100);
            if (nameError != null) {
                log.warn("Benef iciary name validation failed: {} - Error: {}", request.getBeneficiaryName(), nameError);
                throw GlobalException.badRequest(nameError);
            }

            String accountError = accountNumberValidator.validate(request.getAccountNumber());
            if (accountError != null) {
                log.warn("Beneficiary account validation failed: {} - Error: {}", request.getAccountNumber(), accountError);
                throw GlobalException.badRequest(accountError);
            }

            if (request.getIfscCode() != null) {
                String ifscError = ifscValidator.validate(request.getIfscCode());
                if (ifscError != null) {
                    log.warn("IFSC code validation failed: {} - Error: {}", request.getIfscCode(), ifscError);
                    throw GlobalException.badRequest(ifscError);
                }
            }

            if (request.getBankName() != null) {
                String bankError = sizeValidator.validate(request.getBankName(), "Bank name", 50);
                if (bankError != null) {
                    log.warn("Bank name validation failed - Error: {}", bankError);
                    throw GlobalException.badRequest(bankError);
                }
            }

            if (request.getBranch() != null) {
                String branchError = sizeValidator.validate(request.getBranch(), "Branch", 50);
                if (branchError != null) {
                    log.warn("Branch validation failed - Error: {}", branchError);
                    throw GlobalException.badRequest(branchError);
                }
            }

            if (request.getUpiId() != null) {
                String upiError = upiValidator.validate(request.getUpiId());
                if (upiError != null) {
                    log.warn("UPI ID validation failed: {} - Error: {}", request.getUpiId(), upiError);
                    throw GlobalException.badRequest(upiError);
                }
            }

            String typeError = beneficiaryTypeValidator.validate(request.getBeneficiaryType());
            if (typeError != null) {
                log.warn("Beneficiary type validation failed: {} - Error: {}", request.getBeneficiaryType(), typeError);
                throw GlobalException.badRequest(typeError);
            }

            if (request.getNickname() != null) {
                String nicknameError = sizeValidator.validate(request.getNickname(), "Nickname", 500);
                if (nicknameError != null) {
                    log.warn("Nickname validation failed - Error: {}", nicknameError);
                    throw GlobalException.badRequest(nicknameError);
                }
            }

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> {
                        log.warn("User not found while adding beneficiary - Email: {}", email);
                        return GlobalException.resourceNotFound("User", "email", email);
                    });

            Beneficiary beneficiary = new Beneficiary();
            beneficiary.setUser(user);
            beneficiary.setBeneficiaryName(request.getBeneficiaryName());
            beneficiary.setAccountNumber(request.getAccountNumber());
            beneficiary.setIfscCode(request.getIfscCode());
            beneficiary.setBankName(request.getBankName());
            beneficiary.setBranch(request.getBranch());
            beneficiary.setUpiId(request.getUpiId());
            beneficiary.setBeneficiaryType(Beneficiary.BeneficiaryType.valueOf(request.getBeneficiaryType()));
            beneficiary.setNickname(request.getNickname());
            beneficiary.setIsActive(true);

            Beneficiary savedBeneficiary = beneficiaryRepository.save(beneficiary);
            
            long endTime = System.currentTimeMillis();
            log.info("Beneficiary added successfully - ID: {}, Name: {} for user: {} in {} ms", savedBeneficiary.getId(), request.getBeneficiaryName(), email, (endTime - startTime));
            
            return savedBeneficiary;
        } catch (Exception e) {
            log.error("Failed to add beneficiary for user: {} - Error: {}", email, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<Beneficiary> getBeneficiaries(Authentication authentication) {
        long startTime = System.currentTimeMillis();
        String email = authentication.getName();
        log.info("Fetching beneficiaries for user: {}", email);
        
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> {
                        log.warn("User not found while fetching beneficiaries - Email: {}", email);
                        return GlobalException.resourceNotFound("User", "email", email);
                    });
            List<Beneficiary> beneficiaries = beneficiaryRepository.findByUserId(user.getId());
            
            long endTime = System.currentTimeMillis();
            log.info("Successfully fetched {} beneficiaries for user: {} in {} ms", beneficiaries.size(), email, (endTime - startTime));
            return beneficiaries;
        } catch (Exception e) {
            log.error("Failed to fetch beneficiaries for user: {} - Error: {}", email, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public ScheduledTransfer scheduleTransfer(ScheduledTransferRequest request, Authentication authentication) {
        long startTime = System.currentTimeMillis();
        String email = authentication.getName();
        log.info("Scheduling transfer for user: {} - Amount: {}, To Account: {}, Scheduled Date: {}", 
                email, request.getAmount(), request.getToAccountNumber(), request.getScheduledDate());
        
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> {
                        log.warn("User not found while scheduling transfer - Email: {}", email);
                        return GlobalException.resourceNotFound("User", "email", email);
                    });

            final String fromAccountNumber;
            if (request.getFromAccountNumber() == null || request.getFromAccountNumber().trim().isEmpty()) {
                Account defaultAccount = accountRepository.findByUserIdAndAccountType(user.getId(), Account.AccountType.SAVINGS)
                        .orElseThrow(() -> {
                            log.warn("Default savings account not found for user: {}", user.getId());
                            return GlobalException.resourceNotFound("Savings Account", "user_id", user.getId());
                        });
                fromAccountNumber = defaultAccount.getAccountNumber();
            } else {
                fromAccountNumber = request.getFromAccountNumber();
            }

            String fromAccountError = accountNumberValidator.validate(fromAccountNumber);
            if (fromAccountError != null) {
                log.warn("From account validation failed: {} - Error: {}", fromAccountNumber, fromAccountError);
                throw GlobalException.badRequest(fromAccountError);
            }

            String toAccountError = accountNumberValidator.validate(request.getToAccountNumber());
            if (toAccountError != null) {
                log.warn("To account validation failed: {} - Error: {}", request.getToAccountNumber(), toAccountError);
                throw GlobalException.badRequest(toAccountError);
            }

            String amountError = amountValidator.validate(request.getAmount());
            if (amountError != null) {
                log.warn("Amount validation failed: {} - Error: {}", request.getAmount(), amountError);
                throw GlobalException.badRequest(amountError);
            }

            String transferTypeError = transferTypeValidator.validate(request.getTransferType());
            if (transferTypeError != null) {
                log.warn("Transfer type validation failed: {} - Error: {}", request.getTransferType(), transferTypeError);
                throw GlobalException.badRequest(transferTypeError);
            }

            if (request.getRemarks() != null) {
                String remarksError = sizeValidator.validate(request.getRemarks(), "Remarks", 500);
                if (remarksError != null) {
                    log.warn("Remarks validation failed - Error: {}", remarksError);
                    throw GlobalException.badRequest(remarksError);
                }
            }

            Account fromAccount = accountRepository.findByAccountNumber(fromAccountNumber)
                    .orElseThrow(() -> {
                        log.warn("From account not found: {}", fromAccountNumber);
                        return GlobalException.resourceNotFound("Account", "accountNumber", fromAccountNumber);
                    });

            if (!fromAccount.getUser().getId().equals(user.getId())) {
                log.warn("Unauthorized scheduled transfer attempt from account: {} by user: {}", fromAccountNumber, email);
                throw new RuntimeException(ValidationConstants.UNAUTHORIZED_ACCESS_TO_ACCOUNT);
            }

            ScheduledTransfer scheduledTransfer = new ScheduledTransfer();
            scheduledTransfer.setFromAccount(fromAccount);
            scheduledTransfer.setUser(user);
            scheduledTransfer.setToAccountNumber(request.getToAccountNumber());
            scheduledTransfer.setToAccountName(request.getToAccountName());
            scheduledTransfer.setAmount(request.getAmount());
            scheduledTransfer.setRemarks(request.getRemarks());
            scheduledTransfer.setTransferType(ScheduledTransfer.TransferType.valueOf(request.getTransferType()));
            scheduledTransfer.setScheduleType(ScheduledTransfer.ScheduleType.valueOf(request.getScheduleType()));
            scheduledTransfer.setScheduledDate(request.getScheduledDate());
            scheduledTransfer.setIsRecurring(request.getIsRecurring());
            scheduledTransfer.setRecurringIntervalDays(request.getRecurringIntervalDays());
            scheduledTransfer.setEndDate(request.getEndDate());
            scheduledTransfer.setStatus(ScheduledTransfer.TransferStatus.SCHEDULED);

            ScheduledTransfer savedTransfer = scheduledTransferRepository.save(scheduledTransfer);
            
            long endTime = System.currentTimeMillis();
            log.info("Transfer scheduled successfully - ID: {}, Amount: {}, Scheduled Date: {} in {} ms", 
                    savedTransfer.getId(), request.getAmount(), request.getScheduledDate(), (endTime - startTime));
            
            return savedTransfer;
        } catch (Exception e) {
            log.error("Failed to schedule transfer for user: {} - Error: {}", email, e.getMessage(), e);
            throw e;
        }
    }
}