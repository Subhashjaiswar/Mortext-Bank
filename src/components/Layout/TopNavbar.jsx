import React, { useState, useRef, useEffect } from 'react';
import { Sun, Moon, Bell, Search, CheckCheck } from 'lucide-react';
import { useAuth } from '../../hooks/useAuth';
import './TopNavbar.css';

const TopNavbar = ({ title }) => {
  const {
    theme,
    toggleTheme,
    notifications,
    markAllNotificationsAsRead,
  } = useAuth();

  const [showNotifications, setShowNotifications] = useState(false);

  const notifRef = useRef(null);

  const unreadCount = notifications.filter(n => !n.read).length;

  // Click outside to close dropdowns
  useEffect(() => {
    const handleClickOutside = (event) => {
      if (notifRef.current && !notifRef.current.contains(event.target)) {
        setShowNotifications(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  const handleMarkAllRead = () => {
    markAllNotificationsAsRead();
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
