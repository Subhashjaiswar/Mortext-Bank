/**
 * Mortext Bank API Client
 * Centralized API handler featuring standard fetch request wrappers,
 * automatic JWT authorization header injection, request logging, and a
 * robust local Mock Mode fallback for local-only interactive execution.
 */

const BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

// Toggle this to false when connecting to a real running backend
const USE_MOCK_API = false;

// Helper to simulate network latency for Mock responses
const sleep = (ms = 1000) => new Promise((resolve) => setTimeout(resolve, ms));

// Custom request interceptors loggers
const logRequest = (method, url, data = null) => {
  const badgeStyle = 'background: #2563eb; color: white; padding: 2px 6px; border-radius: 4px; font-weight: bold;';
  const textStyle = 'color: #1e293b; font-weight: 500;';
  console.log(`%cAPI REQ%c [${method}] ${url}`, badgeStyle, textStyle, data ? { body: data } : '');
};

const logResponse = (method, url, status, data) => {
  const badgeStyle = 'background: #10b981; color: white; padding: 2px 6px; border-radius: 4px; font-weight: bold;';
  const textStyle = 'color: #0f766e; font-weight: 500;';
  console.log(`%cAPI RES%c [${method}] ${url} (${status})`, badgeStyle, textStyle, { response: data });
};

const getAuthHeaders = () => {
  const token = localStorage.getItem('mortext_token');
  const headers = {
    'Content-Type': 'application/json',
  };
  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }
  return headers;
};

// Base Fetch Wrapper
const request = async (method, path, body = null, isMultipart = false) => {
  const url = `${BASE_URL}${path}`;
  logRequest(method, path, body);

  if (USE_MOCK_API) {
    // Under mock mode, requests are intercepted and handled locally in services
    return null;
  }

  try {
    const headers = isMultipart ? {} : getAuthHeaders();
    if (localStorage.getItem('mortext_token') && isMultipart) {
      headers['Authorization'] = `Bearer ${localStorage.getItem('mortext_token')}`;
    }

    const options = {
      method,
      headers,
    };

    if (body) {
      options.body = isMultipart ? body : JSON.stringify(body);
    }

    const response = await fetch(url, options);

    if (response.status === 401) {
      // Automatic token expiration logout trigger
      localStorage.removeItem('mortext_token');
      localStorage.removeItem('mortext_user');
      window.location.reload();
      throw new Error('Unauthorized session expired. Please log in.');
    }

    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}));
      throw new Error(errorData.message || `HTTP error! status: ${response.status}`);
    }

    const responseData = await response.json();
    logResponse(method, path, response.status, responseData);
    if (responseData && responseData.hasOwnProperty('success') && responseData.hasOwnProperty('data')) {
      return responseData.data;
    }
    return responseData;
  } catch (error) {
    console.error(`%cAPI ERR%c [${method}] ${path}: ${error.message}`, 'background: #ef4444; color: white; padding: 2px 6px; border-radius: 4px; font-weight: bold;', 'color: #991b1b;', error);
    throw error;
  }
};

export const apiClient = {
  get: (path) => request('GET', path),
  post: (path, body) => request('POST', path, body),
  put: (path, body) => request('PUT', path, body),
  delete: (path) => request('DELETE', path),
  upload: (path, formData) => request('POST', path, formData, true),
  isMock: () => USE_MOCK_API,
  sleep,
  logResponse,
};
