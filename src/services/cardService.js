import { apiClient } from './apiClient';
import { mockDb } from './mockDb';

export const cardService = {
  getCards: async () => {
    if (apiClient.isMock()) {
      await apiClient.sleep(500);
      const cards = mockDb.getCards();
      apiClient.logResponse('GET', '/api/cards', 200, cards);
      return cards;
    }
    return await apiClient.get('/cards');
  },

  getCardById: async (cardId) => {
    if (apiClient.isMock()) {
      await apiClient.sleep(500);
      const cards = mockDb.getCards();
      const card = cards.find((c) => c.id === cardId);
      if (!card) throw new Error('Card not found');
      apiClient.logResponse('GET', `/api/cards/${cardId}`, 200, card);
      return card;
    }
    return await apiClient.get(`/cards/${cardId}`);
  },

  freezeCard: async (cardId) => {
    if (apiClient.isMock()) {
      await apiClient.sleep(800);
      const cards = mockDb.getCards();
      const updated = cards.map((c) => {
        if (c.id === cardId) {
          return { ...c, status: 'Frozen' };
        }
        return c;
      });
      mockDb.saveCards(updated);
      apiClient.logResponse('POST', `/api/cards/${cardId}/freeze`, 200, { status: 'Frozen' });
      return { status: 'Frozen' };
    }
    return await apiClient.post(`/cards/${cardId}/freeze`);
  },

  unfreezeCard: async (cardId) => {
    if (apiClient.isMock()) {
      await apiClient.sleep(800);
      const cards = mockDb.getCards();
      const updated = cards.map((c) => {
        if (c.id === cardId) {
          return { ...c, status: 'Active' };
        }
        return c;
      });
      mockDb.saveCards(updated);
      apiClient.logResponse('POST', `/api/cards/${cardId}/unfreeze`, 200, { status: 'Active' });
      return { status: 'Active' };
    }
    return await apiClient.post(`/cards/${cardId}/unfreeze`);
  },

  changePin: async (cardId, currentPin, newPin) => {
    if (apiClient.isMock()) {
      await apiClient.sleep(1200);
      // Simulate validation
      if (newPin.length !== 4 || isNaN(newPin)) {
        throw new Error('New PIN must be exactly 4 numeric digits.');
      }
      apiClient.logResponse('POST', `/api/cards/change-pin`, 200, { message: 'PIN updated successfully' });
      return true;
    }
    return await apiClient.post(`/cards/change-pin`, { cardId, currentPin, newPin });
  },

  updateLimits: async (cardId, dailyLimit, monthlyLimit) => {
    if (apiClient.isMock()) {
      await apiClient.sleep(800);
      const cards = mockDb.getCards();
      const updated = cards.map((c) => {
        if (c.id === cardId) {
          return { ...c, limit: monthlyLimit }; // map monthlyLimit to UI c.limit
        }
        return c;
      });
      mockDb.saveCards(updated);
      apiClient.logResponse('PUT', `/api/cards/${cardId}/limits?dailyLimit=${dailyLimit}&monthlyLimit=${monthlyLimit}`, 200, { dailyLimit, monthlyLimit });
      return { dailyLimit, monthlyLimit };
    }
    return await apiClient.put(`/cards/${cardId}/limits?dailyLimit=${dailyLimit}&monthlyLimit=${monthlyLimit}`);
  }
};
