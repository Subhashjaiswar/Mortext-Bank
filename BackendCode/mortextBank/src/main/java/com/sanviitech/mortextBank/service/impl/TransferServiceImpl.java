package com.sanviitech.mortextBank.service.impl;

import com.sanviitech.mortextBank.dto.AddBeneficiaryRequest;
import com.sanviitech.mortextBank.dto.ScheduledTransferRequest;
import com.sanviitech.mortextBank.dto.TransferRequest;
import com.sanviitech.mortextBank.entity.*;
import com.sanviitech.mortextBank.constants.ValidationConstants;
import com.sanviitech.mortextBank.util.GlobalException;
import com.sanviitech.mortextBank.repository.*;
import com.sanviitech.mortextBank.service.TransferService;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransferServiceImpl implements TransferService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final BeneficiaryRepository beneficiaryRepository;
    private final ScheduledTransferRepository scheduledTransferRepository;

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
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> GlobalException.resourceNotFound("User", "email", authentication.getName()));

        final String fromAccountNumber;
        if (request.getFromAccountNumber() == null || request.getFromAccountNumber().trim().isEmpty()) {
            Account defaultAccount = accountRepository.findByUserIdAndAccountType(user.getId(), Account.AccountType.SAVINGS)
                    .orElseThrow(() -> GlobalException.resourceNotFound("Savings Account", "user_id", user.getId()));
            fromAccountNumber = defaultAccount.getAccountNumber();
        } else {
            fromAccountNumber = request.getFromAccountNumber();
        }

        String fromAccountError = accountNumberValidator.validate(fromAccountNumber);
        if (fromAccountError != null) {
            throw GlobalException.badRequest(fromAccountError);
        }

        String toAccountError = accountNumberValidator.validate(request.getToAccountNumber());
        if (toAccountError != null) {
            throw GlobalException.badRequest(toAccountError);
        }

        String amountError = amountValidator.validate(request.getAmount());
        if (amountError != null) {
            throw GlobalException.badRequest(amountError);
        }

        String transferTypeError = transferTypeValidator.validate(request.getTransferType());
        if (transferTypeError != null) {
            throw GlobalException.badRequest(transferTypeError);
        }

        if (request.getRemarks() != null) {
            String remarksError = sizeValidator.validate(request.getRemarks(), "Remarks", 500);
            if (remarksError != null) {
                throw GlobalException.badRequest(remarksError);
            }
        }

        Account fromAccount = accountRepository.findByAccountNumber(fromAccountNumber)
                .orElseThrow(() -> GlobalException.resourceNotFound("Account", "accountNumber", fromAccountNumber));

        if (!fromAccount.getUser().getId().equals(user.getId())) {
            throw new RuntimeException(ValidationConstants.UNAUTHORIZED_ACCESS_TO_ACCOUNT);
        }

        if (fromAccount.getAvailableBalance().compareTo(request.getAmount()) < 0) {
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

        return transactionRepository.save(transaction);
    }

    @Override
    public Beneficiary addBeneficiary(AddBeneficiaryRequest request, Authentication authentication) {
        String nameError = nameValidator.validate(request.getBeneficiaryName(), "Beneficiary name", 2, 100);
        if (nameError != null) {
            throw GlobalException.badRequest(nameError);
        }

        String accountError = accountNumberValidator.validate(request.getAccountNumber());
        if (accountError != null) {
            throw GlobalException.badRequest(accountError);
        }

        if (request.getIfscCode() != null) {
            String ifscError = ifscValidator.validate(request.getIfscCode());
            if (ifscError != null) {
                throw GlobalException.badRequest(ifscError);
            }
        }

        if (request.getBankName() != null) {
            String bankError = sizeValidator.validate(request.getBankName(), "Bank name", 50);
            if (bankError != null) {
                throw GlobalException.badRequest(bankError);
            }
        }

        if (request.getBranch() != null) {
            String branchError = sizeValidator.validate(request.getBranch(), "Branch", 50);
            if (branchError != null) {
                throw GlobalException.badRequest(branchError);
            }
        }

        if (request.getUpiId() != null) {
            String upiError = upiValidator.validate(request.getUpiId());
            if (upiError != null) {
                throw GlobalException.badRequest(upiError);
            }
        }

        String typeError = beneficiaryTypeValidator.validate(request.getBeneficiaryType());
        if (typeError != null) {
            throw GlobalException.badRequest(typeError);
        }

        if (request.getNickname() != null) {
            String nicknameError = sizeValidator.validate(request.getNickname(), "Nickname", 500);
            if (nicknameError != null) {
                throw GlobalException.badRequest(nicknameError);
            }
        }

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> GlobalException.resourceNotFound("User", "email", authentication.getName()));

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

        return beneficiaryRepository.save(beneficiary);
    }

    @Override
    public List<Beneficiary> getBeneficiaries(Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> GlobalException.resourceNotFound("User", "email", authentication.getName()));
        return beneficiaryRepository.findByUserId(user.getId());
    }

    @Override
    public ScheduledTransfer scheduleTransfer(ScheduledTransferRequest request, Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> GlobalException.resourceNotFound("User", "email", authentication.getName()));

        final String fromAccountNumber;
        if (request.getFromAccountNumber() == null || request.getFromAccountNumber().trim().isEmpty()) {
            Account defaultAccount = accountRepository.findByUserIdAndAccountType(user.getId(), Account.AccountType.SAVINGS)
                    .orElseThrow(() -> GlobalException.resourceNotFound("Savings Account", "user_id", user.getId()));
            fromAccountNumber = defaultAccount.getAccountNumber();
        } else {
            fromAccountNumber = request.getFromAccountNumber();
        }

        String fromAccountError = accountNumberValidator.validate(fromAccountNumber);
        if (fromAccountError != null) {
            throw GlobalException.badRequest(fromAccountError);
        }

        String toAccountError = accountNumberValidator.validate(request.getToAccountNumber());
        if (toAccountError != null) {
            throw GlobalException.badRequest(toAccountError);
        }

        String amountError = amountValidator.validate(request.getAmount());
        if (amountError != null) {
            throw GlobalException.badRequest(amountError);
        }

        String transferTypeError = transferTypeValidator.validate(request.getTransferType());
        if (transferTypeError != null) {
            throw GlobalException.badRequest(transferTypeError);
        }

        if (request.getRemarks() != null) {
            String remarksError = sizeValidator.validate(request.getRemarks(), "Remarks", 500);
            if (remarksError != null) {
                throw GlobalException.badRequest(remarksError);
            }
        }

        Account fromAccount = accountRepository.findByAccountNumber(fromAccountNumber)
                .orElseThrow(() -> GlobalException.resourceNotFound("Account", "accountNumber", fromAccountNumber));

        if (!fromAccount.getUser().getId().equals(user.getId())) {
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

        return scheduledTransferRepository.save(scheduledTransfer);
    }
}