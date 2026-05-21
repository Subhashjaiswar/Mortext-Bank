import { apiClient } from './apiClient';
import { mockDb } from './mockDb';

export const dashboardService = {
  getDashboardData: async () => {
    if (apiClient.isMock()) {
      await apiClient.sleep(500);
      const accounts = mockDb.getAccounts();
      const transactions = mockDb.getTransactions();
      const notifications = mockDb.getNotifications();
      const kycStatus = mockDb.getKycStatus();
      const cards = mockDb.getCards();

      const totalBalance = accounts.reduce((sum, acc) => sum + acc.balance, 0);

      const dashboardData = {
        totalBalance,
        accounts,
        recentTransactions: transactions.slice(0, 3),
        notifications: notifications.slice(0, 3),
        kycStatus,
        cardsCount: cards.length,
        weeklyExpenses: 3170.00,
        monthlyCashflow: 10800.00
      };

      apiClient.logResponse('GET', '/api/dashboard', 200, dashboardData);
      return dashboardData;
    }
    return await apiClient.get('/dashboard');
  }
};
