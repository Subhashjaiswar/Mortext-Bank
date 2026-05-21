import React, { useState, useRef, useEffect } from 'react';
import { Sun, Moon, Bell, Search, Globe, ChevronDown, CheckCheck } from 'lucide-react';
import { useAuth } from '../../hooks/useAuth';
import './TopNavbar.css';

const TopNavbar = ({ title }) => {
  const {
    theme,
    toggleTheme,
    notifications,
    markAllNotificationsAsRead,
    language,
    setLanguage
  } = useAuth();

  const [showNotifications, setShowNotifications] = useState(false);
  const [showLangDropdown, setShowLangDropdown] = useState(false);

  const notifRef = useRef(null);
  const langRef = useRef(null);

  const unreadCount = notifications.filter(n => !n.read).length;

  // Click outside to close dropdowns
  useEffect(() => {
    const handleClickOutside = (event) => {
      if (notifRef.current && !notifRef.current.contains(event.target)) {
        setShowNotifications(false);
      }
      if (langRef.current && !langRef.current.contains(event.target)) {
        setShowLangDropdown(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  const handleMarkAllRead = () => {
    markAllNotificationsAsRead();
  };

  const getLanguageLabel = (langCode) => {
    switch (langCode) {
      case 'es': return 'Español';
      case 'fr': return 'Français';
      case 'en':
      default: return 'English';
    }
  };

  return (
    <header className="top-navbar-container">
      {/* Search Bar / Context Titles */}
      <div className="navbar-left">
        <h1 className="navbar-page-title">{title}</h1>
        <div className="search-bar-wrapper">
          <Search size={18} className="search-icon" />
          <input type="text" placeholder="Search transactions, cards, help..." className="search-input" />
        </div>
      </div>

      {/* Action Controls */}
      <div className="navbar-right">
        {/* Language Toggler */}
        <div className="lang-picker-wrapper" ref={langRef}>
          <button className="lang-picker-btn" onClick={() => setShowLangDropdown(!showLangDropdown)}>
            <Globe size={18} />
            <span className="lang-text-label">{getLanguageLabel(language)}</span>
            <ChevronDown size={14} />
          </button>
          
          {showLangDropdown && (
            <ul className="lang-dropdown-menu animate-scale-in">
              <li onClick={() => { setLanguage('en'); setShowLangDropdown(false); }} className={language === 'en' ? 'active' : ''}>English</li>
              <li onClick={() => { setLanguage('es'); setShowLangDropdown(false); }} className={language === 'es' ? 'active' : ''}>Español</li>
              <li onClick={() => { setLanguage('fr'); setShowLangDropdown(false); }} className={language === 'fr' ? 'active' : ''}>Français</li>
            </ul>
          )}
        </div>

        {/* Theme Toggle Button */}
        <button
          className="theme-toggle-btn"
          onClick={toggleTheme}
          title={theme === 'light' ? 'Switch to Dark Theme' : 'Switch to Light Theme'}
          aria-label="Toggle Theme"
        >
          {theme === 'light' ? <Moon size={20} /> : <Sun size={20} />}
        </button>

        {/* Notifications Icon with Badge */}
        <div className="notification-bell-wrapper" ref={notifRef}>
          <button
            className={`navbar-notif-btn ${unreadCount > 0 ? 'has-unread' : ''}`}
            onClick={() => setShowNotifications(!showNotifications)}
            aria-label="View notifications"
          >
            <Bell size={20} />
            {unreadCount > 0 && <span className="notification-badge">{unreadCount}</span>}
          </button>

          {showNotifications && (
            <div className="notifications-dropdown animate-scale-in">
              <div className="notif-dropdown-header">
                <span className="notif-title">Notifications</span>
                {unreadCount > 0 && (
                  <button className="mark-read-btn" onClick={handleMarkAllRead}>
                    <CheckCheck size={14} style={{ marginRight: '4px' }} /> Mark all read
                  </button>
                )}
              </div>
              
              <ul className="notif-dropdown-list">
                {notifications.length > 0 ? (
                  notifications.map((notif) => (
                    <li key={notif.id} className={`notif-item ${!notif.read ? 'unread' : ''}`}>
                      <div className="notif-item-header">
                        <span className="notif-item-title">{notif.title}</span>
                        <span className="notif-item-time">{notif.time}</span>
                      </div>
                      <p className="notif-item-msg">{notif.message}</p>
                      {!notif.read && <span className="unread-dot"></span>}
                    </li>
                  ))
                ) : (
                  <li className="notif-empty">No new notifications</li>
                )}
              </ul>
            </div>
          )}
        </div>
      </div>
    </header>
  );
};

export default TopNavbar;
