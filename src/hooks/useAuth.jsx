import React, { createContext, useContext, useState, useEffect } from 'react';
import { mockDb } from '../services/mockDb';
import { accountService } from '../services/accountService';
import { transferService } from '../services/transferService';
import { cardService } from '../services/cardService';
import { transactionService } from '../services/transactionService';
import { apiClient } from '../services/apiClient';

const AuthContext = createContext(null);

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [theme, setTheme] = useState('light');
  const [language, setLanguage] = useState('en');

  // Shared application states loaded dynamically from mockDb
  const [accounts, setAccounts] = useState([]);
  const [beneficiaries, setBeneficiaries] = useState([]);
  const [cards, setCards] = useState([]);
  const [transactions, setTransactions] = useState([]);
  const [notifications, setNotifications] = useState([]);
  const [adminUsers, setAdminUsers] = useState([]);
  const [kycStatus, setKycStatus] = useState('Pending');
  const [kycDocument, setKycDocument] = useState(null);

  // Sync state with mockDb or live APIs
  const syncWithDb = async () => {
    if (apiClient.isMock()) {
      setAccounts(mockDb.getAccounts());
      setBeneficiaries(mockDb.getBeneficiaries());
      setCards(mockDb.getCards());
      setTransactions(mockDb.getTransactions());
      setNotifications(mockDb.getNotifications());
      setAdminUsers(mockDb.getAdminUsers());
      setKycStatus(mockDb.getKycStatus());
      setKycDocument(mockDb.getKycDocument());
    } else {
      // Pre-fetching disabled. Pages will fetch their own data.
    }
  };

  // On mount: sync data and session
  useEffect(() => {
    syncWithDb();

    const cachedUser = localStorage.getItem('mortext_user');
    if (cachedUser) {
      setUser(JSON.parse(cachedUser));
    }
    const cachedTheme = localStorage.getItem('mortext_theme');
    if (cachedTheme) {
      setTheme(cachedTheme);
      document.body.className = cachedTheme === 'dark' ? 'dark-theme' : '';
    } else {
      const dbTheme = mockDb.getTheme();
      setTheme(dbTheme);
      document.body.className = dbTheme === 'dark' ? 'dark-theme' : '';
    }

    const cachedLang = mockDb.getLanguage();
    setLanguage(cachedLang);
  }, []);

  // Theme Toggler
  const toggleTheme = () => {
    const nextTheme = theme === 'light' ? 'dark' : 'light';
    setTheme(nextTheme);
    mockDb.saveTheme(nextTheme);
    document.body.className = nextTheme === 'dark' ? 'dark-theme' : '';
  };

  // Locale Language setter
  const updateLanguage = (lang) => {
    setLanguage(lang);
    mockDb.saveLanguage(lang);
  };

  // Legacy Operations mapped for backward compatibility
  const login = (userData) => {
    setUser(userData);
    syncWithDb();
  };

  const register = (userData) => {
    setUser(userData);
    syncWithDb();
  };

  const logout = () => {
    setUser(null);
    localStorage.removeItem('mortext_user');
    localStorage.removeItem('mortext_token');
  };

  // Aggregated total balances
  const totalBalance = accounts.reduce((sum, a) => sum + a.balance, 0);

  return (
    <AuthContext.Provider
      value={{
        user,
        theme,
        language,
        notifications,
        accounts,
        beneficiaries,
        cards,
        transactions,
        kycStatus,
        kycDocument,
        adminUsers,
        totalBalance,
        toggleTheme,
        setLanguage: updateLanguage,
        login,
        register,
        logout,
        triggerGlobalRefresh: syncWithDb
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};
