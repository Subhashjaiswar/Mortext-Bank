import { apiClient } from './apiClient';
import { mockDb } from './mockDb';

export const transferService = {
  transferMoney: async (payload) => {
    // Send the correctly formatted payload to match TransferRequest DTO in the backend
    apiClient.logResponse('POST', '/api/transfers', 200, payload);
    return await apiClient.post('/transfers', payload);
  },

  addBeneficiary: async (name, accountNumber, ifscCode, nickname) => {
    // Always call the live backend API: POST /api/transfers/beneficiaries
    const payload = { 
      beneficiaryName: name, 
      accountNumber, 
      ifscCode, 
      nickname,
      beneficiaryType: 'BANK_ACCOUNT'
    };
    apiClient.logResponse('POST', '/api/transfers/beneficiaries', 201, payload);
    return await apiClient.post('/transfers/beneficiaries', payload);
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
