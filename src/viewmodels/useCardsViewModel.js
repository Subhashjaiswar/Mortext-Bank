import { useState, useEffect } from 'react';
import { useAuth } from '../hooks/useAuth';
import { useToast } from '../components/UI/Toast';
import { cardService } from '../services/cardService';

export const useCardsViewModel = () => {
  const { cards: globalCards, triggerGlobalRefresh } = useAuth();
  const toast = useToast();

  const [cards, setCards] = useState(globalCards);
  const [selectedCardId, setSelectedCardId] = useState(globalCards[0]?.id || 'card-1');
  const [revealDetails, setRevealDetails] = useState(false);

  // Form States
  const [currentPin, setCurrentPin] = useState('');
  const [newPin, setNewPin] = useState('');
  const [confirmPin, setConfirmPin] = useState('');
  const [changingPin, setChangingPin] = useState(false);

  const fetchCards = async () => {
    try {
      const data = await cardService.getCards();
      setCards(data);
    } catch (err) {
      console.error('Failed to fetch cards via cardService', err);
    }
  };

  useEffect(() => {
    fetchCards();
  }, [globalCards]);

  const selectedCard = cards.find((c) => c.id === selectedCardId) || cards[0] || {};

  const handleToggleFreeze = async () => {
    if (!selectedCard.id) return;
    const isFrozen = selectedCard.status === 'Frozen';
    const cardType = (selectedCard.type || 'card').toUpperCase();
    const cardLastFour = (selectedCard.number || '').slice(-4);
    
    try {
      if (isFrozen) {
        await cardService.unfreezeCard(selectedCard.id);
        toast.success(`Your ${cardType} Card ending in ${cardLastFour} has been unfrozen.`);
      } else {
        await cardService.freezeCard(selectedCard.id);
        toast.success(`Your ${cardType} Card ending in ${cardLastFour} has been frozen.`);
      }
      fetchCards();
      if (triggerGlobalRefresh) triggerGlobalRefresh(); // Sync global auth context if needed
    } catch (err) {
      toast.error('Failed to change freeze status.');
    }
  };

  const handlePINSubmit = async (e) => {
    e.preventDefault();
    if (!currentPin || !newPin || !confirmPin) {
      toast.error('All PIN fields are required.');
      return;
    }
    if (newPin.length !== 4 || isNaN(newPin)) {
      toast.error('New PIN must be exactly 4 numeric digits.');
      return;
    }
    if (newPin !== confirmPin) {
      toast.error('New PIN and confirmation do not match.');
      return;
    }

    setChangingPin(true);
    try {
      await cardService.changePin(selectedCard.id, currentPin, newPin);
      setChangingPin(false);
      setCurrentPin('');
      setNewPin('');
      setConfirmPin('');
      toast.success('Card security PIN updated successfully!');
      if (triggerGlobalRefresh) triggerGlobalRefresh();
    } catch (err) {
      setChangingPin(false);
      toast.error(err.message || 'Failed to update card PIN.');
    }
  };

  const handleLimitSliderChange = async (e) => {
    const nextLimit = parseInt(e.target.value);
    // Optimistic UI update
    setCards((prev) =>
      prev.map((c) => (c.id === selectedCard.id ? { ...c, limit: nextLimit } : c))
    );
    try {
      await cardService.updateLimits(selectedCard.id, 0, nextLimit);
      if (triggerGlobalRefresh) triggerGlobalRefresh();
    } catch (err) {
      toast.error('Failed to modify card limit.');
      fetchCards(); // rollback
    }
  };

  const formatCardNumber = (num, reveal) => {
    if (!num) return '';
    if (reveal) return num;
    return `•••• •••• •••• ${num.slice(-4)}`;
  };

  return {
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
  };
};
