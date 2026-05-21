import { apiClient } from './apiClient';
import { mockDb } from './mockDb';

export const profileService = {
  getProfile: async () => {
    if (apiClient.isMock()) {
      await apiClient.sleep(500);
      const user = JSON.parse(localStorage.getItem('mortext_user'));
      apiClient.logResponse('GET', '/api/profile', 200, user);
      return user;
    }
    return await apiClient.get('/profile');
  },

  updateProfile: async (profileData) => {
    if (apiClient.isMock()) {
      await apiClient.sleep(1000);
      const user = JSON.parse(localStorage.getItem('mortext_user')) || {};
      const updatedUser = { ...user, ...profileData };
      localStorage.setItem('mortext_user', JSON.stringify(updatedUser));

      // Also update admin list to sync
      const adminUsers = mockDb.getAdminUsers();
      const updatedAdminUsers = adminUsers.map((u) => {
        if (u.id === user.id || u.name === user.name) {
          return { ...u, ...profileData };
        }
        return u;
      });
      mockDb.saveAdminUsers(updatedAdminUsers);

      apiClient.logResponse('PUT', '/api/profile', 200, updatedUser);
      return updatedUser;
    }
    return await apiClient.put('/profile', profileData);
  },

  changePassword: async (currentPassword, newPassword) => {
    if (apiClient.isMock()) {
      await apiClient.sleep(1000);
      apiClient.logResponse('POST', '/api/profile/change-password', 200, { message: 'Password changed successfully' });
      return true;
    }
    return await apiClient.post('/profile/change-password', { currentPassword, newPassword });
  },

  uploadKyc: async (documentFile) => {
    if (apiClient.isMock()) {
      await apiClient.sleep(1500);
      mockDb.saveKycStatus('Uploaded');
      mockDb.saveKycDocument({ name: documentFile.name || 'document.pdf', size: documentFile.size || 102450 });

      // Update in admin users list
      const user = JSON.parse(localStorage.getItem('mortext_user')) || {};
      const adminUsers = mockDb.getAdminUsers();
      const updatedAdminUsers = adminUsers.map((u) => {
        if (u.name === user.name) {
          return { ...u, kycStatus: 'Uploaded' };
        }
        return u;
      });
      mockDb.saveAdminUsers(updatedAdminUsers);

      // Create notification
      const notifications = mockDb.getNotifications();
      notifications.unshift({
        id: Date.now(),
        title: 'KYC Uploaded',
        message: 'Your identity documents were received and are under review.',
        read: false,
        time: 'Just now'
      });
      mockDb.saveNotifications(notifications);

      apiClient.logResponse('POST', '/api/profile/kyc', 200, { status: 'Uploaded' });
      return { status: 'Uploaded' };
    }

    const formData = new FormData();
    formData.append('document', documentFile);
    return await apiClient.upload('/profile/kyc', formData);
  },

  getKycStatus: async () => {
    if (apiClient.isMock()) {
      await apiClient.sleep(500);
      const status = mockDb.getKycStatus();
      apiClient.logResponse('GET', '/api/profile/kyc', 200, { status });
      return { status };
    }
    return await apiClient.get('/profile/kyc');
  }
};
