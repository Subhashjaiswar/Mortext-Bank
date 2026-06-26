import React from 'react';
import { Search, Calendar, FileSpreadsheet, RefreshCw, AlertTriangle, Loader2 } from 'lucide-react';
import { useHistoryViewModel } from '../../viewmodels/useHistoryViewModel';
import Card from '../../components/UI/Card';
import Button from '../../components/UI/Button';
import Badge from '../../components/UI/Badge';
import './History.css';

const History = () => {
  const {
    query,
    setQuery,
    typeFilter,
    setTypeFilter,
    categoryFilter,
    setCategoryFilter,
    dateFilter,
    setDateFilter,
    exporting,
    loading,
    error,
    currentPage,
    setCurrentPage,
    filteredTxs,
    paginatedTxs,
    totalPages,
    availableCategories,
    handleExportCSV,
    resetAllFilters,
    formatCurrency,
    handleRetry,
  } = useHistoryViewModel();

  return (
    <div className="history-page-container">
      {/* Search & Filters Card */}
      <Card title="Ledger Filters & Sorts" subtitle="Refine your bank ledger audit logs">
        <div className="history-filter-grid">
          {/* Search bar */}
          <div className="h-filter-item">
            <label className="h-lbl">Search Term</label>
            <div className="h-field-wrapper">
              <Search size={18} className="h-field-icon" />
              <input
                type="text"
                placeholder="Payee, store, keywords..."
                value={query}
                onChange={(e) => { setQuery(e.target.value); setCurrentPage(1); }}
                className="h-field"
              />
            </div>
          </div>

          {/* Type filter */}
          <div className="h-filter-item">
            <label className="h-lbl">Transaction Type</label>
            <select
              value={typeFilter}
              onChange={(e) => { setTypeFilter(e.target.value); setCurrentPage(1); }}
              className="h-select"
            >
              <option value="all">All Types</option>
              <option value="credit">Deposits / Income</option>
              <option value="debit">Withdrawals / Transfers</option>
            </select>
          </div>

          {/* Category Filter — dynamically populated */}
          <div className="h-filter-item">
            <label className="h-lbl">Expense Category</label>
            <select
              value={categoryFilter}
              onChange={(e) => { setCategoryFilter(e.target.value); setCurrentPage(1); }}
              className="h-select"
            >
              <option value="all">All Categories</option>
              {availableCategories.map((cat) => (
                <option key={cat} value={cat}>{cat}</option>
              ))}
            </select>
          </div>

          {/* Date Picker */}
          <div className="h-filter-item">
            <label className="h-lbl">Audit Date</label>
            <div className="h-field-wrapper">
              <Calendar size={18} className="h-field-icon" />
              <input
                type="date"
                value={dateFilter}
                onChange={(e) => { setDateFilter(e.target.value); setCurrentPage(1); }}
                className="h-field date-picker-input"
              />
            </div>
          </div>
        </div>

        {/* Buttons Toolbar */}
        <div className="history-toolbar-btns">
          <Button variant="outline" size="sm" onClick={resetAllFilters}>
            Clear Search Filters
          </Button>
          <Button
            variant="outline"
            size="sm"
            onClick={handleRetry}
            icon={RefreshCw}
          >
            Refresh
          </Button>
          <Button
            variant="primary"
            size="sm"
            onClick={handleExportCSV}
            isLoading={exporting}
            icon={FileSpreadsheet}
          >
            Export Ledger to CSV
          </Button>
        </div>
      </Card>

      {/* Transactions Table List */}
      <Card
        title="Consolidated Bank Ledger"
        subtitle={
          loading
            ? 'Fetching transactions from server...'
            : error
              ? 'Failed to load transactions'
              : `Audit results: ${filteredTxs.length} money movements match query`
        }
      >
        {/* Loading State */}
        {loading && (
          <div className="h-loading-container">
            <div className="h-loading-spinner">
              <Loader2 size={40} className="h-spin-icon" />
            </div>
            <p className="h-loading-text">Fetching transactions from the server...</p>
            <p className="h-loading-subtext">Connecting to API with Bearer authentication</p>
          </div>
        )}

        {/* Error State */}
        {!loading && error && (
          <div className="h-error-container">
            <div className="h-error-icon-wrap">
              <AlertTriangle size={40} />
            </div>
            <h3 className="h-error-title">Unable to Load Transactions</h3>
            <p className="h-error-message">{error}</p>
            <div className="h-error-actions">
              <Button variant="primary" size="sm" onClick={handleRetry} icon={RefreshCw}>
                Retry Connection
              </Button>
            </div>
            <p className="h-error-hint">
              Ensure your backend is running at <code>localhost:8080</code> and you are authenticated.
            </p>
          </div>
        )}

        {/* Data State */}
        {!loading && !error && (
          <>
            <div className="history-table-container">
              <table className="history-table">
                <thead>
                  <tr>
                    <th>Date</th>
                    <th>Reference ID</th>
                    <th>Transaction Details</th>
                    <th>Category</th>
                    <th>Cashflow</th>
                    <th>Amount</th>
                    <th>Status</th>
                  </tr>
                </thead>
                <tbody>
                  {paginatedTxs.length > 0 ? (
                    paginatedTxs.map((tx) => (
                      <tr key={tx.id} className="history-table-row">
                        <td>
                          <span className="h-date-cell">
                            <Calendar size={14} style={{ marginRight: '6px', color: 'var(--text-muted)' }} />
                            {tx.date}
                          </span>
                        </td>
                        <td>
                          <span className="font-mono h-ref-cell">{tx.id}</span>
                        </td>
                        <td>
                          <div className="h-details-cell">
                            <span className="h-details-title">{tx.title}</span>
                            <p className="h-details-desc">{tx.description}</p>
                          </div>
                        </td>
                        <td>
                          <Badge variant="secondary">{tx.category}</Badge>
                        </td>
                        <td>
                          <span className={`h-cashflow-tag ${tx.type}`}>
                            {tx.type === 'credit' ? 'INFLOW' : 'OUTFLOW'}
                          </span>
                        </td>
                        <td>
                          <span className={`h-amount-tag ${tx.type}`}>
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
                      <td colSpan="7" className="table-empty-row">
                        No transactions meet the specified search query parameters.
                      </td>
                    </tr>
                  )}
                </tbody>
              </table>
            </div>

            {/* Pagination footer */}
            {totalPages > 1 && (
              <div className="history-pagination-footer">
                <span className="pagination-info">
                  Showing page {currentPage} of {totalPages} ({filteredTxs.length} items)
                </span>
                <div className="pagination-buttons">
                  <Button
                    variant="outline"
                    size="sm"
                    disabled={currentPage === 1}
                    onClick={() => setCurrentPage((prev) => Math.max(prev - 1, 1))}
                  >
                    Previous
                  </Button>
                  <div className="page-numeric-indicator">{currentPage}</div>
                  <Button
                    variant="outline"
                    size="sm"
                    disabled={currentPage === totalPages}
                    onClick={() => setCurrentPage((prev) => Math.min(prev + 1, totalPages))}
                  >
                    Next
                  </Button>
                </div>
              </div>
            )}
          </>
        )}
      </Card>
    </div>
  );
};

export default History;
