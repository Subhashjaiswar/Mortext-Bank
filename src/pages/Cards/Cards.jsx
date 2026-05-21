import React from 'react';
import { ShieldAlert, KeyRound, Eye, EyeOff, Sliders, ToggleLeft, ToggleRight } from 'lucide-react';
import { useCardsViewModel } from '../../viewmodels/useCardsViewModel';
import Card from '../../components/UI/Card';
import Input from '../../components/UI/Input';
import Button from '../../components/UI/Button';
import Badge from '../../components/UI/Badge';
import './Cards.css';

const Cards = () => {
  const {
    cards,
    selectedCardId,
    setSelectedCardId,
    revealDetails,
    setRevealDetails,
    currentPin,
    setCurrentPin,
    newPin,
    setNewPin,
    confirmPin,
    setConfirmPin,
    changingPin,
    selectedCard,
    handleToggleFreeze,
    handlePINSubmit,
    handleLimitSliderChange,
    formatCardNumber,
  } = useCardsViewModel();

  return (
    <div className="cards-page-container">
      {/* 1. Realistic Virtual Cards Showcase */}
      <div className="cards-view-panel">
        <div className="virtual-cards-carousel">
          {cards.map((c) => {
            const isSelected = c.id === selectedCardId;
            const isDebit = c.type === 'debit';
            return (
              <div
                key={c.id}
                onClick={() => setSelectedCardId(c.id)}
                className={`virtual-card-wrapper ${isSelected ? 'active-c' : ''} ${isDebit ? 'debit-card-theme' : 'credit-card-theme'}`}
              >
                {/* Holographic reflections */}
                <div className="card-glass-glare"></div>

                {/* Bank logo banner */}
                <div className="v-card-logo-row">
                  <div className="card-logo-symbol">
                    <div className="circle-1"></div>
                    <div className="circle-2"></div>
                    <span className="card-logo-brand-text">Mortext</span>
                  </div>
                  <span className="card-tier-label">{c.type.toUpperCase()}</span>
                </div>

                {/* Metallic smartchip */}
                <div className="card-chip-element">
                  <div className="chip-micro-lines"></div>
                </div>

                {/* Card Number display */}
                <div className="v-card-number-display">
                  {formatCardNumber(c.number, revealDetails && isSelected)}
                </div>

                {/* Expiry and holder labels */}
                <div className="v-card-footer-row">
                  <div className="v-card-holder-col">
                    <span className="lbl-txt">CARDHOLDER</span>
                    <span className="val-txt">{c.holder}</span>
                  </div>

                  <div className="v-card-expiry-col">
                    <span className="lbl-txt">EXPIRES</span>
                    <span className="val-txt">{c.expiry}</span>
                  </div>

                  <div className="v-card-cvv-col">
                    <span className="lbl-txt">CVV</span>
                    <span className="val-txt">{revealDetails && isSelected ? c.cvv : '•••'}</span>
                  </div>
                </div>

                {/* Frozen status overlay stamp */}
                {c.status === 'Frozen' && (
                  <div className="frozen-overlay-stamp">
                    <ShieldAlert size={28} />
                    <span>CARD FROZEN</span>
                  </div>
                )}
              </div>
            );
          })}
        </div>

        {/* Visibility actions panel */}
        <div className="reveal-actions-panel">
          <Button
            variant="outline"
            size="sm"
            onClick={() => setRevealDetails(!revealDetails)}
            icon={revealDetails ? EyeOff : Eye}
          >
            {revealDetails ? 'Hide Secure Details' : 'Reveal Card Credentials'}
          </Button>
          <div className="active-card-meta-badge">
            <span>Currently Editing: </span>
            <Badge variant="primary">{selectedCard.name || `${selectedCard.type.toUpperCase()} Card`}</Badge>
          </div>
        </div>
      </div>

      {/* 2. Operations Split Details */}
      <div className="cards-operations-grid">

        {/* Toggle Switches & Spending Limits slider */}
        <Card title="Card Authorization & Settings" subtitle="Configure spending and security thresholds">
          <div className="card-toggles-list">

            {/* Freeze Toggle */}
            <div className="card-toggle-row">
              <div className="toggle-meta">
                <h5>Freeze / Block Card</h5>
                <p>Temporarily deactivate card authorization for all online/offline checkouts.</p>
              </div>
              <button className="toggle-switch-btn" onClick={handleToggleFreeze}>
                {selectedCard.status === 'Frozen' ? (
                  <ToggleRight size={44} className="toggle-active-svg" />
                ) : (
                  <ToggleLeft size={44} className="toggle-inactive-svg" />
                )}
              </button>
            </div>

            {/* Slider Limit control */}
            <div className="card-slider-limit-block">
              <div className="slider-meta-header">
                <div className="meta-titles">
                  <h5>Spent Limits Control</h5>
                  <p>Slide to define the maximum transaction cap allowed in a 30-day window.</p>
                </div>
                <div className="slider-limit-indicator">
                  <strong>${selectedCard.limit.toLocaleString()}</strong>
                  <span className="text-muted">/ ${selectedCard.maxLimit.toLocaleString()}</span>
                </div>
              </div>
              <div className="slider-input-wrapper">
                <input
                  type="range"
                  min="500"
                  max={selectedCard.maxLimit}
                  step="250"
                  value={selectedCard.limit}
                  onChange={handleLimitSliderChange}
                  disabled={selectedCard.status === 'Frozen'}
                  className="spend-slider-bar"
                />
                <div className="slider-caps-markers">
                  <span>Min: $500</span>
                  <span>Max: ${selectedCard.maxLimit.toLocaleString()}</span>
                </div>
              </div>
            </div>

            {/* Alert banner */}
            {selectedCard.status === 'Frozen' && (
              <div className="frozen-limits-alert">
                <ShieldAlert size={16} />
                <span>Card is frozen! Limits modifications are disabled until unfrozen.</span>
              </div>
            )}

          </div>
        </Card>

        {/* Change Card secure PIN form */}
        <Card title="Change Security PIN" subtitle="Configure a secure numeric code for POS terminal sales">
          <form onSubmit={handlePINSubmit} className="change-pin-form">
            <Input
              label="Current ATM PIN"
              id="c-pin"
              type="password"
              placeholder="••••"
              maxLength={4}
              value={currentPin}
              onChange={(e) => setCurrentPin(e.target.value.replace(/\D/g, ''))}
              icon={KeyRound}
              disabled={selectedCard.status === 'Frozen'}
              required
            />

            <div className="pin-double-inputs">
              <Input
                label="Configure New PIN"
                id="n-pin"
                type="password"
                placeholder="••••"
                maxLength={4}
                value={newPin}
                onChange={(e) => setNewPin(e.target.value.replace(/\D/g, ''))}
                icon={KeyRound}
                disabled={selectedCard.status === 'Frozen'}
                required
              />
              <Input
                label="Re-enter New PIN"
                id="conf-pin"
                type="password"
                placeholder="••••"
                maxLength={4}
                value={confirmPin}
                onChange={(e) => setConfirmPin(e.target.value.replace(/\D/g, ''))}
                icon={KeyRound}
                disabled={selectedCard.status === 'Frozen'}
                required
              />
            </div>

            <Button
              type="submit"
              variant="outline"
              isLoading={changingPin}
              disabled={selectedCard.status === 'Frozen'}
              icon={Sliders}
              fullWidth
            >
              Update PIN Authorization
            </Button>
          </form>
        </Card>

      </div>
    </div>
  );
};

export default Cards;
