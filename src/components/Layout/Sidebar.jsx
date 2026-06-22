import React, { useState, useEffect } from 'react';
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
  TrendingUp,
  Globe,
  CheckCircle2,
  AlertCircle,
  Check,
  X,
  Plus,
  Trash2
} from 'lucide-react';
import { useAuth } from '../../hooks/useAuth';
import { useToast } from '../UI/Toast';
import { adminService } from '../../services/adminService';
import './Sidebar.css';

const Sidebar = ({ activeTab, setActiveTab, collapsed, setCollapsed }) => {
  const { user, logout } = useAuth();
  const toast = useToast();
  const [ipAddress, setIpAddress] = useState('');
  const [ipDescription, setIpDescription] = useState('');
  const [ipType, setIpType] = useState(null); // 'IPv4' | 'IPv6' | 'invalid' | null
  const [whitelistedIps, setWhitelistedIps] = useState([]);
  const [appliedIp, setAppliedIp] = useState(() => {
    return localStorage.getItem('applied_client_ip') || '';
  });

  const fetchActiveIps = async () => {
    try {
      const data = await adminService.getActiveIpWhitelist();
      setWhitelistedIps(data || []);
    } catch (err) {
      console.error('Failed to load whitelisted IPs', err);
    }
  };

  useEffect(() => {
    fetchActiveIps();
  }, []);

  const validateIP = (value) => {
    if (!value) {
      setIpType(null);
      return;
    }
    const ipv4Regex = /^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$/;
    const ipv6Regex = /^(([0-9a-fA-F]{1,4}:){7,7}[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,7}:|([0-9a-fA-F]{1,4}:){1,6}:[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,5}(:[0-9a-fA-F]{1,4}){1,2}|([0-9a-fA-F]{1,4}:){1,4}(:[0-9a-fA-F]{1,4}){1,3}|([0-9a-fA-F]{1,4}:){1,3}(:[0-9a-fA-F]{1,4}){1,4}|([0-9a-fA-F]{1,4}:){1,2}(:[0-9a-fA-F]{1,4}){1,5}|[0-9a-fA-F]{1,4}:((:[0-9a-fA-F]{1,4}){1,6})|:((:[0-9a-fA-F]{1,4}){1,7}|:)|fe80:(:[0-9a-fA-F]{0,4}){0,4}%[0-9a-zA-Z]{1,}|::(ffff(:0{1,4}){0,1}:){0,1}((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\.){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])|([0-9a-fA-F]{1,4}:){1,4}:((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\.){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9]))$/;

    if (ipv4Regex.test(value)) {
      setIpType('IPv4');
    } else if (ipv6Regex.test(value)) {
      setIpType('IPv6');
    } else {
      setIpType('invalid');
    }
  };

  const handleIpChange = (e) => {
    const value = e.target.value.trim();
    setIpAddress(value);
    validateIP(value);
  };

  const handleClearIp = () => {
    setIpAddress('');
    setIpDescription('');
    setIpType(null);
  };

  const handleApplyIp = (ipToApply) => {
    localStorage.setItem('applied_client_ip', ipToApply);
    setAppliedIp(ipToApply);
    toast.success(`Client IP ${ipToApply} applied to request headers.`);
  };

  const handleUnapplyIp = () => {
    localStorage.removeItem('applied_client_ip');
    setAppliedIp('');
    toast.warning('Client IP unapplied. Reverted to real IP.');
  };

  const handleAddIp = async () => {
    if (ipType === 'IPv4' || ipType === 'IPv6') {
      if (whitelistedIps.some(item => item.ipAddress === ipAddress)) {
        toast.error('This IP address is already saved.');
        return;
      }

      try {
        await adminService.addIpToWhitelist(ipAddress, ipDescription || 'Office network');
        toast.success(`IP ${ipAddress} whitelisted successfully!`);

        const localSaved = JSON.parse(localStorage.getItem('mortext_saved_ips') || '[]');
        localSaved.push({ ip: ipAddress, type: ipType, addedAt: new Date().toLocaleDateString(), description: ipDescription });
        localStorage.setItem('mortext_saved_ips', JSON.stringify(localSaved));

        setIpAddress('');
        setIpDescription('');
        setIpType(null);
        fetchActiveIps();
      } catch (err) {
        console.error(err);
        toast.error(err.message || 'Failed to add IP address to backend whitelist.');
      }
    }
  };

  const handleDeleteIp = async (id, ipToDelete) => {
    try {
      await adminService.removeIpFromWhitelist(id);
      toast.success('IP address removed.');

      const localSaved = JSON.parse(localStorage.getItem('mortext_saved_ips') || '[]');
      const filtered = localSaved.filter(item => item.ip !== ipToDelete);
      localStorage.setItem('mortext_saved_ips', JSON.stringify(filtered));

      if (localStorage.getItem('applied_client_ip') === ipToDelete) {
        localStorage.removeItem('applied_client_ip');
        setAppliedIp('');
      }

      fetchActiveIps();
    } catch (err) {
      console.error(err);
      toast.error('Failed to remove IP from whitelist.');
    }
  };

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
        {!collapsed && (
          <div className="ip-validator-container animate-fade-in">
            <div className="ip-validator-title">
              <Globe size={14} className="logo-icon" />
              <span>IP Validator</span>
            </div>
            <div className="ip-validator-input-wrapper">
              <input
                type="text"
                className="ip-validator-input"
                placeholder="Enter IP address..."
                value={ipAddress}
                onChange={handleIpChange}
              />
              {ipAddress && (
                <button className="ip-validator-clear-btn" onClick={handleClearIp} title="Clear">
                  <X size={14} />
                </button>
              )}
            </div>
            <div className={`ip-validator-status ${ipType ? 'visible' : ''} ${ipType === 'invalid' ? 'invalid' : ipType ? 'valid' : ''}`}>
              {ipType === 'IPv4' && (
                <>
                  <CheckCircle2 size={14} />
                  <span>Valid IPv4 Address</span>
                </>
              )}
              {ipType === 'IPv6' && (
                <>
                  <CheckCircle2 size={14} />
                  <span>Valid IPv6 Address</span>
                </>
              )}
              {ipType === 'invalid' && (
                <>
                  <AlertCircle size={14} />
                  <span>Invalid IP Address</span>
                </>
              )}
            </div>
            {(ipType === 'IPv4' || ipType === 'IPv6') && (
              <>
                <div className="ip-validator-input-wrapper animate-fade-in">
                  <input
                    type="text"
                    className="ip-validator-input"
                    placeholder="Enter description..."
                    value={ipDescription}
                    onChange={(e) => setIpDescription(e.target.value)}
                  />
                </div>
                <button className="ip-validator-add-btn animate-scale-in" onClick={handleAddIp}>
                  <Plus size={14} />
                  <span>Add IP</span>
                </button>
              </>
            )}
            {whitelistedIps.length > 0 && (
              <div className="saved-ips-section">
                <div className="saved-ips-header">Whitelisted IPs ({whitelistedIps.length})</div>
                <ul className="saved-ips-list">
                  {whitelistedIps.map((item) => {
                    const isApplied = appliedIp === item.ipAddress;
                    return (
                      <li key={item.id} className="saved-ip-item animate-fade-in" style={{ padding: '8px 12px', borderLeft: isApplied ? '3px solid var(--success)' : undefined }}>
                        <div className="saved-ip-info" style={{ display: 'flex', flexDirection: 'column', alignItems: 'flex-start', gap: '2px', minWidth: 0, flex: 1 }}>
                          <div style={{ display: 'flex', alignItems: 'center', gap: '6px', width: '100%' }}>
                            <span className="saved-ip-text" title={item.ipAddress} style={{ fontSize: '0.75rem', fontWeight: 'bold', color: 'var(--text-primary)', whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }}>
                              {item.ipAddress}
                            </span>
                            {isApplied && (
                              <span style={{ fontSize: '0.6rem', color: 'var(--success)', fontWeight: 'bold', background: 'var(--success-light)', padding: '1px 4px', borderRadius: '4px', textTransform: 'uppercase' }}>
                                Active
                              </span>
                            )}
                          </div>
                          <span className="saved-ip-desc" title={item.description} style={{ fontSize: '0.675rem', color: 'var(--text-muted)', whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis', width: '100%' }}>
                            {item.description}
                          </span>
                        </div>
                        <div style={{ display: 'flex', alignItems: 'center', gap: '4px', marginLeft: '6px', flexShrink: 0 }}>
                          {isApplied ? (
                            <button className="saved-ip-delete-btn" onClick={handleUnapplyIp} title="Unapply IP" style={{ color: 'var(--success)' }}>
                              <Check size={12} />
                            </button>
                          ) : (
                            <button className="saved-ip-delete-btn" onClick={() => handleApplyIp(item.ipAddress)} title="Apply IP">
                              <Globe size={12} />
                            </button>
                          )}
                          <button className="saved-ip-delete-btn" onClick={() => handleDeleteIp(item.id, item.ipAddress)} title="Remove IP">
                            <Trash2 size={12} />
                          </button>
                        </div>
                      </li>
                    );
                  })}
                </ul>
              </div>
            )}
          </div>
        )}
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
