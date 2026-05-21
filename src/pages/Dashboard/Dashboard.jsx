import React, { useState } from 'react';
import {
  ArrowUpRight,
  ArrowDownLeft,
  ArrowLeftRight,
  Wallet,
  ShieldCheck,
  CreditCard,
  Bell,
  Clock,
  ArrowRight,
  TrendingUp,
  TrendingDown
} from 'lucide-react';
import { useDashboardViewModel } from '../../viewmodels/useDashboardViewModel';
import Card from '../../components/UI/Card';
import Button from '../../components/UI/Button';
import Badge from '../../components/UI/Badge';
import Chart from '../../components/UI/Chart';
import Modal from '../../components/UI/Modal';
import './Dashboard.css';

const Dashboard = ({ setActiveTab }) => {
  const {
    user,
    selectedTx,
    setSelectedTx,
    formatCurrency,
    expenseData,
    monthlySummary,
    totalBalance,
    accounts,
    recentTransactions,
    notifications,
    kycStatus,
  } = useDashboardViewModel();



  return (
    <div className="dashboard-page-container">
      {/* 1. Welcome Message */}
      <div className="dashboard-welcome-section">
        <div className="welcome-texts">
          <h2>Hello, {user?.name || 'Alex Carter'} 👋</h2>
          <p>Welcome back! Here is a summary of your bank accounts and expenses.</p>
        </div>
        <div className="kyc-dashboard-alert">
          <Badge variant={kycStatus === 'Approved' ? 'success' : kycStatus === 'Pending' ? 'warning' : 'info'}>
            KYC Status: {kycStatus}
          </Badge>
        </div>
      </div>

      {/* 2. Grid Overview: Balance & Cards & Quick Actions */}
      <div className="dashboard-top-grid">
        {/* Total Balance Card */}
        <Card
          variant="primary"
          title="Total Combined Balance"
          subtitle="All savings and current accounts"
          action={
            <Button variant="ghost" size="sm" onClick={() => setActiveTab('accounts')} style={{ color: '#fff' }}>
              View Accounts
            </Button>
          }
          className="balance-summary-card animate-float"
        >
          <div className="balance-large-display">
            {formatCurrency(totalBalance)}
          </div>
          <div className="balance-account-badges">
            {accounts.map(acc => (
              <div key={acc.id} className="acc-mini-indicator">
                <span className="acc-type-lbl">{acc.name}:</span>
                <span className="acc-val-lbl">{formatCurrency(acc.balance)}</span>
              </div>
            ))}
          </div>
        </Card>

        {/* Quick Actions Card */}
        <Card title="Quick Security & Actions" subtitle="One-click navigation widgets" className="dashboard-quick-actions">
          <div className="quick-actions-grid">
            <button className="q-action-item-btn" onClick={() => setActiveTab('transfer')}>
              <div className="q-icon-box bg-primary">
                <ArrowLeftRight size={20} />
              </div>
              <span>Send Money</span>
            </button>
            
            <button className="q-action-item-btn" onClick={() => setActiveTab('cards')}>
              <div className="q-icon-box bg-accent">
                <CreditCard size={20} />
              </div>
              <span>Freeze Card</span>
            </button>
            
            <button className="q-action-item-btn" onClick={() => setActiveTab('accounts')}>
              <div className="q-icon-box bg-success">
                <Wallet size={20} />
              </div>
              <span>Statements</span>
            </button>
            
            <button className="q-action-item-btn" onClick={() => setActiveTab('profile')}>
              <div className="q-icon-box bg-warning">
                <ShieldCheck size={20} />
              </div>
              <span>KYC Profile</span>
            </button>
          </div>
        </Card>
      </div>

      {/* 3. Mid Grid: Expense Charts & Summaries */}
      <div className="dashboard-mid-grid">
        {/* Weekly Analytics Line Chart */}
        <Card title="Weekly Expenses" subtitle="Custom SVG analytics line graph" className="analytics-card">
          <div className="analytics-trend-header">
            <div className="trend-amount">
              <h3>$3,170.00</h3>
              <span className="trend-pct-up"><TrendingUp size={14} /> +12.4% this week</span>
            </div>
            <div className="trend-desc text-muted">Weekly limit: $5,000.00</div>
          </div>
          <Chart data={expenseData} type="area" height={200} />
        </Card>

        {/* Monthly Summary Bar Chart */}
        <Card title="Monthly Cashflow" subtitle="Deposits / Income trends" className="analytics-card">
          <div className="analytics-trend-header">
            <div className="trend-amount">
              <h3>$10,800.00</h3>
              <span className="trend-pct-down"><TrendingDown size={14} /> -3.2% from last month</span>
            </div>
            <div className="trend-desc text-muted">Avg Monthly Deposit</div>
          </div>
          <Chart data={monthlySummary} type="bar" height={200} />
        </Card>
      </div>

      {/* 4. Bottom Grid: Recent Transactions & Notifications */}
      <div className="dashboard-bottom-grid">
        {/* Recent Transactions list */}
        <Card
          title="Recent Ledger Actions"
          subtitle="Your latest 3 transactions"
          action={
            <Button variant="ghost" size="sm" onClick={() => setActiveTab('history')} icon={ArrowRight} iconPosition="right">
              View All
            </Button>
          }
          className="dashboard-recent-txs"
        >
          <div className="tx-compact-list">
            {recentTransactions.map((tx) => (
              <div key={tx.id} className="tx-compact-row" onClick={() => setSelectedTx(tx)}>
                <div className={`tx-icon-container ${tx.type}`}>
                  {tx.type === 'credit' ? <ArrowDownLeft size={18} /> : <ArrowUpRight size={18} />}
                </div>
                <div className="tx-details-mini">
                  <span className="tx-title-lbl">{tx.title}</span>
                  <span className="tx-time-lbl">{tx.date} • {tx.category}</span>
                </div>
                <div className={`tx-amount-display ${tx.type}`}>
                  {tx.type === 'credit' ? '+' : '-'}{formatCurrency(tx.amount)}
                </div>
              </div>
            ))}
          </div>
        </Card>

        {/* Notifications Widget */}
        <Card
          title="System & Security Alerts"
          subtitle="Unread notifications dashboard"
          action={
            <div className="notif-badge-bubble">
              <Bell size={16} />
              <span>{notifications.filter(n => !n.read).length} Unread</span>
            </div>
          }
          className="dashboard-alerts-widget"
        >
          <div className="notif-compact-list">
            {notifications.slice(0, 3).map((notif) => (
              <div key={notif.id} className={`notif-compact-row ${!notif.read ? 'unread' : ''}`}>
                <div className="notif-marker-dot"></div>
                <div className="notif-info-mini">
                  <span className="notif-header-title">{notif.title}</span>
                  <p className="notif-body-message">{notif.message}</p>
                </div>
                <span className="notif-time-ago"><Clock size={12} style={{ marginRight: '3px' }} /> {notif.time}</span>
              </div>
            ))}
          </div>
        </Card>
      </div>

      {/* Transaction Details Modal */}
      <Modal
        isOpen={selectedTx !== null}
        onClose={() => setSelectedTx(null)}
        title="Transaction Ledger Audit"
        size="sm"
        footer={
          <Button variant="secondary" onClick={() => setSelectedTx(null)}>
            Dismiss Audit
          </Button>
        }
      >
        {selectedTx && (
          <div className="tx-audit-modal-content">
            <div className={`audit-badge-container ${selectedTx.type}`}>
              {selectedTx.type === 'credit' ? <ArrowDownLeft size={28} /> : <ArrowUpRight size={28} />}
              <h3>{selectedTx.type === 'credit' ? '+' : '-'}{formatCurrency(selectedTx.amount)}</h3>
              <Badge variant={selectedTx.status === 'Completed' ? 'success' : 'warning'}>
                {selectedTx.status}
              </Badge>
            </div>
            <div className="audit-details-grid">
              <div className="audit-detail-row">
                <span className="audit-lbl">Ledger Title</span>
                <span className="audit-val">{selectedTx.title}</span>
              </div>
              <div className="audit-detail-row">
                <span className="audit-lbl">Description</span>
                <span className="audit-val">{selectedTx.description}</span>
              </div>
              <div className="audit-detail-row">
                <span className="audit-lbl">Category</span>
                <span className="audit-val">{selectedTx.category}</span>
              </div>
              <div className="audit-detail-row">
                <span className="audit-lbl">Timestamp Date</span>
                <span className="audit-val">{selectedTx.date}</span>
              </div>
              <div className="audit-detail-row">
                <span className="audit-lbl">Transaction Reference ID</span>
                <span className="audit-val font-mono">{selectedTx.id}</span>
              </div>
              <div className="audit-detail-row">
                <span className="audit-lbl">Asset Source</span>
                <span className="audit-val">
                  {selectedTx.accountId === 'acc-1' ? 'Savings Account' : 'Current Account'}
                </span>
              </div>
            </div>
          </div>
        )}
      </Modal>
    </div>
  );
};

export default Dashboard;
