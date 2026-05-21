import { useState, useEffect } from 'react';
import { useAuth } from '../hooks/useAuth';
import { useToast } from '../components/UI/Toast';
import { profileService } from '../services/profileService';

export const useProfileViewModel = () => {
  const {
    user: globalUser,
    theme,
    toggleTheme,
    language,
    setLanguage,
    kycStatus: globalKycStatus,
    triggerGlobalRefresh
  } = useAuth();

  const toast = useToast();

  // Local sync stats
  const [user, setUser] = useState(globalUser);
  const [kycStatus, setKycStatus] = useState(globalKycStatus);

  // Profile Edit Form States
  const [profileName, setProfileName] = useState(globalUser?.name || 'Alex Carter');
  const [profileEmail, setProfileEmail] = useState(globalUser?.email || 'alex@mortext.com');
  const [updatingProfile, setUpdatingProfile] = useState(false);

  // Password Modification States
  const [currentPassword, setCurrentPassword] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [changingPass, setChangingPass] = useState(false);

  // File Upload states
  const [dragActive, setDragActive] = useState(false);
  const [selectedFile, setSelectedFile] = useState(null);

  useEffect(() => {
    setUser(globalUser);
    if (globalUser) {
      setProfileName(globalUser.name);
      setProfileEmail(globalUser.email);
    }
  }, [globalUser]);

  useEffect(() => {
    setKycStatus(globalKycStatus);
  }, [globalKycStatus]);

  const handleProfileSubmit = async (e) => {
    e.preventDefault();
    if (!profileName || !profileEmail) {
      toast.error('Name and Email are required.');
      return;
    }

    setUpdatingProfile(true);
    try {
      await profileService.updateProfile({ name: profileName, email: profileEmail });
      setUpdatingProfile(false);
      toast.success('Profile details updated successfully!');
      if (triggerGlobalRefresh) triggerGlobalRefresh();
    } catch (err) {
      setUpdatingProfile(false);
      toast.error('Failed to update profile details.');
    }
  };

  const handlePasswordSubmit = async (e) => {
    e.preventDefault();
    if (!currentPassword || !newPassword || !confirmPassword) {
      toast.error('All fields are required.');
      return;
    }
    if (newPassword.length < 6) {
      toast.error('Password must be at least 6 characters.');
      return;
    }
    if (newPassword !== confirmPassword) {
      toast.error('Passwords do not match.');
      return;
    }

    setChangingPass(true);
    try {
      await profileService.changePassword(currentPassword, newPassword);
      setChangingPass(false);
      setCurrentPassword('');
      setNewPassword('');
      setConfirmPassword('');
      toast.success('Your security password has been changed.');
    } catch (err) {
      setChangingPass(false);
      toast.error(err.message || 'Failed to change password.');
    }
  };

  const handleDrag = (e) => {
    e.preventDefault();
    e.stopPropagation();
    if (e.type === 'dragenter' || e.type === 'dragover') {
      setDragActive(true);
    } else if (e.type === 'dragleave') {
      setDragActive(false);
    }
  };

  const handleDrop = (e) => {
    e.preventDefault();
    e.stopPropagation();
    setDragActive(false);

    if (e.dataTransfer.files && e.dataTransfer.files[0]) {
      const file = e.dataTransfer.files[0];
      setSelectedFile(file);
      toast.success(`Selected document: ${file.name}`);
    }
  };

  const handleFileChange = (e) => {
    if (e.target.files && e.target.files[0]) {
      const file = e.target.files[0];
      setSelectedFile(file);
      toast.success(`Selected document: ${file.name}`);
    }
  };

  const handleKycSubmit = async () => {
    if (!selectedFile) {
      toast.error('Please drop or select a document file first.');
      return;
    }

    try {
      await profileService.uploadKyc(selectedFile);
      toast.success('KYC Documents submitted for admin audit review.');
      setSelectedFile(null);
      if (triggerGlobalRefresh) triggerGlobalRefresh();
    } catch (err) {
      toast.error('Failed to upload KYC document.');
    }
  };

  return {
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
  };
};
