import React from 'react';
import {
  LayoutDashboard,
  Wallet,
  ArrowLeftRight,
  History,
  CreditCard,
  User,
  Settings,
  ShieldCheck,
  LogOut,
  ChevronLeft,
  ChevronRight,
  TrendingUp
} from 'lucide-react';
import { useAuth } from '../../hooks/useAuth';
import './Sidebar.css';

const Sidebar = ({ activeTab, setActiveTab, collapsed, setCollapsed }) => {
  const { user, logout } = useAuth();

  const menuItems = [
    { id: 'dashboard', label: 'Dashboard', icon: LayoutDashboard },
    { id: 'accounts', label: 'Accounts', icon: Wallet },
    { id: 'transfer', label: 'Money Transfer', icon: ArrowLeftRight },
    { id: 'history', label: 'Transactions', icon: History },
    { id: 'cards', label: 'Cards', icon: CreditCard },
    { id: 'profile', label: 'Profile & Settings', icon: User },
  ];

  // Admin menu item
  if (user?.role === 'Admin') {
    menuItems.push({ id: 'admin', label: 'Admin Panel', icon: ShieldCheck });
  }

  return (
    <aside className={`sidebar-container ${collapsed ? 'collapsed' : ''}`}>
      {/* Sidebar Header */}
      <div className="sidebar-header">
        {!collapsed ? (
          <div className="logo-brand animate-fade-in">
            <TrendingUp className="logo-icon" size={28} />
            <span className="logo-text">Mortext<span className="logo-subtext">Bank</span></span>
          </div>
        ) : (
          <div className="logo-brand collapsed animate-scale-in">
            <TrendingUp className="logo-icon" size={28} />
          </div>
        )}
        <button
          className="collapse-toggle-btn"
          onClick={() => setCollapsed(!collapsed)}
          aria-label="Toggle Sidebar"
        >
          {collapsed ? <ChevronRight size={18} /> : <ChevronLeft size={18} />}
        </button>
      </div>

      {/* Sidebar Menu Items */}
      <nav className="sidebar-menu">
        <ul className="menu-list">
          {menuItems.map((item) => {
            const Icon = item.icon;
            const isActive = activeTab === item.id;
            return (
              <li key={item.id} className="menu-item-wrapper">
                <button
                  className={`menu-item-btn ${isActive ? 'active' : ''}`}
                  onClick={() => setActiveTab(item.id)}
                  title={collapsed ? item.label : undefined}
                >
                  <Icon size={20} className="menu-icon" />
                  {!collapsed && <span className="menu-label">{item.label}</span>}
                  {isActive && !collapsed && <span className="active-indicator"></span>}
                </button>
              </li>
            );
          })}
        </ul>
      </nav>

      {/* Sidebar Footer */}
      <div className="sidebar-footer">
        {!collapsed ? (
          <div className="user-profile-summary animate-fade-in">
            <img
              src={user?.avatar || 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&q=80&w=120'}
              alt="Profile avatar"
              className="user-avatar"
            />
            <div className="user-info">
              <span className="user-name">{user?.name || 'Alex Carter'}</span>
              <span className="user-role">{user?.role || 'Admin'}</span>
            </div>
            <button className="logout-btn" onClick={logout} title="Sign Out">
              <LogOut size={18} />
            </button>
          </div>
        ) : (
          <div className="user-profile-summary collapsed">
            <button className="logout-btn collapsed" onClick={logout} title="Sign Out">
              <LogOut size={20} />
            </button>
          </div>
        )}
      </div>
    </aside>
  );
};

export default Sidebar;
