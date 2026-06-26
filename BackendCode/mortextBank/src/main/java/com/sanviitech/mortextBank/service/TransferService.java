package com.sanviitech.mortextBank.service;

import com.sanviitech.mortextBank.dto.AddBeneficiaryRequest;
import com.sanviitech.mortextBank.dto.ScheduledTransferRequest;
import com.sanviitech.mortextBank.dto.TransferRequest;
import com.sanviitech.mortextBank.entity.Beneficiary;
import com.sanviitech.mortextBank.entity.ScheduledTransfer;
import com.sanviitech.mortextBank.entity.Transaction;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface TransferService {
    Transaction transferMoney(TransferRequest request, Authentication authentication);
    Beneficiary addBeneficiary(AddBeneficiaryRequest request, Authentication authentication);
    List<Beneficiary> getBeneficiaries(Authentication authentication);
    ScheduledTransfer scheduleTransfer(ScheduledTransferRequest request, Authentication authentication);
}