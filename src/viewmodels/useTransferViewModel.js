import { useState, useEffect } from 'react';
import { useAuth } from '../hooks/useAuth';
import { useToast } from '../components/UI/Toast';
import { transferService } from '../services/transferService';
import { accountService } from '../services/accountService';

export const useTransferViewModel = () => {
  const { accounts: globalAccounts, beneficiaries: globalBeneficiaries, triggerGlobalRefresh } = useAuth();
  const toast = useToast();

  // Tab: 'direct', 'upi', 'qr', 'schedule'
  const [activeSubTab, setActiveSubTab] = useState('direct');

  // Accounts & Beneficiaries local state loaded from service
  const [accounts, setAccounts] = useState(globalAccounts);
  const [beneficiaries, setBeneficiaries] = useState(globalBeneficiaries);

  // Transaction Form States
  const [fromAccount, setFromAccount] = useState(globalAccounts[0]?.id || 'acc-1');
  const [selectedBen, setSelectedBen] = useState('');
  const [amount, setAmount] = useState('');
  const [remarks, setRemarks] = useState('');
  const [upiId, setUpiId] = useState('');
  const [scheduledDate, setScheduledDate] = useState('');

  // Modals
  const [showAddBenModal, setShowAddBenModal] = useState(false);
  const [showQrScanner, setShowQrScanner] = useState(false);
  const [showConfirmModal, setShowConfirmModal] = useState(false);
  const [showSuccessModal, setShowSuccessModal] = useState(false);

  // Add Beneficiary form states
  const [benName, setBenName] = useState('');
  const [benAccNo, setBenAccNo] = useState('');
  const [benIfscCode, setBenIfscCode] = useState('');
  const [benNickname, setBenNickname] = useState('');

  // UX Status
  const [processing, setProcessing] = useState(false);
  const [successReceipt, setSuccessReceipt] = useState(null);

  const fetchTransferData = async () => {
    try {
      // Run both fetches concurrently
      const [bens, accs] = await Promise.all([
        transferService.getBeneficiaries().catch(() => []),
        accountService.getAccounts().catch(() => [])
      ]);
      if (bens && bens.length > 0) setBeneficiaries(bens);
      if (accs && accs.length > 0) setAccounts(accs);
    } catch (err) {
      console.error('Failed to load transfer data', err);
    }
  };

  useEffect(() => {
    fetchTransferData();
  }, [globalBeneficiaries]);

  useEffect(() => {
    setAccounts(globalAccounts);
  }, [globalAccounts]);

  const resetForm = () => {
    setAmount('');
    setRemarks('');
    setUpiId('');
    setScheduledDate('');
    setSelectedBen('');
  };

  const handleAddBeneficiarySubmit = async (e) => {
    e.preventDefault();
    if (!benName || !benAccNo || !benIfscCode) {
      toast.error('Please enter all required fields.');
      return;
    }

    try {
      await transferService.addBeneficiary(benName, benAccNo, benIfscCode, benNickname);
      setShowAddBenModal(false);

      // Auto-select newly added beneficiary
      setSelectedBen(benName);

      // Clear fields
      setBenName('');
      setBenAccNo('');
      setBenIfscCode('');
      setBenNickname('');

      toast.success(`${benName} added as a beneficiary.`);
      fetchTransferData();
      if (triggerGlobalRefresh) triggerGlobalRefresh();
    } catch (err) {
      toast.error('Failed to add beneficiary.');
    }
  };

  const handleQRScanSuccess = () => {
    setShowQrScanner(false);
    toast.success('QR Code read successfully!');
    // Autofill form
    setAmount('45.00');
    setRemarks('Scan Merchant Payment');
    setSelectedBen('Mortext Global Merchant #4910');
  };

  const handleInitiateTransfer = (e) => {
    e.preventDefault();

    if (!amount || parseFloat(amount) <= 0) {
      toast.error('Please enter a valid transfer amount.');
      return;
    }

    const sourceAcc = accounts.find((a) => a.id === fromAccount);
    if (sourceAcc && sourceAcc.balance < parseFloat(amount)) {
      toast.error('Insufficient funds in the selected account.');
      return;
    }

    if (activeSubTab === 'direct' && !selectedBen) {
      toast.error('Please select a recipient.');
      return;
    }

    if (activeSubTab === 'upi' && !upiId) {
      toast.error('Please enter a valid UPI Address.');
      return;
    }

    if (activeSubTab === 'schedule' && (!selectedBen || !scheduledDate)) {
      toast.error('Please select beneficiary and schedule date.');
      return;
    }

    setShowConfirmModal(true);
  };

  const handleConfirmTransfer = async () => {
    setShowConfirmModal(false);
    setProcessing(true);

    try {
      const recipientName = activeSubTab === 'upi' ? upiId : selectedBen;
      const sourceAcc = accounts.find((a) => a.id === fromAccount || a.accountNumber === fromAccount);
      const ben = beneficiaries.find(b => (b.beneficiaryName || b.name) === selectedBen);

      const fromAccountNumber = sourceAcc ? (sourceAcc.accountNumber || sourceAcc.id) : '';
      const toAccountNumber = activeSubTab === 'upi' ? upiId : (ben ? (ben.accountNumber || ben.accountNo) : '');
      const category = activeSubTab === 'schedule' ? 'Bills' : 'Transfer';
      const transferType = activeSubTab === 'upi' ? 'UPI' : 'BANK_TRANSFER';

      if (activeSubTab === 'schedule') {
        // Schedule API Endpoint
        await transferService.scheduleTransfer({
          fromAccountId: fromAccount,
          recipientName,
          amount: parseFloat(amount),
          date: scheduledDate,
          remarks
        });
      } else {
        // Live Transfer API Endpoint
        await transferService.transferMoney({
          fromAccountNumber,
          toAccountNumber,
          amount: parseFloat(amount),
          remarks: remarks || category,
          transferType
        });
      }

      setSuccessReceipt({
        txId: `TXN-${Math.floor(Math.random() * 9000000) + 1000000}`,
        recipient: recipientName,
        amount: parseFloat(amount),
        date: new Date().toLocaleDateString(),
        source: sourceAcc ? sourceAcc.name : 'Savings Account',
        type: activeSubTab
      });

      setProcessing(false);
      setShowSuccessModal(true);
      resetForm();
      if (triggerGlobalRefresh) triggerGlobalRefresh();
    } catch (err) {
      setProcessing(false);
      toast.error(err.message || 'Transfer failed.');
    }
  };

  return {
    activeSubTab,
    setActiveSubTab,
    accounts,
    beneficiaries,
    fromAccount,
    setFromAccount,
    selectedBen,
    setSelectedBen,
    amount,
    setAmount,
    remarks,
    setRemarks,
    upiId,
    setUpiId,
    scheduledDate,
    setScheduledDate,
    showAddBenModal,
    setShowAddBenModal,
    showQrScanner,
    setShowQrScanner,
    showConfirmModal,
    setShowConfirmModal,
    showSuccessModal,
    setShowSuccessModal,
    benName,
    setBenName,
    benAccNo,
    setBenAccNo,
    benIfscCode,
    setBenIfscCode,
    benNickname,
    setBenNickname,
    processing,
    successReceipt,
    handleAddBeneficiarySubmit,
    handleQRScanSuccess,
    handleInitiateTransfer,
    handleConfirmTransfer,
    resetForm,
  };
};
