import { apiClient } from './apiClient';

export const uploadService = {
  uploadImage: async (imageFile) => {
    if (apiClient.isMock()) {
      await apiClient.sleep(1200);
      const mockResponse = {
        status: 'success',
        url: 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&q=80&w=120',
        message: 'Image uploaded successfully'
      };
      apiClient.logResponse('POST', '/api/upload/image', 201, mockResponse);
      return mockResponse;
    }

    const formData = new FormData();
    formData.append('image', imageFile);
    return await apiClient.upload('/upload/image', formData);
  },

  uploadDocument: async (documentFile) => {
    if (apiClient.isMock()) {
      await apiClient.sleep(1200);
      const mockResponse = {
        status: 'success',
        url: 'https://mortext-bank.s3.amazonaws.com/documents/kyc_id_card.pdf',
        message: 'Document uploaded successfully'
      };
      apiClient.logResponse('POST', '/api/upload/document', 201, mockResponse);
      return mockResponse;
    }

    const formData = new FormData();
    formData.append('document', documentFile);
    return await apiClient.upload('/upload/document', formData);
  }
};
