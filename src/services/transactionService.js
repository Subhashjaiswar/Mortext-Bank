import { apiClient } from './apiClient';
import { mockDb } from './mockDb';

export const transactionService = {
  getTransactions: async (page = 1, limit = 10) => {
    if (apiClient.isMock()) {
      await apiClient.sleep(500);
      const allTxs = mockDb.getTransactions();
      const startIndex = (page - 1) * limit;
      const endIndex = page * limit;
      const paginated = allTxs.slice(startIndex, endIndex);
      
      const responseData = {
        transactions: paginated,
        total: allTxs.length,
        page,
        limit,
        totalPages: Math.ceil(allTxs.length / limit)
      };

      apiClient.logResponse('GET', `/api/transactions?page=${page}&limit=${limit}`, 200, responseData);
      return responseData;
    }
    return await apiClient.get(`/transactions?page=${page}&limit=${limit}`);
  },

  getTransactionsByRange: async (startDate, endDate) => {
    if (apiClient.isMock()) {
      await apiClient.sleep(600);
      const allTxs = mockDb.getTransactions();
      const filtered = allTxs.filter((tx) => {
        const txDate = new Date(tx.date);
        const start = new Date(startDate);
        const end = new Date(endDate);
        return txDate >= start && txDate <= end;
      });
      apiClient.logResponse('GET', `/api/transactions/range?startDate=${startDate}&endDate=${endDate}`, 200, filtered);
      return filtered;
    }
    return await apiClient.get(`/transactions/range?startDate=${startDate}&endDate=${endDate}`);
  },

  getTransactionsByType: async (transactionType) => {
    if (apiClient.isMock()) {
      await apiClient.sleep(500);
      const allTxs = mockDb.getTransactions();
      const filtered = allTxs.filter((tx) => tx.type === transactionType);
      apiClient.logResponse('GET', `/api/transactions/type/${transactionType}`, 200, filtered);
      return filtered;
    }
    return await apiClient.get(`/transactions/type/${transactionType}`);
  },

  getTransactionsByStatus: async (status) => {
    if (apiClient.isMock()) {
      await apiClient.sleep(500);
      const allTxs = mockDb.getTransactions();
      const filtered = allTxs.filter((tx) => tx.status.toLowerCase() === status.toLowerCase());
      apiClient.logResponse('GET', `/api/transactions/status/${status}`, 200, filtered);
      return filtered;
    }
    return await apiClient.get(`/transactions/status/${status}`);
  }
};
