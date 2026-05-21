import React, { useState } from 'react';
import { AuthProvider, useAuth } from './hooks/useAuth';
import { ToastProvider } from './components/UI/Toast';

// Layout & Navigation Imports
import MainLayout from './components/Layout/MainLayout';

// Screen Page Imports
import AuthPage from './pages/Auth/AuthPage';
import Dashboard from './pages/Dashboard/Dashboard';
import Accounts from './pages/Accounts/Accounts';
import Transfer from './pages/Transfer/Transfer';
import History from './pages/History/History';
import Cards from './pages/Cards/Cards';
import Profile from './pages/Profile/Profile';
import AdminPanel from './pages/Admin/AdminPanel';

import './App.css';

// Central Core Selector that determines which tab should render in view
const BankAppCore = () => {
  const { user } = useAuth();
  const [activeTab, setActiveTab] = useState('dashboard');

  // If no active user session, redirect to the secure authentication gate screen
  if (!user) {
    return <AuthPage />;
  }

  // Safe fallback to prevent normal users from accessing Admin Console pages
  const renderActiveScreen = () => {
    switch (activeTab) {
      case 'accounts':
        return <Accounts />;
      case 'transfer':
        return <Transfer />;
      case 'history':
        return <History />;
      case 'cards':
        return <Cards />;
      case 'profile':
        return <Profile />;
      case 'admin':
        return user.role === 'Admin' ? <AdminPanel /> : <Dashboard setActiveTab={setActiveTab} />;
      case 'dashboard':
      default:
        return <Dashboard setActiveTab={setActiveTab} />;
    }
  };

  return (
    <MainLayout activeTab={activeTab} setActiveTab={setActiveTab}>
      {renderActiveScreen()}
    </MainLayout>
  );
};

function App() {
  return (
    <ToastProvider>
      <AuthProvider>
        <BankAppCore />
      </AuthProvider>
    </ToastProvider>
  );
}

export default App;
