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
  ShieldAlert,
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
import Modal from '../UI/Modal';
import Button from '../UI/Button';
import Input from '../UI/Input';
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
  const [isIpModalOpen, setIsIpModalOpen] = useState(false);
  const [modalIpAddress, setModalIpAddress] = useState('');
  const [modalIpDescription, setModalIpDescription] = useState('');
  const [isDetectingIp, setIsDetectingIp] = useState(false);
  const [modalIpType, setModalIpType] = useState(null);
  const [isPromptModalOpen, setIsPromptModalOpen] = useState(false);
  const [isAccessBlocked, setIsAccessBlocked] = useState(false);
  const [detectedNetworkIp, setDetectedNetworkIp] = useState('');
 
  const checkIpType = (value) => {
    if (!value) return null;
    const ipv4Regex = /^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$/;
    const ipv6Regex = /^(([0-9a-fA-F]{1,4}:){7,7}[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,7}:|([0-9a-fA-F]{1,4}:){1,6}:[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,5}(:[0-9a-fA-F]{1,4}){1,2}|([0-9a-fA-F]{1,4}:){1,4}(:[0-9a-fA-F]{1,4}){1,3}|([0-9a-fA-F]{1,4}:){1,3}(:[0-9a-fA-F]{1,4}){1,4}|([0-9a-fA-F]{1,4}:){1,2}(:[0-9a-fA-F]{1,4}){1,5}|[0-9a-fA-F]{1,4}:((:[0-9a-fA-F]{1,4}){1,6})|:((:[0-9a-fA-F]{1,4}){1,7}|:)|fe80:(:[0-9a-fA-F]{0,4}){0,4}%[0-9a-zA-Z]{1,}|::(ffff(:0{1,4}){0,1}:){0,1}((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\.){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])|([0-9a-fA-F]{1,4}:){1,4}:((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\.){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9]))$/;
 
    if (ipv4Regex.test(value)) return 'IPv4';
    if (ipv6Regex.test(value)) return 'IPv6';
    return 'invalid';
  };
 
  const fetchActiveIps = async () => {
    try {
      const data = await adminService.getActiveIpWhitelist();
      setWhitelistedIps(data || []);
      return data || [];
    } catch (err) {
      console.error('Failed to load whitelisted IPs', err);
      return [];
    }
  };
 
  useEffect(() => {
    const initIpCheck = async () => {
      const activeIps = await fetchActiveIps();
      try {
        const response = await fetch('https://api64.ipify.org?format=json');
        if (!response.ok) throw new Error();
        const data = await response.json();
        const currentNetworkIp = data.ip;
        setDetectedNetworkIp(currentNetworkIp);
 
        const isWhitelisted = activeIps.some(item => item.ipAddress === currentNetworkIp);
        if (!isWhitelisted) {
          if (localStorage.getItem('applied_client_ip') === currentNetworkIp) {
            localStorage.removeItem('applied_client_ip');
            setAppliedIp('');
          }
          setModalIpAddress(currentNetworkIp);
          setModalIpType(checkIpType(currentNetworkIp));
          setModalIpDescription('My Current Network');
          setIsPromptModalOpen(true);
        } else {
          setIsAccessBlocked(false);
        }
      } catch (err) {
        console.warn('Could not auto-detect network IP on load:', err);
      }
    };
    initIpCheck();
  }, []);

  const validateIP = (value) => {
    setIpType(checkIpType(value));
  };

  const handleOpenModal = () => {
    setModalIpAddress('');
    setModalIpDescription('');
    setModalIpType(null);
    setIsIpModalOpen(true);
    detectNetworkIp();
  };

  const detectNetworkIp = async () => {
    setIsDetectingIp(true);
    try {
      const response = await fetch('https://api64.ipify.org?format=json');
      if (!response.ok) throw new Error('Network error');
      const data = await response.json();
      setModalIpAddress(data.ip);
      setModalIpType(checkIpType(data.ip));
    } catch (err) {
      console.warn('Could not auto-detect network IP, using local fallback:', err);
      const mockIp = '49.43.2.248';
      setModalIpAddress(mockIp);
      setModalIpType(checkIpType(mockIp));
      toast.warning('Auto-detection failed. Using local fallback network IP.');
    } finally {
      setIsDetectingIp(false);
    }
  };

  const handleAddModalIp = async () => {
    if (modalIpType === 'IPv4' || modalIpType === 'IPv6') {
      if (whitelistedIps.some(item => item.ipAddress === modalIpAddress)) {
        toast.error('This IP address is already saved.');
        return;
      }

      try {
        await adminService.addIpToWhitelist(modalIpAddress, modalIpDescription || 'Network IP');
        toast.success(`IP ${modalIpAddress} whitelisted successfully!`);

        const localSaved = JSON.parse(localStorage.getItem('mortext_saved_ips') || '[]');
        localSaved.push({ ip: modalIpAddress, type: modalIpType, addedAt: new Date().toLocaleDateString(), description: modalIpDescription || 'Network IP' });
        localStorage.setItem('mortext_saved_ips', JSON.stringify(localSaved));

        setIsIpModalOpen(false);
        if (modalIpAddress === detectedNetworkIp) {
          setIsAccessBlocked(false);
        }
        fetchActiveIps();
      } catch (err) {
        console.error(err);
        toast.error(err.message || 'Failed to add IP address to backend whitelist.');
      }
    }
  };

  const handleAddPromptIp = async () => {
    try {
      await adminService.addIpToWhitelist(modalIpAddress, modalIpDescription || 'My Current Network');
      toast.success(`IP ${modalIpAddress} whitelisted successfully!`);

      const localSaved = JSON.parse(localStorage.getItem('mortext_saved_ips') || '[]');
      localSaved.push({ ip: modalIpAddress, type: modalIpType || 'IPv4', addedAt: new Date().toLocaleDateString(), description: modalIpDescription || 'My Current Network' });
      localStorage.setItem('mortext_saved_ips', JSON.stringify(localSaved));

      setIsPromptModalOpen(false);
      setIsAccessBlocked(false);
      fetchActiveIps();
    } catch (err) {
      console.error(err);
      toast.error(err.message || 'Failed to add IP address to backend whitelist.');
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

  const handleApplyIp = async (ipToApply) => {
    try {
      localStorage.setItem('applied_client_ip', ipToApply);
      setAppliedIp(ipToApply);
      await adminService.applyIpWhitelist();
      toast.success(`Client IP ${ipToApply} applied to request headers.`);
    } catch (err) {
      console.error(err);
      toast.error(err.message || 'Failed to apply IP whitelist changes on the server.');
      // Revert if API call fails
      localStorage.removeItem('applied_client_ip');
      setAppliedIp('');
    }
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
    if (!window.confirm("Do you want to delete IP Address")) {
      return;
    }
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
            <button 
              type="button"
              className="ip-validator-add-btn animate-scale-in" 
              onClick={handleOpenModal}
              style={{ marginTop: '2px', marginBottom: '4px' }}
            >
              <Globe size={14} />
              <span>Get Network IP</span>
            </button>
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

      <Modal
        isOpen={isIpModalOpen}
        onClose={() => setIsIpModalOpen(false)}
        title="Add IP from Network"
        size="md"
        footer={
          <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '12px', width: '100%' }}>
            <Button variant="secondary" onClick={() => setIsIpModalOpen(false)}>
              Cancel
            </Button>
            <Button 
              variant="primary" 
              onClick={handleAddModalIp} 
              disabled={isDetectingIp || !modalIpAddress || modalIpType === 'invalid'}
            >
              Add IP
            </Button>
          </div>
        }
      >
        <div style={{ display: 'flex', flexDirection: 'column', gap: '16px', padding: '8px 0' }}>
          {isDetectingIp ? (
            <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', padding: '24px 0', gap: '12px' }}>
              <span className="btn-spinner" style={{ display: 'inline-block', margin: '0 auto', width: '28px', height: '28px' }}></span>
              <span style={{ fontSize: '0.9rem', color: 'var(--text-secondary)' }}>Detecting public network IP...</span>
            </div>
          ) : (
            <>
              <div style={{ display: 'flex', flexDirection: 'column', gap: '6px' }}>
                <label style={{ fontSize: '0.85rem', fontWeight: '600', color: 'var(--text-secondary)' }}>Detected IP Address</label>
                <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                  <Input
                    placeholder="Auto-detected IP"
                    value={modalIpAddress}
                    onChange={(e) => {
                      setModalIpAddress(e.target.value);
                      setModalIpType(checkIpType(e.target.value));
                    }}
                    style={{ flex: 1, marginBottom: 0 }}
                  />
                  <Button variant="outline" size="md" onClick={detectNetworkIp} title="Detect Again">
                    <Globe size={16} />
                  </Button>
                </div>
                {modalIpType && (
                  <div style={{ 
                    display: 'flex', 
                    alignItems: 'center', 
                    gap: '6px', 
                    fontSize: '0.75rem', 
                    fontWeight: '600', 
                    color: modalIpType === 'invalid' ? 'var(--danger)' : 'var(--success)', 
                    marginTop: '4px' 
                  }}>
                    {modalIpType === 'invalid' ? (
                      <>
                        <AlertCircle size={14} />
                        <span>Invalid IP Address</span>
                      </>
                    ) : (
                      <>
                        <CheckCircle2 size={14} />
                        <span>Valid {modalIpType} Address</span>
                      </>
                    )}
                  </div>
                )}
              </div>

              <div style={{ display: 'flex', flexDirection: 'column', gap: '6px' }}>
                <label style={{ fontSize: '0.85rem', fontWeight: '600', color: 'var(--text-secondary)' }}>Description</label>
                <Input
                  placeholder="e.g. Office Connection, Home Wi-Fi"
                  value={modalIpDescription}
                  onChange={(e) => setModalIpDescription(e.target.value)}
                />
              </div>
            </>
          )}
        </div>
      </Modal>

      <Modal
        isOpen={isPromptModalOpen}
        onClose={() => {
          setIsPromptModalOpen(false);
          setIsAccessBlocked(true);
        }}
        closeOnOverlayClick={false}
        showCloseButton={false}
        title="Unlisted Network Detected"
        size="md"
        footer={
          <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '12px', width: '100%' }}>
            <Button variant="secondary" onClick={() => {
              setIsPromptModalOpen(false);
              setIsAccessBlocked(true);
            }}>
              No, Cancel
            </Button>
            <Button 
              variant="primary" 
              onClick={handleAddPromptIp}
            >
              Yes, Add IP
            </Button>
          </div>
        }
      >
        <div style={{ display: 'flex', flexDirection: 'column', gap: '16px', padding: '8px 0' }}>
          <p style={{ fontSize: '0.95rem', color: 'var(--text-secondary)', lineHeight: '1.5' }}>
            We detected that your current public network IP address <strong>{modalIpAddress}</strong> is not whitelisted in our database.
          </p>
          <p style={{ fontSize: '0.9rem', color: 'var(--text-muted)' }}>
            Do you want to add this IP address to our database whitelist?
          </p>
          
          <div style={{ display: 'flex', flexDirection: 'column', gap: '6px' }}>
            <label style={{ fontSize: '0.85rem', fontWeight: '600', color: 'var(--text-secondary)' }}>Description</label>
            <Input
              placeholder="e.g. My Current Wifi Network"
              value={modalIpDescription}
              onChange={(e) => setModalIpDescription(e.target.value)}
            />
          </div>
        </div>
      </Modal>

      {isAccessBlocked && (
        <div className="access-blocked-overlay" style={{
          position: 'fixed',
          top: 0,
          left: 0,
          width: '100vw',
          height: '100vh',
          backgroundColor: 'rgba(15, 23, 42, 0.98)',
          zIndex: 9999,
          display: 'flex',
          flexDirection: 'column',
          alignItems: 'center',
          justifyContent: 'center',
          color: '#fff',
          padding: '24px',
          textAlign: 'center'
        }}>
          <ShieldAlert size={64} style={{ color: 'var(--danger)', marginBottom: '16px' }} />
          <h1 style={{ fontSize: '2rem', fontWeight: '800', marginBottom: '8px', color: '#fff' }}>Access Blocked</h1>
          <p style={{ fontSize: '1rem', color: 'rgba(255, 255, 255, 0.7)', maxWidth: '480px', marginBottom: '24px', lineHeight: '1.6' }}>
            Your current network IP address ({detectedNetworkIp}) is not whitelisted. Access to this application has been restricted.
          </p>
          <Button variant="primary" onClick={() => {
            setModalIpAddress(detectedNetworkIp);
            setModalIpType(checkIpType(detectedNetworkIp));
            setModalIpDescription('My Current Network');
            setIsPromptModalOpen(true);
          }}>
            Whitelist IP Address
          </Button>
        </div>
      )}
    </aside>
  );
};

export default Sidebar;
