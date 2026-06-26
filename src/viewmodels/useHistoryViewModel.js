import { useState, useEffect, useCallback } from 'react';
import { useAuth } from '../hooks/useAuth';
import { useToast } from '../components/UI/Toast';
import { transactionService } from '../services/transactionService';

export const useHistoryViewModel = () => {
  const { transactions: globalTransactions } = useAuth();
  const toast = useToast();

  const [transactions, setTransactions] = useState(globalTransactions);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [query, setQuery] = useState('');
  const [typeFilter, setTypeFilter] = useState('all');
  const [categoryFilter, setCategoryFilter] = useState('all');
  const [dateFilter, setDateFilter] = useState('');
  const [exporting, setExporting] = useState(false);

  // Pagination
  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = 8;

  const fetchTransactions = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await transactionService.getTransactions(1, 100);
      const list = data.transactions || data.content || data;
      if (Array.isArray(list) && list.length > 0) {
        setTransactions(list);
      } else if (globalTransactions.length > 0) {
        setTransactions(globalTransactions);
      } else {
        setTransactions([]);
      }
    } catch (err) {
      console.error('Failed to load transactions via transactionService', err);
      setError(err.message || 'Failed to load transactions');
      // Fallback to global transactions from context
      if (globalTransactions.length > 0) {
        setTransactions(globalTransactions);
        setError(null); // Clear error if we have fallback data
      }
    } finally {
      setLoading(false);
    }
  }, [globalTransactions]);

  useEffect(() => {
    fetchTransactions();
  }, [fetchTransactions]);

  // Dynamically extract unique categories from loaded transactions
  const availableCategories = [...new Set(
    transactions
      .map(tx => tx.category)
      .filter(Boolean)
  )].sort();

  const filteredTxs = transactions.filter((tx) => {
    const title = (tx.title || '').toLowerCase();
    const description = (tx.description || '').toLowerCase();
    const searchQuery = query.toLowerCase();

    const matchesSearch = title.includes(searchQuery) || description.includes(searchQuery);
    const matchesType = typeFilter === 'all' || tx.type === typeFilter;
    const matchesCategory = categoryFilter === 'all' || tx.category === categoryFilter;
    const matchesDate = !dateFilter || tx.date === dateFilter;

    return matchesSearch && matchesType && matchesCategory && matchesDate;
  });

  const indexOfLastItem = currentPage * itemsPerPage;
  const indexOfFirstItem = indexOfLastItem - itemsPerPage;
  const paginatedTxs = filteredTxs.slice(indexOfFirstItem, indexOfLastItem);
  const totalPages = Math.ceil(filteredTxs.length / itemsPerPage) || 1;

  const formatCurrency = (val) => {
    return new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(val);
  };

  const handleExportCSV = async () => {
    setExporting(true);
    toast.info('Exporting transaction history ledger via service...', 1000);

    try {
      if (dateFilter) {
        // Range API endpoint simulation
        await transactionService.getTransactionsByRange(dateFilter, dateFilter);
      } else if (typeFilter !== 'all') {
        // Type API endpoint simulation
        await transactionService.getTransactionsByType(typeFilter);
      }

      const headers = 'Transaction ID,Date,Title,Description,Category,Type,Amount,Status,Source\n';
      const csvRows = filteredTxs
        .map(
          (t) =>
            `"${t.id}","${t.date}","${t.title}","${t.description}","${t.category}","${t.type}",${t.amount},"${t.status}","${t.accountId === 'acc-1' ? 'Savings' : 'Current'}"`
        )
        .join('\n');

      const blob = new Blob([headers + csvRows], { type: 'text/csv;charset=utf-8;' });
      const url = URL.createObjectURL(blob);
      const link = document.createElement('a');

      link.setAttribute('href', url);
      link.setAttribute('download', `mortext-bank-transaction-ledger.csv`);
      link.style.visibility = 'hidden';
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);

      toast.success(`Exported ${filteredTxs.length} ledger transactions to CSV!`);
    } catch (err) {
      toast.error('Failed to export CSV file.');
    } finally {
      setExporting(false);
    }
  };

  const resetAllFilters = () => {
    setQuery('');
    setTypeFilter('all');
    setCategoryFilter('all');
    setDateFilter('');
    setCurrentPage(1);
    toast.success('Search filters reset successfully.');
  };

  const handleRetry = () => {
    fetchTransactions();
  };

  return {
    query,
    setQuery,
    typeFilter,
    setTypeFilter,
    categoryFilter,
    setCategoryFilter,
    dateFilter,
    setDateFilter,
    exporting,
    loading,
    error,
    currentPage,
    setCurrentPage,
    filteredTxs,
    paginatedTxs,
    totalPages,
    availableCategories,
    handleExportCSV,
    resetAllFilters,
    formatCurrency,
    handleRetry,
  };
};
