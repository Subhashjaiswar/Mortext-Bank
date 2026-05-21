import React, { useState } from 'react';
import Sidebar from './Sidebar';
import TopNavbar from './TopNavbar';
import './MainLayout.css';

const MainLayout = ({ children, activeTab, setActiveTab }) => {
  const [collapsed, setCollapsed] = useState(false);

  // Return user-friendly headers based on the current page activeTab
  const getHeaderTitle = () => {
    switch (activeTab) {
      case 'accounts':
        return 'Accounts Management';
      case 'transfer':
        return 'Money Transfers';
      case 'history':
        return 'Transaction Ledger';
      case 'cards':
        return 'Card Dashboard';
      case 'profile':
        return 'Profile & Security';
      case 'admin':
        return 'System Administration';
      case 'dashboard':
      default:
        return 'Dashboard Overview';
    }
  };

  return (
    <div className={`app-layout-wrapper ${collapsed ? 'sidebar-collapsed' : ''}`}>
      {/* Navigation Sidebar */}
      <Sidebar
        activeTab={activeTab}
        setActiveTab={setActiveTab}
        collapsed={collapsed}
        setCollapsed={setCollapsed}
      />

      {/* Main Content Area */}
      <div className="main-content-wrapper">
        <TopNavbar title={getHeaderTitle()} />
        <main className="content-viewport animate-fade-in">
          {children}
        </main>
      </div>
    </div>
  );
};

export default MainLayout;
