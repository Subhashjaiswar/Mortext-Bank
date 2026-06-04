/**
 * Mortext Bank Mock Local Storage Database
 * Standardized data repository that seeds and persists simulated bank records,
 * allowing live interactive changes without a backend database.
 */

const SEED_ACCOUNTS = [
  { id: '1', name: 'Savings Account', number: '•••• •••• •••• 4892', type: 'savings', balance: 12450.80, status: 'Active' },
  { id: '2', name: 'Current Account', number: '•••• •••• •••• 9210', type: 'current', balance: 48290.45, status: 'Active' }
];

const SEED_BENEFICIARIES = [
  { id: 'ben-1', name: 'Alice Smith', accountNo: '1234567890', bankName: 'Mortext Bank', nickname: 'Alice', type: 'Internal' },
  { id: 'ben-2', name: 'John Doe', accountNo: '9876543210', bankName: 'Chase Bank', nickname: 'Johnny', type: 'External' },
  { id: 'ben-3', name: 'Emma Watson', accountNo: '5566778899', bankName: 'Wells Fargo', nickname: 'Emma', type: 'External' }
];

const SEED_CARDS = [
  { id: 'card-1', type: 'debit', number: '4532 8902 4892 7831', holder: 'Alex Carter', expiry: '12/29', cvv: '182', status: 'Active', limit: 5000, maxLimit: 10000 },
  { id: 'card-2', type: 'credit', number: '5412 7511 9210 5543', holder: 'Alex Carter', expiry: '06/28', cvv: '495', status: 'Active', limit: 8000, maxLimit: 15000 }
];

const SEED_TRANSACTIONS = [
  { id: 'tx-1', title: 'Salary Credit', description: 'Monthly salary from Tech Corp', amount: 4200.00, type: 'credit', category: 'Income', date: '2026-05-15', status: 'Completed', accountId: 'acc-1' },
  { id: 'tx-2', title: 'Shopping at Supermarket', description: 'Grocery items', amount: 120.50, type: 'debit', category: 'Shopping', date: '2026-05-14', status: 'Completed', accountId: 'acc-2' },
  { id: 'tx-3', title: 'Sent to John Doe', description: 'Rent sharing', amount: 250.00, type: 'debit', category: 'Transfer', date: '2026-05-13', status: 'Completed', accountId: 'acc-1' },
  { id: 'tx-4', title: 'Electricity Bill', description: 'Power supply bill', amount: 85.00, type: 'debit', category: 'Bills', date: '2026-05-10', status: 'Completed', accountId: 'acc-2' },
  { id: 'tx-5', title: 'Sent to Alice Smith', description: 'Gift', amount: 1200.00, type: 'debit', category: 'Transfer', date: '2026-05-08', status: 'Completed', accountId: 'acc-1' }
];

const SEED_NOTIFICATIONS = [
  { id: 1, title: 'Welcome to Mortext Bank', message: 'Your account is active and secure.', read: false, time: '1 hour ago' },
  { id: 2, title: 'Salary Credited', message: '$4,200.00 deposited into your Savings Account.', read: false, time: '3 days ago' },
  { id: 3, title: 'Card PIN Changed', message: 'Your Debit Card PIN was updated successfully.', read: true, time: '5 days ago' }
];

const SEED_ADMIN_USERS = [
  { id: 'u-1', name: 'Alex Carter', email: 'alex@mortext.com', joined: '2026-01-10', kycStatus: 'Pending', role: 'User', status: 'Active' },
  { id: 'u-2', name: 'Sarah Connor', email: 'sarah@cyber.com', joined: '2026-02-15', kycStatus: 'Approved', role: 'User', status: 'Active' },
  { id: 'u-3', name: 'Bruce Wayne', email: 'bruce@wayne.com', joined: '2026-03-22', kycStatus: 'Approved', role: 'User', status: 'Active' }
];

// Helper to load/save JSON from local storage
const load = (key, seed) => {
  const data = localStorage.getItem(`db_${key}`);
  if (!data) {
    localStorage.setItem(`db_${key}`, JSON.stringify(seed));
    return seed;
  }
  return JSON.parse(data);
};

const save = (key, data) => {
  localStorage.setItem(`db_${key}`, JSON.stringify(data));
};

export const mockDb = {
  getAccounts: () => load('accounts', SEED_ACCOUNTS),
  saveAccounts: (data) => save('accounts', data),

  getBeneficiaries: () => load('beneficiaries', SEED_BENEFICIARIES),
  saveBeneficiaries: (data) => save('beneficiaries', data),

  getCards: () => load('cards', SEED_CARDS),
  saveCards: (data) => save('cards', data),

  getTransactions: () => load('transactions', SEED_TRANSACTIONS),
  saveTransactions: (data) => save('transactions', data),

  getNotifications: () => load('notifications', SEED_NOTIFICATIONS),
  saveNotifications: (data) => save('notifications', data),

  getAdminUsers: () => load('admin_users', SEED_ADMIN_USERS),
  saveAdminUsers: (data) => save('admin_users', data),

  getKycStatus: () => {
    return localStorage.getItem('db_kyc_status') || 'Pending';
  },
  saveKycStatus: (status) => {
    localStorage.setItem('db_kyc_status', status);
  },

  getKycDocument: () => {
    const doc = localStorage.getItem('db_kyc_document');
    return doc ? JSON.parse(doc) : null;
  },
  saveKycDocument: (doc) => {
    localStorage.setItem('db_kyc_document', JSON.stringify(doc));
  },

  getTheme: () => localStorage.getItem('mortext_theme') || 'light',
  saveTheme: (theme) => localStorage.setItem('mortext_theme', theme),

  getLanguage: () => localStorage.getItem('mortext_language') || 'en',
  saveLanguage: (lang) => localStorage.setItem('mortext_language', lang),
};
