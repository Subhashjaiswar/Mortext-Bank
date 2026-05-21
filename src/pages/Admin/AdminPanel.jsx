import React from 'react';
import { ShieldCheck, UserCheck, Activity, Users, Search, Ban, Check, X, AlertTriangle } from 'lucide-react';
import { useAdminViewModel } from '../../viewmodels/useAdminViewModel';
import Card from '../../components/UI/Card';
import Button from '../../components/UI/Button';
import Badge from '../../components/UI/Badge';
import './AdminPanel.css';

const AdminPanel = () => {
  const {
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
  } = useAdminViewModel();

  return (
    <div className="admin-page-container">
      {/* 1. Admin System Aggregation Cards */}
      <div className="admin-stats-grid">
        <Card className="admin-stat-card">
          <div className="stat-icon-wrapper blue">
            <Users size={24} />
          </div>
          <div className="stat-content">
            <span className="stat-lbl text-muted">System Registrations</span>
            <h3>{systemTotalUsers} Active Accounts</h3>
          </div>
        </Card>

        <Card className="admin-stat-card">
          <div className="stat-icon-wrapper green">
            <ShieldCheck size={24} />
          </div>
          <div className="stat-content">
            <span className="stat-lbl text-muted">KYC Verified Rate</span>
            <h3>{verifiedRate}% verified</h3>
          </div>
        </Card>

        <Card className="admin-stat-card">
          <div className="stat-icon-wrapper accent">
            <Activity size={24} />
          </div>
          <div className="stat-content">
            <span className="stat-lbl text-muted">Gross Transaction Volume</span>
            <h3>{formatCurrency(systemTotalTransactionsVolume)}</h3>
          </div>
        </Card>
      </div>

      {/* 2. Split Rows: KYC approvals and fraud surveillance */}
      <div className="admin-mid-split-grid">
        {/* KYC Approval Action Centre */}
        <Card
          title="KYC Approvals Desk"
          subtitle="Real-time pending document verification queue"
          className="admin-kyc-desk-card"
        >
          <div className="kyc-approval-list">
            {adminUsers.filter((u) => u.kycStatus === 'Uploaded' || u.kycStatus === 'Pending').length > 0 ? (
              adminUsers
                .filter((u) => u.kycStatus === 'Uploaded' || u.kycStatus === 'Pending')
                .map((u) => (
                  <div key={u.id} className="kyc-pending-row animate-scale-in">
                    <div className="kyc-user-meta">
                      <span className="u-name-lbl">{u.name}</span>
                      <span className="u-email-lbl">{u.email}</span>
                      <Badge variant={u.kycStatus === 'Uploaded' ? 'info' : 'secondary'} className="mt-4">
                        {u.kycStatus === 'Uploaded' ? 'DOCUMENTS RECEIVED' : 'PENDING UPLOAD'}
                      </Badge>
                    </div>
                    {u.kycStatus === 'Uploaded' ? (
                      <div className="kyc-action-buttons">
                        <Button
                          variant="success"
                          size="sm"
                          onClick={() => handleApproveKyc(u.id, u.name)}
                          icon={Check}
                          title="Approve verification"
                        >
                          Approve
                        </Button>
                        <Button
                          variant="danger"
                          size="sm"
                          onClick={() => handleRejectKyc(u.id, u.name)}
                          icon={X}
                          title="Reject verification"
                        >
                          Reject
                        </Button>
                      </div>
                    ) : (
                      <span className="awaiting-upload-txt">Awaiting ID Upload</span>
                    )}
                  </div>
                ))
            ) : (
              <div className="kyc-desk-empty">
                <ShieldCheck size={40} className="shield-ok-svg" />
                <p>Verify Queue is empty! No pending documents require audit approvals currently.</p>
              </div>
            )}
          </div>
        </Card>

        {/* Live Fraud Detection alerts */}
        <Card title="Surveillance & Fraud Alerts" subtitle="Intelligent neural active monitoring metrics">
          <div className="fraud-detection-list">
            <div className="fraud-alert-item critical">
              <AlertTriangle size={20} className="alert-critical-icon" />
              <div className="alert-details">
                <h5>Suspicious Velocity Limit</h5>
                <p>Account •••• 9210 initiated 3 transfers in under 45 seconds.</p>
                <span>Timestamp: 10 mins ago • Severity: High</span>
              </div>
            </div>

            <div className="fraud-alert-item warning">
              <AlertTriangle size={20} className="alert-warning-icon" />
              <div className="alert-details">
                <h5>Foreign IP Sign In</h5>
                <p>User Sarah Connor logged in via proxy server IP in Berlin.</p>
                <span>Timestamp: 2 hours ago • Severity: Medium</span>
              </div>
            </div>
          </div>
        </Card>
      </div>

      {/* 3. Bottom Row: Full User database table */}
      <Card title="System User Ledger Database" subtitle="Detailed audit indices of all active bank depositors">
        {/* User filter toolbar */}
        <div className="admin-table-toolbar">
          <div className="admin-search-wrapper">
            <Search size={18} className="search-icon" />
            <input
              type="text"
              placeholder="Search by profile name, deposit email..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="admin-search-input-field"
            />
          </div>

          <select
            value={kycFilter}
            onChange={(e) => setKycFilter(e.target.value)}
            className="admin-select-filter"
          >
            <option value="All">All Verification States</option>
            <option value="Approved">Verified Only</option>
            <option value="Pending">Pending Audit</option>
            <option value="Uploaded">Documents Uploaded</option>
          </select>
        </div>

        {/* System User Table */}
        <div className="admin-table-wrapper">
          <table className="admin-table">
            <thead>
              <tr>
                <th>Profile Depositor</th>
                <th>Joined Date</th>
                <th>Access Level</th>
                <th>Identity Audit</th>
                <th>Status</th>
                <th>Administrative Options</th>
              </tr>
            </thead>
            <tbody>
              {filteredUsers.length > 0 ? (
                filteredUsers.map((u) => (
                  <tr key={u.id}>
                    <td>
                      <div className="user-db-profile-cell">
                        <span className="user-db-name">{u.name}</span>
                        <span className="user-db-email text-muted">{u.email}</span>
                      </div>
                    </td>
                    <td>{u.joined}</td>
                    <td>
                      <Badge variant={u.role === 'Admin' ? 'primary' : 'secondary'}>{u.role}</Badge>
                    </td>
                    <td>
                      <Badge
                        variant={
                          u.kycStatus === 'Approved' ? 'success' :
                            u.kycStatus === 'Uploaded' ? 'info' : 'warning'
                        }
                      >
                        {u.kycStatus}
                      </Badge>
                    </td>
                    <td>
                      <span className={`user-status-dot-label ${u.status.toLowerCase()}`}>
                        <span className="dot"></span>
                        {u.status}
                      </span>
                    </td>
                    <td>
                      <Button
                        variant="ghost"
                        size="sm"
                        icon={Ban}
                        onClick={() => handleToggleUserStatus(u.id, u.name)}
                      >
                        Block / Freeze
                      </Button>
                    </td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan="6" className="table-empty-row">
                    No depositors match your current search constraints.
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </Card>
    </div>
  );
};

export default AdminPanel;
