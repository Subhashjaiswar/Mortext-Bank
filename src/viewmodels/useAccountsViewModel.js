import { useState, useEffect } from 'react';
import { useAuth } from '../hooks/useAuth';
import { useToast } from '../components/UI/Toast';
import { accountService } from '../services/accountService';

export const useAccountsViewModel = () => {
  const { accounts: globalAccounts, transactions: globalTransactions, fetchFinancialData } = useAuth();
  const toast = useToast();

  const [accounts, setAccounts] = useState(globalAccounts);
  const [selectedAccId, setSelectedAccId] = useState(globalAccounts[0]?.id || 'acc-1');
  const [searchTerm, setSearchTerm] = useState('');
  const [categoryFilter, setCategoryFilter] = useState('All');
  const [downloading, setDownloading] = useState(false);
  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = 4;

  // Re-fetch accounts on mount or sync with global state
  useEffect(() => {
    const fetchAccounts = async () => {
      try {
        const data = await accountService.getAccounts();
        setAccounts(data);
      } catch (err) {
        console.error('Failed to load accounts via accountService', err);
      }
    };
    fetchAccounts();
  }, [globalAccounts]);

  const currentAccount = accounts.find((a) => a.id === selectedAccId) || accounts[0] || {};

  // Filter transactions belonging to selected account
  const filteredTxs = globalTransactions
    .filter((tx) => tx.accountId === selectedAccId)
    .filter((tx) => {
      const matchesSearch =
        tx.title.toLowerCase().includes(searchTerm.toLowerCase()) ||
        tx.description.toLowerCase().includes(searchTerm.toLowerCase());
      const matchesCategory = categoryFilter === 'All' || tx.category === categoryFilter;
      return matchesSearch && matchesCategory;
    });

  // Pagination logic
  const indexOfLastItem = currentPage * itemsPerPage;
  const indexOfFirstItem = indexOfLastItem - itemsPerPage;
  const paginatedTxs = filteredTxs.slice(indexOfFirstItem, indexOfLastItem);
  const totalPages = Math.ceil(filteredTxs.length / itemsPerPage) || 1;

  const formatCurrency = (val) => {
    return new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(val);
  };

  const handleDownloadStatement = async () => {
    setDownloading(true);
    toast.info('Formatting audit ledger via service...', 1000);

    try {
      // Call service layer for download
      await accountService.downloadStatementAsPdf(selectedAccId);

      // Trigger the local simulated browser download
      const headers = 'Transaction ID,Date,Title,Description,Category,Type,Amount,Status\n';
      const csvRows = filteredTxs
        .map(
          (t) =>
            `"${t.id}","${t.date}","${t.title}","${t.description}","${t.category}","${t.type}",${t.amount},"${t.status}"`
        )
        .join('\n');

      const blob = new Blob([headers + csvRows], { type: 'text/csv;charset=utf-8;' });
      const url = URL.createObjectURL(blob);
      const link = document.createElement('a');

      link.setAttribute('href', url);
      link.setAttribute(
        'download',
        `mortext-bank-statement-${currentAccount.name ? currentAccount.name.replace(/\s+/g, '-').toLowerCase() : 'statement'}.csv`
      );
      link.style.visibility = 'hidden';
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);

      toast.success(`Statement for ${currentAccount.name || 'Account'} downloaded successfully!`);
    } catch (err) {
      toast.error('Failed to generate statement file.');
    } finally {
      setDownloading(false);
    }
  };

  return {
    accounts,
    selectedAccId,
    setSelectedAccId,
    searchTerm,
    setSearchTerm,
    categoryFilter,
    setCategoryFilter,
    downloading,
    currentPage,
    setCurrentPage,
    currentAccount,
    paginatedTxs,
    totalPages,
    filteredTxs,
    handleDownloadStatement,
    formatCurrency,
  };
};
