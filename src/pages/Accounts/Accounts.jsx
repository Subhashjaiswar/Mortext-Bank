import React from 'react';
import { Search, Download, Calendar, Filter } from 'lucide-react';
import { useAccountsViewModel } from '../../viewmodels/useAccountsViewModel';
import Card from '../../components/UI/Card';
import Button from '../../components/UI/Button';
import Badge from '../../components/UI/Badge';
import './Accounts.css';

const Accounts = () => {
  const {
    accounts,
    selectedAccId,
    setSelectedAccId,
    searchTerm,
    setSearchTerm,
    categoryFilter,
    setCategoryFilter,
    downloading,
    currentPage,
    setCurrentPage,
    currentAccount,
    paginatedTxs,
    totalPages,
    filteredTxs,
    handleDownloadStatement,
    formatCurrency,
  } = useAccountsViewModel();

  return (
    <div className="accounts-page-container">
      {/* 1. Account Cards Showcase */}
      <div className="accounts-selection-grid">
        {accounts.map((acc) => {
          const isSelected = acc.id === selectedAccId;
          return (
            <Card
              key={acc.id}
              onClick={() => { setSelectedAccId(acc.id); setCurrentPage(1); }}
              variant={isSelected ? 'gradient' : 'default'}
              hoverEffect
              className={`account-nav-card ${isSelected ? 'selected' : ''}`}
            >
              <div className="acc-card-header">
                <span className="acc-card-type">{acc.name}</span>
                <Badge variant={acc.status === 'Active' ? 'success' : 'secondary'}>
                  {acc.status}
                </Badge>
              </div>
              <h3 className="acc-card-balance">{formatCurrency(acc.balance)}</h3>
              <div className="acc-card-footer">
                <span className="acc-card-no">{acc.number}</span>
                <span className="acc-card-badge-type">{acc.type.toUpperCase()}</span>
              </div>
            </Card>
          );
        })}
      </div>

      {/* 2. Selected Account Details Section */}
      <Card title="Account Statement Ledger" subtitle={`Detailed audit logs for ${currentAccount.name}`}>
        
        {/* Filters and Searches Toolbar */}
        <div className="ledger-toolbar">
          <div className="toolbar-search-input">
            <Search size={18} className="search-icon" />
            <input
              type="text"
              placeholder="Search by keyword, recipient, shop name..."
              value={searchTerm}
              onChange={(e) => { setSearchTerm(e.target.value); setCurrentPage(1); }}
              className="toolbar-field"
            />
          </div>

          <div className="toolbar-filters">
            <div className="filter-dropdown-wrapper">
              <Filter size={16} className="filter-icon" />
              <select
                value={categoryFilter}
                onChange={(e) => { setCategoryFilter(e.target.value); setCurrentPage(1); }}
                className="toolbar-select"
              >
                <option value="All">All Categories</option>
                <option value="Income">Income</option>
                <option value="Shopping">Shopping</option>
                <option value="Transfer">Transfer</option>
                <option value="Bills">Bills</option>
              </select>
            </div>

            <Button
              variant="primary"
              size="md"
              onClick={handleDownloadStatement}
              isLoading={downloading}
              icon={Download}
            >
              Download CSV Statement
            </Button>
          </div>
        </div>

        {/* Statement Data Table */}
        <div className="statement-table-wrapper">
          <table className="statement-table">
            <thead>
              <tr>
                <th>Date</th>
                <th>Transaction</th>
                <th>Category</th>
                <th>Type</th>
                <th>Amount</th>
                <th>Status</th>
              </tr>
            </thead>
            <tbody>
              {paginatedTxs.length > 0 ? (
                paginatedTxs.map((tx) => (
                  <tr key={tx.id}>
                    <td>
                      <span className="tx-date-cell">
                        <Calendar size={14} style={{ marginRight: '6px', color: 'var(--text-muted)' }} />
                        {tx.date}
                      </span>
                    </td>
                    <td>
                      <div className="tx-details-cell">
                        <span className="tx-name-title">{tx.title}</span>
                        <span className="tx-name-desc">{tx.description}</span>
                      </div>
                    </td>
                    <td>
                      <Badge variant="secondary">{tx.category}</Badge>
                    </td>
                    <td>
                      <span className={`tx-type-badge ${tx.type}`}>
                        {tx.type === 'credit' ? 'Deposit' : 'Withdrawal'}
                      </span>
                    </td>
                    <td>
                      <span className={`tx-amount-badge ${tx.type}`}>
                        {tx.type === 'credit' ? '+' : '-'}{formatCurrency(tx.amount)}
                      </span>
                    </td>
                    <td>
                      <Badge variant={tx.status === 'Completed' ? 'success' : 'warning'}>
                        {tx.status}
                      </Badge>
                    </td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan="6" className="table-empty-row">
                    No transactions match your current search and filter filters.
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>

        {/* Pagination Toolbar */}
        {totalPages > 1 && (
          <div className="statement-pagination-bar">
            <span className="pagination-info">
              Showing page {currentPage} of {totalPages} ({filteredTxs.length} items)
            </span>
            <div className="pagination-buttons">
              <Button
                variant="outline"
                size="sm"
                disabled={currentPage === 1}
                onClick={() => setCurrentPage(prev => Math.max(prev - 1, 1))}
              >
                Previous
              </Button>
              <div className="page-numeric-indicator">
                {currentPage}
              </div>
              <Button
                variant="outline"
                size="sm"
                disabled={currentPage === totalPages}
                onClick={() => setCurrentPage(prev => Math.min(prev + 1, totalPages))}
              >
                Next
              </Button>
            </div>
          </div>
        )}
      </Card>
    </div>
  );
};

export default Accounts;
