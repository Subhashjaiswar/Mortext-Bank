import React from 'react';
import { User, Mail, ShieldAlert, Upload, KeyRound, Globe, Moon, Sun, CheckCircle } from 'lucide-react';
import { useProfileViewModel } from '../../viewmodels/useProfileViewModel';
import { useToast } from '../../components/UI/Toast';
import Card from '../../components/UI/Card';
import Input from '../../components/UI/Input';
import Button from '../../components/UI/Button';
import Badge from '../../components/UI/Badge';
import './Profile.css';

const Profile = () => {
  const toast = useToast();
  const {
    user,
    theme,
    toggleTheme,
    language,
    setLanguage,
    kycStatus,
    profileName,
    setProfileName,
    profileEmail,
    setProfileEmail,
    updatingProfile,
    currentPassword,
    setCurrentPassword,
    newPassword,
    setNewPassword,
    confirmPassword,
    setConfirmPassword,
    changingPass,
    dragActive,
    selectedFile,
    handleProfileSubmit,
    handlePasswordSubmit,
    handleDrag,
    handleDrop,
    handleFileChange,
    handleKycSubmit,
  } = useProfileViewModel();

  return (
    <div className="profile-page-container">

      {/* Left Column: Profile Card and KYC Drag and Drop */}
      <div className="profile-left-col">
        {/* Profile Card */}
        <Card className="profile-avatar-card">
          <div className="profile-avatar-wrapper animate-float">
            <img
              src={user?.avatar || 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&q=80&w=120'}
              alt="Profile Avatar"
              className="p-card-avatar-img"
            />
            <Badge variant="primary" className="p-card-role-badge">
              {user?.role || 'Admin'}
            </Badge>
          </div>
          <div className="profile-card-texts">
            <h3>{user?.name || 'Alex Carter'}</h3>
            <p className="text-muted">{user?.email || 'alex@mortext.com'}</p>
            <span className="joined-date-lbl">Member since: {user?.joined || '2026-05-18'}</span>
          </div>
        </Card>

        {/* KYC Upload Widget */}
        <Card
          title="Identity Verification (KYC)"
          subtitle="Submit official ID cards to approve transfer limit upgrades"
        >
          <div className="kyc-current-status-row">
            <span>Identity Audit State: </span>
            <Badge
              variant={
                kycStatus === 'Approved' ? 'success' :
                  kycStatus === 'Pending' ? 'secondary' :
                    kycStatus === 'Uploaded' ? 'info' : 'danger'
              }
            >
              {kycStatus.toUpperCase()}
            </Badge>
          </div>

          {kycStatus === 'Approved' ? (
            <div className="kyc-verified-box animate-scale-in">
              <CheckCircle size={28} className="verified-icon" />
              <div>
                <h5>Account Fully Verified</h5>
                <p>Your document review passed. Zero-liability payment security holds is active.</p>
              </div>
            </div>
          ) : (
            <div className="kyc-upload-form">
              <div
                className={`drag-drop-zone ${dragActive ? 'active' : ''} ${selectedFile ? 'has-file' : ''}`}
                onDragEnter={handleDrag}
                onDragOver={handleDrag}
                onDragLeave={handleDrag}
                onDrop={handleDrop}
              >
                <input
                  type="file"
                  id="kyc-file-input"
                  onChange={handleFileChange}
                  accept=".pdf,.png,.jpg,.jpeg"
                  className="hidden-file-input"
                />
                <label htmlFor="kyc-file-input" className="drag-drop-label">
                  <Upload size={36} className="upload-cloud-icon" />
                  {selectedFile ? (
                    <div className="selected-file-meta">
                      <h5>Selected ID Document</h5>
                      <span>{selectedFile.name} ({(selectedFile.size / 1024).toFixed(1)} KB)</span>
                    </div>
                  ) : (
                    <div className="drag-drop-texts">
                      <h5>Upload Passport or Driver License</h5>
                      <p>Drag and drop file here, or click to browse files</p>
                      <span>Supports PDF, PNG, JPG (Max 5MB)</span>
                    </div>
                  )}
                </label>
              </div>

              {selectedFile && (
                <Button variant="primary" onClick={handleKycSubmit} fullWidth icon={Upload}>
                  Submit KYC Document
                </Button>
              )}
            </div>
          )}
        </Card>
      </div>

      {/* Right Column: Preferences, Edit Profile, Edit Password */}
      <div className="profile-right-col">
        {/* General Application Preferences (Theme, Language) */}
        <Card title="Application Preferences" subtitle="Customize theme visuals and currency options">
          <div className="preferences-options-list">

            {/* Theme settings */}
            <div className="pref-row">
              <div className="pref-meta">
                <h5>Application Display Theme</h5>
                <p>Switch between modern Light theme or Slate Dark theme.</p>
              </div>
              <Button
                variant="outline"
                size="sm"
                onClick={toggleTheme}
                icon={theme === 'light' ? Moon : Sun}
              >
                {theme === 'light' ? 'Slate Dark Mode' : 'Clean Light Mode'}
              </Button>
            </div>

            {/* Language settings */}
            <div className="pref-row mb-none">
              <div className="pref-meta">
                <h5>Locale Language</h5>
                <p>Modify default application label translations.</p>
              </div>
              <select
                value={language}
                onChange={(e) => { setLanguage(e.target.value); toast.success(`Language set to ${e.target.value.toUpperCase()}`); }}
                className="pref-language-select"
              >
                <option value="en">English (US)</option>
                <option value="es">Español (ES)</option>
                <option value="fr">Français (FR)</option>
              </select>
            </div>

          </div>
        </Card>

        {/* Edit profile details */}
        <Card title="Update Profile Details" subtitle="Edit user name and contact email fields">
          <form onSubmit={handleProfileSubmit} className="profile-edit-form">
            <div className="profile-dual-inputs">
              <Input
                label="Full Profile Name"
                id="p-name"
                value={profileName}
                onChange={(e) => setProfileName(e.target.value)}
                icon={User}
                required
              />
              <Input
                label="Registered Contact Email"
                id="p-email"
                type="email"
                value={profileEmail}
                onChange={(e) => setProfileEmail(e.target.value)}
                icon={Mail}
                required
              />
            </div>
            <Button type="submit" isLoading={updatingProfile} fullWidth>
              Save Profile Changes
            </Button>
          </form>
        </Card>

        {/* Change password details */}
        <Card title="Modify System Password" subtitle="Configure a new secure login credential password">
          <form onSubmit={handlePasswordSubmit} className="password-edit-form">
            <Input
              label="Current Password"
              id="c-pass"
              type="password"
              placeholder="••••••••"
              value={currentPassword}
              onChange={(e) => setCurrentPassword(e.target.value)}
              icon={KeyRound}
              required
            />
            <div className="profile-dual-inputs">
              <Input
                label="New Password"
                id="n-pass"
                type="password"
                placeholder="••••••••"
                value={newPassword}
                onChange={(e) => setNewPassword(e.target.value)}
                icon={KeyRound}
                required
              />
              <Input
                label="Confirm New Password"
                id="conf-pass"
                type="password"
                placeholder="••••••••"
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
                icon={KeyRound}
                required
              />
            </div>
            <Button type="submit" variant="outline" isLoading={changingPass} fullWidth>
              Save New Password
            </Button>
          </form>
        </Card>
      </div>

    </div>
  );
};

export default Profile;
