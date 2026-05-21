import { apiClient } from './apiClient';
import { mockDb } from './mockDb';

export const accountService = {
  getAccounts: async () => {
    if (apiClient.isMock()) {
      await apiClient.sleep(500);
      const accounts = mockDb.getAccounts();
      apiClient.logResponse('GET', '/api/accounts', 200, accounts);
      return accounts;
    }
    return await apiClient.get('/accounts');
  },

  getAccountById: async (accountId) => {
    if (apiClient.isMock()) {
      await apiClient.sleep(500);
      const accounts = mockDb.getAccounts();
      const account = accounts.find((acc) => acc.id === accountId);
      if (!account) throw new Error('Account not found');
      apiClient.logResponse('GET', `/api/accounts/${accountId}`, 200, account);
      return account;
    }
    return await apiClient.get(`/accounts/${accountId}`);
  },

  getStatement: async (accountId) => {
    if (apiClient.isMock()) {
      await apiClient.sleep(600);
      const txs = mockDb.getTransactions();
      const statement = txs.filter((tx) => tx.accountId === accountId);
      apiClient.logResponse('GET', `/api/accounts/${accountId}/statement`, 200, statement);
      return statement;
    }
    return await apiClient.get(`/accounts/${accountId}/statement`);
  },

  getStatementByRange: async (accountId, startDate, endDate) => {
    if (apiClient.isMock()) {
      await apiClient.sleep(600);
      const txs = mockDb.getTransactions();
      const filtered = txs
        .filter((tx) => tx.accountId === accountId)
        .filter((tx) => {
          const txDate = new Date(tx.date);
          const start = new Date(startDate);
          const end = new Date(endDate);
          return txDate >= start && txDate <= end;
        });
      apiClient.logResponse(
        'GET',
        `/api/accounts/${accountId}/statement/range?startDate=${startDate}&endDate=${endDate}`,
        200,
        filtered
      );
      return filtered;
    }
    return await apiClient.get(`/accounts/${accountId}/statement/range?startDate=${startDate}&endDate=${endDate}`);
  },

  downloadStatementAsPdf: async (accountId) => {
    if (apiClient.isMock()) {
      await apiClient.sleep(1500);
      // Return a simulated binary PDF blob or array buffer
      const responseData = { status: 'success', message: 'PDF statement generated' };
      apiClient.logResponse('GET', `/api/accounts/${accountId}/statement/download`, 200, responseData);
      return responseData;
    }
    return await apiClient.get(`/accounts/${accountId}/statement/download`);
  }
};
