import { apiClient } from './apiClient';
import { mockDb } from './mockDb';

/**
 * Normalize a single transaction object from the API into
 * the shape expected by the UI components.
 * Handles different backend naming conventions gracefully.
 */
export const normalizeTransaction = (tx) => {
  if (!tx) return null;

  return {
    id: tx.id || tx.transactionId || tx.transaction_id || `tx-${Date.now()}`,
    title: tx.title || tx.name || tx.description || tx.remarks || 'Transaction',
    description: tx.description || tx.remarks || tx.memo || tx.narration || '',
    amount: parseFloat(tx.amount) || 0,
    type: normalizeType(tx.type || tx.transactionType || tx.transaction_type),
    category: tx.category || tx.transactionCategory || tx.tag || 'General',
    date: normalizeDate(tx.date || tx.transactionDate || tx.transaction_date || tx.createdAt || tx.created_at),
    status: tx.status || 'Completed',
    accountId: tx.accountId || tx.account_id || tx.sourceAccount || 'acc-1',
  };
};

const normalizeType = (type) => {
  if (!type) return 'debit';
  const lower = type.toLowerCase();
  if (lower === 'credit' || lower === 'cr' || lower === 'deposit' || lower === 'income') return 'credit';
  return 'debit';
};

const normalizeDate = (dateStr) => {
  if (!dateStr) return new Date().toISOString().split('T')[0];
  try {
    const d = new Date(dateStr);
    if (isNaN(d.getTime())) return dateStr;
    return d.toISOString().split('T')[0];
  } catch {
    return dateStr;
  }
};

/**
 * Extract an array of transactions from various possible
 * API response shapes.
 */
export const extractTransactionList = (data) => {
  if (!data) return [];

  // Direct array
  if (Array.isArray(data)) return data;

  // Spring Boot Page object: { content: [...] }
  if (Array.isArray(data.content)) return data.content;

  // Custom wrapper: { transactions: [...] }
  if (Array.isArray(data.transactions)) return data.transactions;

  // Generic wrapper: { data: [...] }
  if (Array.isArray(data.data)) return data.data;

  // Nested wrapper: { result: { items: [...] } }
  if (data.result && Array.isArray(data.result.items)) return data.result.items;

  // Single-item fallback
  if (data.id || data.transactionId) return [data];

  return [];
};

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

    const rawData = await apiClient.get(`/transactions?page=${page > 0 ? page - 1 : 0}&size=${limit}`);
    const rawList = extractTransactionList(rawData);
    const normalized = rawList.map(normalizeTransaction).filter(Boolean);

    return {
      transactions: normalized,
      total: rawData?.totalElements || rawData?.total || normalized.length,
      page: rawData?.number != null ? rawData.number + 1 : page,
      limit: rawData?.size || limit,
      totalPages: rawData?.totalPages || Math.ceil((rawData?.totalElements || normalized.length) / limit),
    };
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

    const rawData = await apiClient.get(`/transactions/range?startDate=${startDate}&endDate=${endDate}`);
    const rawList = extractTransactionList(rawData);
    return rawList.map(normalizeTransaction).filter(Boolean);
  },

  getTransactionsByType: async (transactionType) => {
    if (apiClient.isMock()) {
      await apiClient.sleep(500);
      const allTxs = mockDb.getTransactions();
      const filtered = allTxs.filter((tx) => tx.type === transactionType);
      apiClient.logResponse('GET', `/api/transactions/type/${transactionType}`, 200, filtered);
      return filtered;
    }

    const rawData = await apiClient.get(`/transactions/type/${transactionType}`);
    const rawList = extractTransactionList(rawData);
    return rawList.map(normalizeTransaction).filter(Boolean);
  },

  getTransactionsByStatus: async (status) => {
    if (apiClient.isMock()) {
      await apiClient.sleep(500);
      const allTxs = mockDb.getTransactions();
      const filtered = allTxs.filter((tx) => tx.status.toLowerCase() === status.toLowerCase());
      apiClient.logResponse('GET', `/api/transactions/status/${status}`, 200, filtered);
      return filtered;
    }

    const rawData = await apiClient.get(`/transactions/status/${status}`);
    const rawList = extractTransactionList(rawData);
    return rawList.map(normalizeTransaction).filter(Boolean);
  }
};
