import { useState, useEffect } from 'react';
import { useAuth } from '../hooks/useAuth';
import { useToast } from '../components/UI/Toast';
import { adminService } from '../services/adminService';

export const useAdminViewModel = () => {
  const { adminUsers: globalAdminUsers, transactions: globalTransactions, triggerGlobalRefresh } = useAuth();
  const toast = useToast();

  const [adminUsers, setAdminUsers] = useState(globalAdminUsers);
  const [searchTerm, setSearchTerm] = useState('');
  const [kycFilter, setKycFilter] = useState('All');
  const [analytics, setAnalytics] = useState(null);

  const fetchAdminData = async () => {
    try {
      const data = await adminService.getUsers(1, 100);
      setAdminUsers(data.users || globalAdminUsers);
      
      const analyticData = await adminService.getAnalytics();
      setAnalytics(analyticData);
    } catch (err) {
      console.error('Failed to load admin data via adminService', err);
    }
  };

  useEffect(() => {
    fetchAdminData();
  }, [globalAdminUsers]);

  const filteredUsers = adminUsers.filter((u) => {
    const matchesSearch =
      u.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
      u.email.toLowerCase().includes(searchTerm.toLowerCase());
    const matchesKyc = kycFilter === 'All' || u.kycStatus === kycFilter;
    return matchesSearch && matchesKyc;
  });

  const handleApproveKyc = async (userId, userName) => {
    try {
      await adminService.approveKyc(userId);
      toast.success(`Identity document for ${userName} has been Approved.`);
      fetchAdminData();
      if (triggerGlobalRefresh) triggerGlobalRefresh();
    } catch (err) {
      toast.error('Failed to approve KYC.');
    }
  };

  const handleRejectKyc = async (userId, userName) => {
    try {
      await adminService.rejectKyc(userId, 'Incomplete / Unreadable Document');
      toast.error(`Identity verification for ${userName} was rejected.`);
      fetchAdminData();
      if (triggerGlobalRefresh) triggerGlobalRefresh();
    } catch (err) {
      toast.error('Failed to reject KYC.');
    }
  };

  const handleToggleUserStatus = async (userId, userName) => {
    try {
      const res = await adminService.toggleUserStatus(userId);
      toast.warning(`Account lock state for ${userName} toggled to ${res.status}.`);
      fetchAdminData();
      if (triggerGlobalRefresh) triggerGlobalRefresh();
    } catch (err) {
      toast.error('Failed to toggle status.');
    }
  };

  const formatCurrency = (val) => {
    return new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(val);
  };

  // Aggregated System Metrics
  const systemTotalUsers = adminUsers.length;
  const verifiedUsersCount = adminUsers.filter((u) => u.kycStatus === 'Approved').length;
  const verifiedRate = systemTotalUsers > 0 ? ((verifiedUsersCount / systemTotalUsers) * 100).toFixed(0) : 0;
  const systemTotalTransactionsVolume = globalTransactions.reduce((sum, t) => sum + t.amount, 0);

  return {
    adminUsers,
    searchTerm,
    setSearchTerm,
    kycFilter,
    setKycFilter,
    filteredUsers,
    handleApproveKyc,
    handleRejectKyc,
    handleToggleUserStatus,
    formatCurrency,
    systemTotalUsers,
    verifiedUsersCount,
    verifiedRate,
    systemTotalTransactionsVolume,
    analytics,
  };
};
