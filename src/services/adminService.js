import { apiClient } from './apiClient';
import { mockDb } from './mockDb';

export const adminService = {
  getUsers: async (page = 1, limit = 10) => {
    if (apiClient.isMock()) {
      await apiClient.sleep(500);
      const users = mockDb.getAdminUsers();
      const startIndex = (page - 1) * limit;
      const endIndex = page * limit;
      const paginated = users.slice(startIndex, endIndex);

      const responseData = {
        users: paginated,
        total: users.length,
        page,
        limit,
        totalPages: Math.ceil(users.length / limit)
      };

      apiClient.logResponse('GET', `/api/admin/users?page=${page}&limit=${limit}`, 200, responseData);
      return responseData;
    }
    return await apiClient.get(`/admin/users?page=${page > 0 ? page - 1 : 0}&size=${limit}`);
  },

  getUserById: async (userId) => {
    if (apiClient.isMock()) {
      await apiClient.sleep(500);
      const users = mockDb.getAdminUsers();
      const user = users.find((u) => u.id === userId);
      if (!user) throw new Error('User not found');
      apiClient.logResponse('GET', `/api/admin/users/${userId}`, 200, user);
      return user;
    }
    return await apiClient.get(`/admin/users/${userId}`);
  },

  toggleUserStatus: async (userId) => {
    if (apiClient.isMock()) {
      await apiClient.sleep(800);
      const users = mockDb.getAdminUsers();
      let newStatus = 'Active';
      const updated = users.map((u) => {
        if (u.id === userId) {
          newStatus = u.status === 'Active' ? 'Suspended' : 'Active';
          return { ...u, status: newStatus };
        }
        return u;
      });
      mockDb.saveAdminUsers(updated);
      apiClient.logResponse('POST', `/api/admin/users/${userId}/toggle-status`, 200, { status: newStatus });
      return { status: newStatus };
    }
    return await apiClient.post(`/admin/users/${userId}/toggle-status`);
  },

  getTransactions: async (page = 1, limit = 10) => {
    if (apiClient.isMock()) {
      await apiClient.sleep(500);
      const txs = mockDb.getTransactions();
      const startIndex = (page - 1) * limit;
      const endIndex = page * limit;
      const paginated = txs.slice(startIndex, endIndex);

      const responseData = {
        transactions: paginated,
        total: txs.length,
        page,
        limit,
        totalPages: Math.ceil(txs.length / limit)
      };

      apiClient.logResponse('GET', `/api/admin/transactions?page=${page}&limit=${limit}`, 200, responseData);
      return responseData;
    }
    return await apiClient.get(`/admin/transactions?page=${page > 0 ? page - 1 : 0}&size=${limit}`);
  },

  getTransactionsByStatus: async (status, page = 1, limit = 10) => {
    if (apiClient.isMock()) {
      await apiClient.sleep(500);
      const txs = mockDb.getTransactions().filter((tx) => tx.status.toLowerCase() === status.toLowerCase());
      const startIndex = (page - 1) * limit;
      const endIndex = page * limit;
      const paginated = txs.slice(startIndex, endIndex);

      const responseData = {
        transactions: paginated,
        total: txs.length,
        page,
        limit,
        totalPages: Math.ceil(txs.length / limit)
      };

      apiClient.logResponse('GET', `/api/admin/transactions/status/${status}?page=${page}&limit=${limit}`, 200, responseData);
      return responseData;
    }
    return await apiClient.get(`/admin/transactions/status/${status}?page=${page > 0 ? page - 1 : 0}&size=${limit}`);
  },

  markTransactionFraudulent: async (transactionId, reason) => {
    if (apiClient.isMock()) {
      await apiClient.sleep(800);
      const txs = mockDb.getTransactions();
      const updated = txs.map((tx) => {
        if (tx.id === transactionId) {
          return { ...tx, status: 'Fraudulent', description: `${tx.description} (FLAGGED FRAUD: ${reason})` };
        }
        return tx;
      });
      mockDb.saveTransactions(updated);
      apiClient.logResponse('POST', `/api/admin/transactions/${transactionId}/mark-fraudulent?reason=${reason}`, 200, { status: 'Fraudulent', reason });
      return { status: 'Fraudulent', reason };
    }
    return await apiClient.post(`/admin/transactions/${transactionId}/mark-fraudulent?reason=${encodeURIComponent(reason)}`);
  },

  getKycRequests: async (page = 1, limit = 10) => {
    if (apiClient.isMock()) {
      await apiClient.sleep(500);
      const users = mockDb.getAdminUsers().filter((u) => u.kycStatus && u.kycStatus !== 'Pending');
      const startIndex = (page - 1) * limit;
      const endIndex = page * limit;
      const paginated = users.slice(startIndex, endIndex);

      const responseData = {
        kycRequests: paginated,
        total: users.length,
        page,
        limit,
        totalPages: Math.ceil(users.length / limit)
      };

      apiClient.logResponse('GET', `/api/admin/kyc?page=${page}&limit=${limit}`, 200, responseData);
      return responseData;
    }
    return await apiClient.get(`/admin/kyc?page=${page > 0 ? page - 1 : 0}&size=${limit}`);
  },

  getPendingKycRequests: async (page = 1, limit = 10) => {
    if (apiClient.isMock()) {
      await apiClient.sleep(500);
      const users = mockDb.getAdminUsers().filter((u) => u.kycStatus === 'Uploaded');
      const startIndex = (page - 1) * limit;
      const endIndex = page * limit;
      const paginated = users.slice(startIndex, endIndex);

      const responseData = {
        kycRequests: paginated,
        total: users.length,
        page,
        limit,
        totalPages: Math.ceil(users.length / limit)
      };

      apiClient.logResponse('GET', `/api/admin/kyc/pending?page=${page}&limit=${limit}`, 200, responseData);
      return responseData;
    }
    return await apiClient.get(`/admin/kyc/pending?page=${page > 0 ? page - 1 : 0}&size=${limit}`);
  },

  approveKyc: async (kycId) => {
    if (apiClient.isMock()) {
      await apiClient.sleep(800);
      const users = mockDb.getAdminUsers();
      const updated = users.map((u) => {
        if (u.id === kycId) {
          // If it is the current user logged in, sync their profile status
          const currentUser = JSON.parse(localStorage.getItem('mortext_user')) || {};
          if (u.name === currentUser.name) {
            mockDb.saveKycStatus('Approved');
          }
          return { ...u, kycStatus: 'Approved' };
        }
        return u;
      });
      mockDb.saveAdminUsers(updated);

      // Create notification
      const notifications = mockDb.getNotifications();
      const userToNotify = users.find((u) => u.id === kycId);
      notifications.unshift({
        id: Date.now(),
        title: 'KYC Approved',
        message: `Identity documents for user ${userToNotify ? userToNotify.name : 'client'} have been approved.`,
        read: false,
        time: 'Just now'
      });
      mockDb.saveNotifications(notifications);

      apiClient.logResponse('POST', `/api/admin/kyc/${kycId}/approve`, 200, { status: 'Approved' });
      return { status: 'Approved' };
    }
    return await apiClient.post(`/admin/kyc/${kycId}/approve`);
  },

  rejectKyc: async (kycId, reason) => {
    if (apiClient.isMock()) {
      await apiClient.sleep(800);
      const users = mockDb.getAdminUsers();
      const updated = users.map((u) => {
        if (u.id === kycId) {
          const currentUser = JSON.parse(localStorage.getItem('mortext_user')) || {};
          if (u.name === currentUser.name) {
            mockDb.saveKycStatus('Rejected');
          }
          return { ...u, kycStatus: 'Rejected' };
        }
        return u;
      });
      mockDb.saveAdminUsers(updated);

      // Create notification
      const notifications = mockDb.getNotifications();
      const userToNotify = users.find((u) => u.id === kycId);
      notifications.unshift({
        id: Date.now(),
        title: 'KYC Verification Failed',
        message: `Identity documents for user ${userToNotify ? userToNotify.name : 'client'} were rejected: ${reason}`,
        read: false,
        time: 'Just now'
      });
      mockDb.saveNotifications(notifications);

      apiClient.logResponse('POST', `/api/admin/kyc/${kycId}/reject?reason=${reason}`, 200, { status: 'Rejected', reason });
      return { status: 'Rejected', reason };
    }
    return await apiClient.post(`/admin/kyc/${kycId}/reject?reason=${encodeURIComponent(reason)}`);
  },

  getAnalytics: async () => {
    if (apiClient.isMock()) {
      await apiClient.sleep(600);
      const users = mockDb.getAdminUsers();
      const txs = mockDb.getTransactions();

      const activeUsersCount = users.filter((u) => u.status === 'Active').length;
      const totalVolume = txs.reduce((sum, tx) => sum + tx.amount, 0);
      const fraudCount = txs.filter((tx) => tx.status === 'Fraudulent').length;

      const analyticsData = {
        totalUsers: users.length,
        activeUsers: activeUsersCount,
        totalTransactionVolume: totalVolume,
        fraudAlertsCount: fraudCount,
        recentSignupsRate: '+15.2% last month',
        revenueGraphData: [
          { label: 'Jan', value: 8400 },
          { label: 'Feb', value: 12500 },
          { label: 'Mar', value: 10200 },
          { label: 'Apr', value: 19800 },
          { label: 'May', value: 15400 }
        ]
      };

      apiClient.logResponse('GET', '/api/admin/analytics', 200, analyticsData);
      return analyticsData;
    }
    return await apiClient.get('/admin/analytics');
  },

  addIpToWhitelist: async (ipAddress, description) => {
    if (apiClient.isMock()) {
      await apiClient.sleep(500);
      const entry = {
        id: Date.now(),
        ipAddress,
        description,
        active: true,
        createdBy: 'Admin',
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString()
      };
      apiClient.logResponse('POST', '/api/admin/ip-whitelist', 200, entry);
      return entry;
    }
    return await apiClient.post('/admin/ip-whitelist', { ipAddress, description });
  },

  getActiveIpWhitelist: async () => {
    if (apiClient.isMock()) {
      await apiClient.sleep(500);
      const localSaved = JSON.parse(localStorage.getItem('mortext_saved_ips') || '[]');
      const activeList = localSaved.map((item, idx) => ({
        id: idx,
        ipAddress: item.ip,
        description: item.description || 'Saved IP',
        active: true
      }));
      apiClient.logResponse('GET', '/api/admin/ip-whitelist/active', 200, activeList);
      return activeList;
    }
    return await apiClient.get('/admin/ip-whitelist/active');
  },

  removeIpFromWhitelist: async (id) => {
    if (apiClient.isMock()) {
      await apiClient.sleep(500);
      apiClient.logResponse('DELETE', `/api/admin/ip-whitelist/${id}`, 200, { success: true });
      return { success: true };
    }
    return await apiClient.delete(`/admin/ip-whitelist/${id}`);
  },

  applyIpWhitelist: async () => {
    if (apiClient.isMock()) {
      await apiClient.sleep(500);
      const localSaved = JSON.parse(localStorage.getItem('mortext_saved_ips') || '[]');
      const activeIps = localSaved.map(item => item.ip);
      apiClient.logResponse('POST', '/api/admin/ip-whitelist/apply', 200, activeIps);
      return activeIps;
    }
    return await apiClient.post('/admin/ip-whitelist/apply');
  }
};
