import axios from 'axios';
import { apiClient } from './apiClient';
import { mockDb } from './mockDb';

export const authService = {
  login: async (email, password) => {
    // Call live endpoint http://localhost:8080/api/auth/login using Axios directly
    const response = await axios.post('http://localhost:8080/api/auth/login', {
      email: email,
      securityPin: password
    });

    const apiResponse = response.data;
    const authData = apiResponse.data || apiResponse;

    const token = authData.token || `jwt_token_${Date.now()}`;
    const user = {
      id: authData.userId || authData.id || `u-${Date.now()}`,
      name: authData.fullName || authData.name || 'User',
      email: authData.email || email,
      role: authData.role || 'User',
      avatar: 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&q=80&w=120'
    };

    localStorage.setItem('mortext_token', token);
    localStorage.setItem('mortext_user', JSON.stringify(user));
    return user;
  },

  register: async (name, email, password) => {
    // Call live endpoint http://localhost:8080/api/auth/register using Axios directly
    const response = await axios.post('http://localhost:8080/api/auth/register', {
      fullName: name,
      email: email,
      securityPin: password
    });

    const apiResponse = response.data;
    const authData = apiResponse.data || apiResponse;

    const token = authData.token || `jwt_token_${Date.now()}`;
    const user = {
      id: authData.userId || authData.id || `u-${Date.now()}`,
      name: authData.fullName || authData.name || name,
      email: authData.email || email,
      role: authData.role || 'User',
      avatar: 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&q=80&w=120'
    };

    localStorage.setItem('mortext_token', token);
    localStorage.setItem('mortext_user', JSON.stringify(user));
    return user;
  },

  forgotPassword: async (email) => {
    // Call live endpoint http://localhost:8080/api/auth/forgot-password using Axios directly
    const response = await axios.post('http://localhost:8080/api/auth/forgot-password', {
      email: email
    });
    return response.data;
  },

  verifyOtp: async (email, otp) => {
    // Call live endpoint http://localhost:8080/api/auth/verify-otp using Axios directly
    const response = await axios.post('http://localhost:8080/api/auth/verify-otp', {
      email: email,
      otp: otp,
      otpType: 'FORGOT_PASSWORD'
    });
    return response.data;
  },

  resetPassword: async (email, otp, newPassword) => {
    // Call live endpoint http://localhost:8080/api/auth/reset-password using Axios directly
    const response = await axios.post('http://localhost:8080/api/auth/reset-password', {
      email: email,
      otp: otp,
      newSecurityPin: newPassword
    });
    return response.data;
  },

  logout: () => {
    localStorage.removeItem('mortext_token');
    localStorage.removeItem('mortext_user');
  }
};
