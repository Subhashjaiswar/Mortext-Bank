import { apiClient } from './apiClient';
import { mockDb } from './mockDb';

export const transferService = {
  transferMoney: async (fromAccountId, recipientName, amount, category = 'Transfer') => {
    if (apiClient.isMock()) {
      await apiClient.sleep(1500);
      const accounts = mockDb.getAccounts();
      const txs = mockDb.getTransactions();
      const amt = parseFloat(amount);

      // Validate funds
      const account = accounts.find((acc) => acc.id === fromAccountId);
      if (!account) throw new Error('Source account not found.');
      if (account.balance < amt) throw new Error('Insufficient Funds!');

      // Deduct from account
      const updatedAccounts = accounts.map((acc) => {
        if (acc.id === fromAccountId) {
          return { ...acc, balance: acc.balance - amt };
        }
        return acc;
      });
      mockDb.saveAccounts(updatedAccounts);

      // Create new transaction
      const newTx = {
        id: `tx-${Date.now()}`,
        title: `Transfer to ${recipientName}`,
        description: `Instant transfer via UPI/QR/Bank`,
        amount: amt,
        type: 'debit',
        category: category,
        date: new Date().toISOString().split('T')[0],
        status: 'Completed',
        accountId: fromAccountId
      };
      
      txs.unshift(newTx);
      mockDb.saveTransactions(txs);

      // Create notification
      const notifications = mockDb.getNotifications();
      notifications.unshift({
        id: Date.now(),
        title: `Transfer Successful`,
        message: `Sent $${amt.toFixed(2)} to ${recipientName} successfully.`,
        read: false,
        time: 'Just now'
      });
      mockDb.saveNotifications(notifications);

      apiClient.logResponse('POST', '/api/transfers', 200, newTx);
      return newTx;
    }

    return await apiClient.post('/transfers', { fromAccountId, recipientName, amount, category });
  },

  addBeneficiary: async (name, accountNo, bankName, nickname, type = 'External') => {
    if (apiClient.isMock()) {
      await apiClient.sleep(800);
      const beneficiaries = mockDb.getBeneficiaries();
      const newBen = {
        id: `ben-${Date.now()}`,
        name,
        accountNo,
        bankName,
        nickname: nickname || name,
        type
      };
      beneficiaries.push(newBen);
      mockDb.saveBeneficiaries(beneficiaries);

      // Notify
      const notifications = mockDb.getNotifications();
      notifications.unshift({
        id: Date.now(),
        title: 'Beneficiary Added',
        message: `${name} has been added to your beneficiary list.`,
        read: false,
        time: 'Just now'
      });
      mockDb.saveNotifications(notifications);

      apiClient.logResponse('POST', '/api/transfers/beneficiaries', 201, newBen);
      return newBen;
    }

    return await apiClient.post('/transfers/beneficiaries', { name, accountNo, bankName, nickname, type });
  },

  getBeneficiaries: async () => {
    if (apiClient.isMock()) {
      await apiClient.sleep(500);
      const beneficiaries = mockDb.getBeneficiaries();
      apiClient.logResponse('GET', '/api/transfers/beneficiaries', 200, beneficiaries);
      return beneficiaries;
    }
    return await apiClient.get('/transfers/beneficiaries');
  },

  scheduleTransfer: async (transferData) => {
    if (apiClient.isMock()) {
      await apiClient.sleep(1000);
      const responseData = { status: 'success', message: 'Transfer scheduled successfully', data: transferData };
      apiClient.logResponse('POST', '/api/transfers/schedule', 200, responseData);
      return responseData;
    }
    return await apiClient.post('/transfers/schedule', transferData);
  }
};
