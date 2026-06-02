import { useState, useEffect } from 'react';
import { useAuth } from '../hooks/useAuth';
import { dashboardService } from '../services/dashboardService';

export const useDashboardViewModel = () => {
  const {
    user,
    totalBalance: globalTotalBalance,
    accounts: globalAccounts,
    transactions: globalTransactions,
    notifications: globalNotifications,
    kycStatus: globalKycStatus,
    cards: globalCards,
  } = useAuth();

  const [dashboardData, setDashboardData] = useState({
    totalBalance: globalTotalBalance,
    accounts: globalAccounts,
    recentTransactions: globalTransactions.slice(0, 3),
    notifications: globalNotifications.slice(0, 3),
    kycStatus: globalKycStatus,
    cardsCount: globalCards.length,
    weeklyExpenses: 3170.00,
    monthlyCashflow: 10800.00,
  });

  const [selectedTx, setSelectedTx] = useState(null);

  useEffect(() => {
    const fetchDashboard = async () => {
      try {
        const data = await dashboardService.getDashboardData();
        setDashboardData((prev) => ({
          ...prev,
          ...data,
          accounts: data.accounts || globalAccounts,
          recentTransactions: data.recentTransactions || globalTransactions.slice(0, 3),
          notifications: data.notifications || globalNotifications.slice(0, 3),
        }));
      } catch (err) {
        console.error('Failed to load dashboard statistics via dashboardService', err);
      }
    };
    fetchDashboard();
  }, [globalTotalBalance, globalAccounts, globalTransactions, globalNotifications, globalKycStatus, globalCards]);

  const formatCurrency = (val) => {
    return new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(val);
  };

  // Mock Expense Chart Data
  const expenseData = [
    { label: 'Mon', value: 120 },
    { label: 'Tue', value: 450 },
    { label: 'Wed', value: 210 },
    { label: 'Thu', value: 890 },
    { label: 'Fri', value: 340 },
    { label: 'Sat', value: 650 },
    { label: 'Sun', value: 480 },
  ];

  // Mock Debit vs Credit Bar Data
  const monthlySummary = [
    { label: 'Jan', value: 1200 },
    { label: 'Feb', value: 2300 },
    { label: 'Mar', value: 1800 },
    { label: 'Apr', value: 3100 },
    { label: 'May', value: 2400 },
  ];

  return {
    user,
    selectedTx,
    setSelectedTx,
    formatCurrency,
    expenseData,
    monthlySummary,
    ...dashboardData,
  };
};
