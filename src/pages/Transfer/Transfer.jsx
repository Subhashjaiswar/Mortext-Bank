import React from 'react';
import {
  ArrowRight,
  UserPlus,
  QrCode,
  Calendar,
  Send,
  CheckCircle,
  HelpCircle,
  CreditCard,
  ScanLine,
  Smartphone
} from 'lucide-react';
import { useTransferViewModel } from '../../viewmodels/useTransferViewModel';
import Card from '../../components/UI/Card';
import Input from '../../components/UI/Input';
import Button from '../../components/UI/Button';
import Modal from '../../components/UI/Modal';
import Badge from '../../components/UI/Badge';
import './Transfer.css';

const Transfer = () => {
  const {
    activeSubTab,
    setActiveSubTab,
    accounts,
    beneficiaries,
    fromAccount,
    setFromAccount,
    selectedBen,
    setSelectedBen,
    amount,
    setAmount,
    remarks,
    setRemarks,
    upiId,
    setUpiId,
    scheduledDate,
    setScheduledDate,
    showAddBenModal,
    setShowAddBenModal,
    showQrScanner,
    setShowQrScanner,
    showConfirmModal,
    setShowConfirmModal,
    showSuccessModal,
    setShowSuccessModal,
    benName,
    setBenName,
    benAccNo,
    setBenAccNo,
    benBank,
    setBenBank,
    benNickname,
    setBenNickname,
    processing,
    successReceipt,
    handleAddBeneficiarySubmit,
    handleQRScanSuccess,
    handleInitiateTransfer,
    handleConfirmTransfer,
    resetForm,
  } = useTransferViewModel();

  return (
    <div className="transfer-page-container">
      
      {/* Tab Selectors */}
      <div className="transfer-sub-tabs">
        <button
          className={`sub-tab-btn ${activeSubTab === 'direct' ? 'active' : ''}`}
          onClick={() => { setActiveSubTab('direct'); resetForm(); }}
        >
          <Send size={18} />
          Direct Bank
        </button>
        <button
          className={`sub-tab-btn ${activeSubTab === 'upi' ? 'active' : ''}`}
          onClick={() => { setActiveSubTab('upi'); resetForm(); }}
        >
          <Smartphone size={18} />
          UPI Address
        </button>
        <button
          className={`sub-tab-btn ${activeSubTab === 'qr' ? 'active' : ''}`}
          onClick={() => { setActiveSubTab('qr'); resetForm(); }}
        >
          <QrCode size={18} />
          QR Payment
        </button>
        <button
          className={`sub-tab-btn ${activeSubTab === 'schedule' ? 'active' : ''}`}
          onClick={() => { setActiveSubTab('schedule'); resetForm(); }}
        >
          <Calendar size={18} />
          Schedule Transfer
        </button>
      </div>

      <div className="transfer-main-layout">
        {/* Left Card - Form Fields */}
        <Card
          title={
            activeSubTab === 'direct' ? 'Bank Wire Transfer' :
            activeSubTab === 'upi' ? 'Instant UPI Payment' :
            activeSubTab === 'qr' ? 'Camera QR Code Scan' :
            'Scheduled Recurring Transfer'
          }
          subtitle="All transactions are secured with 256-bit SSL encryption"
          className="transfer-form-card"
        >
          <form onSubmit={handleInitiateTransfer} className="transfer-form">
            
            {/* 1. Source Account selection */}
            <div className="transfer-input-row">
              <label className="transfer-lbl">Debit Account Source</label>
              <select
                value={fromAccount}
                onChange={(e) => setFromAccount(e.target.value)}
                className="transfer-select-box"
              >
                {accounts.map(acc => (
                  <option key={acc.id} value={acc.id}>
                    {acc.name} (Avail: ${acc.balance.toLocaleString()})
                  </option>
                ))}
              </select>
            </div>

            {/* 2. Direct Account Details */}
            {activeSubTab === 'direct' && (
              <div className="transfer-input-row">
                <div className="beneficiary-header-label">
                  <label className="transfer-lbl">Select Beneficiary</label>
                  <button
                    type="button"
                    className="add-ben-inline-btn"
                    onClick={() => setShowAddBenModal(true)}
                  >
                    <UserPlus size={14} /> Add New
                  </button>
                </div>
                <select
                  value={selectedBen}
                  onChange={(e) => setSelectedBen(e.target.value)}
                  className="transfer-select-box"
                  required
                >
                  <option value="">Choose Recipient...</option>
                  {beneficiaries.map(ben => (
                    <option key={ben.id} value={ben.name}>
                      {ben.name} ({ben.bankName} - {ben.accountNo})
                    </option>
                  ))}
                </select>
              </div>
            )}

            {/* 3. UPI Address Details */}
            {activeSubTab === 'upi' && (
              <Input
                label="Recipient UPI ID / Address"
                id="upi-address"
                placeholder="name@upi or name@okhdfc"
                value={upiId}
                onChange={(e) => setUpiId(e.target.value)}
                icon={Smartphone}
                required
              />
            )}

            {/* 4. QR Code Scanning simulator */}
            {activeSubTab === 'qr' && (
              <div className="qr-scanners-panel">
                {selectedBen ? (
                  <div className="scanned-merchant-info animate-scale-in">
                    <QrCode size={32} className="qr-scanned-icon" />
                    <div className="scanned-det">
                      <span>Recipient Scanned Merchant</span>
                      <h4>{selectedBen}</h4>
                    </div>
                    <Button variant="outline" size="sm" onClick={() => setSelectedBen('')}>Change QR</Button>
                  </div>
                ) : (
                  <div className="qr-unscanned-prompt">
                    <ScanLine size={48} className="qr-scan-svg animate-float" />
                    <p>Scan a merchant's payment QR Code to prepopulate transfer details instantly.</p>
                    <Button variant="outline" type="button" onClick={() => setShowQrScanner(true)} icon={QrCode}>
                      Open Simulator QR Scanner
                    </Button>
                  </div>
                )}
              </div>
            )}

            {/* 5. Scheduled dates */}
            {activeSubTab === 'schedule' && (
              <>
                <div className="transfer-input-row">
                  <label className="transfer-lbl">Select Beneficiary</label>
                  <select
                    value={selectedBen}
                    onChange={(e) => setSelectedBen(e.target.value)}
                    className="transfer-select-box"
                    required
                  >
                    <option value="">Choose Recipient...</option>
                    {beneficiaries.map(ben => (
                      <option key={ben.id} value={ben.name}>
                        {ben.name} ({ben.bankName})
                      </option>
                    ))}
                  </select>
                </div>
                <Input
                  label="Execution Date"
                  id="schedule-date"
                  type="date"
                  value={scheduledDate}
                  onChange={(e) => setScheduledDate(e.target.value)}
                  icon={Calendar}
                  required
                />
              </>
            )}

            {/* Amount input */}
            <Input
              label="Transaction Amount ($)"
              id="tx-amount"
              type="number"
              step="0.01"
              placeholder="0.00"
              value={amount}
              onChange={(e) => setAmount(e.target.value)}
              icon={CreditCard}
              required
            />

            {/* Remarks */}
            <Input
              label="Remarks / Message (Optional)"
              id="tx-remarks"
              placeholder="e.g. Rent, Groceries, Gift"
              value={remarks}
              onChange={(e) => setRemarks(e.target.value)}
            />

            <Button type="submit" isLoading={processing} fullWidth icon={Send}>
              Initiate Secure Payment
            </Button>
          </form>
        </Card>

        {/* Right Card - Static Guide Info */}
        <Card title="Payment Shield Guard" subtitle="Real-time fraud surveillance metrics" className="transfer-guide-card">
          <div className="shield-metrics-list">
            <div className="shield-item">
              <CheckCircle size={18} className="shield-ok" />
              <div>
                <h5>Zero-Liability Guarantee</h5>
                <p>Protected by Mortext Banking Security protocols against unauthorized payments.</p>
              </div>
            </div>
            <div className="shield-item">
              <CheckCircle size={18} className="shield-ok" />
              <div>
                <h5>Real-Time SMS Alerts</h5>
                <p>A message confirmation is sent to your registered phone number upon completion.</p>
              </div>
            </div>
            <div className="shield-item mb-none">
              <HelpCircle size={18} className="shield-help" />
              <div>
                <h5>Limit Information</h5>
                <p>Default instant transaction limit: $10,000.00. Contact support to upgrade limits.</p>
              </div>
            </div>
          </div>
        </Card>
      </div>

      {/* 1. MODAL: Add Beneficiary */}
      <Modal
        isOpen={showAddBenModal}
        onClose={() => setShowAddBenModal(false)}
        title="Add New Beneficiary"
        size="sm"
        footer={
          <>
            <Button variant="secondary" onClick={() => setShowAddBenModal(false)}>Cancel</Button>
            <Button variant="primary" onClick={handleAddBeneficiarySubmit}>Save Beneficiary</Button>
          </>
        }
      >
        <div className="add-ben-form">
          <Input
            label="Recipient Full Name"
            id="ben-name"
            placeholder="e.g. John Doe"
            value={benName}
            onChange={(e) => setBenName(e.target.value)}
            required
          />
          <Input
            label="Account / UPI Number"
            id="ben-acc"
            placeholder="e.g. 1048293029"
            value={benAccNo}
            onChange={(e) => setBenAccNo(e.target.value)}
            required
          />
          <Input
            label="Bank Name / IFSC"
            id="ben-bank"
            placeholder="e.g. Chase Bank, Mortext"
            value={benBank}
            onChange={(e) => setBenBank(e.target.value)}
            required
          />
          <Input
            label="Nickname / Group"
            id="ben-nick"
            placeholder="e.g. Bro, Landlord"
            value={benNickname}
            onChange={(e) => setBenNickname(e.target.value)}
          />
        </div>
      </Modal>

      {/* 2. MODAL: QR Scanner Simulator */}
      <Modal
        isOpen={showQrScanner}
        onClose={() => setShowQrScanner(false)}
        title="QR Code Camera Scanner"
        size="sm"
      >
        <div className="qr-scanner-simulator">
          <div className="scanner-viewfinder">
            <div className="viewfinder-corner top-left"></div>
            <div className="viewfinder-corner top-right"></div>
            <div className="viewfinder-corner bottom-left"></div>
            <div className="viewfinder-corner bottom-right"></div>
            
            <QrCode size={120} className="floating-qr-svg animate-float" />
            <div className="scanning-laser-line"></div>
          </div>
          <p>Hover merchant code inside the scanning brackets to load transaction.</p>
          <Button variant="success" onClick={handleQRScanSuccess} fullWidth>
            Simulate QR Code Scan Success
          </Button>
        </div>
      </Modal>

      {/* 3. MODAL: Transfer Confirmation details */}
      <Modal
        isOpen={showConfirmModal}
        onClose={() => setShowConfirmModal(false)}
        title="Confirm Secure Transfer"
        size="sm"
        footer={
          <>
            <Button variant="secondary" onClick={() => setShowConfirmModal(false)}>Cancel</Button>
            <Button variant="success" onClick={handleConfirmTransfer}>Authenticate & Pay</Button>
          </>
        }
      >
        <div className="tx-confirm-content">
          <div className="confirm-header-amt">
            <span>You are sending</span>
            <h1>${parseFloat(amount || 0).toLocaleString(undefined, { minimumFractionDigits: 2 })}</h1>
          </div>
          <div className="confirm-list-rows">
            <div className="confirm-det-row">
              <span className="det-lbl">To Recipient</span>
              <span className="det-val">{activeSubTab === 'upi' ? upiId : selectedBen}</span>
            </div>
            <div className="confirm-det-row">
              <span className="det-lbl">Debit Account</span>
              <span className="det-val">
                {fromAccount === 'acc-1' ? 'Savings Account' : 'Current Account'}
              </span>
            </div>
            {remarks && (
              <div className="confirm-det-row">
                <span className="det-lbl">Remarks</span>
                <span className="det-val">{remarks}</span>
              </div>
            )}
            {activeSubTab === 'schedule' && (
              <div className="confirm-det-row">
                <span className="det-lbl">Execution Date</span>
                <span className="det-val">{scheduledDate}</span>
              </div>
            )}
          </div>
        </div>
      </Modal>

      {/* 4. MODAL: Transfer Success receipt */}
      <Modal
        isOpen={showSuccessModal}
        onClose={() => setShowSuccessModal(false)}
        title="Transaction Completed"
        size="sm"
        footer={
          <Button variant="primary" onClick={() => setShowSuccessModal(false)} fullWidth>
            Got it, Thank You
          </Button>
        }
      >
        {successReceipt && (
          <div className="tx-success-receipt-wrapper">
            <div className="receipt-success-icon animate-scale-in">
              <CheckCircle size={56} className="checked-svg" />
            </div>
            <h3>Transfer Successful</h3>
            <p className="receipt-banner-msg">The ledger has successfully executed and settled.</p>
            
            <h2 className="receipt-amount-display">
              ${successReceipt.amount.toLocaleString(undefined, { minimumFractionDigits: 2 })}
            </h2>

            <div className="receipt-table-details">
              <div className="receipt-row">
                <span>Transaction Ref ID</span>
                <strong className="font-mono">{successReceipt.txId}</strong>
              </div>
              <div className="receipt-row">
                <span>Recipient Partner</span>
                <strong>{successReceipt.recipient}</strong>
              </div>
              <div className="receipt-row">
                <span>Source Asset</span>
                <strong>{successReceipt.source}</strong>
              </div>
              <div className="receipt-row">
                <span>Settlement Date</span>
                <strong>{successReceipt.date}</strong>
              </div>
              <div className="receipt-row">
                <span>Verification State</span>
                <Badge variant="success">Completed</Badge>
              </div>
            </div>
          </div>
        )}
      </Modal>
    </div>
  );
};

export default Transfer;
