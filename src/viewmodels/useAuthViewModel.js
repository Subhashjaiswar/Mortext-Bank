import { useState } from 'react';
import { useAuth } from '../hooks/useAuth';
import { useToast } from '../components/UI/Toast';
import { authService } from '../services/authService';

export const useAuthViewModel = () => {
  const { login: setGlobalUser } = useAuth();
  const toast = useToast();

  // Auth view: 'login', 'register', 'forgot', 'otp', 'reset'
  const [view, setView] = useState('login');

  // Form States
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [name, setName] = useState('');
  const [otp, setOtp] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [errors, setErrors] = useState({});

  const validateEmail = (val) => {
    return /\S+@\S+\.\S+/.test(val);
  };

  const handleLogin = async (e) => {
    e.preventDefault();
    setErrors({});

    if (!email) {
      setErrors((prev) => ({ ...prev, email: 'Email is required' }));
      return;
    }
    if (!validateEmail(email)) {
      setErrors((prev) => ({ ...prev, email: 'Please enter a valid email' }));
      return;
    }
    if (!password) {
      setErrors((prev) => ({ ...prev, password: 'Password is required' }));
      return;
    }

    setIsLoading(true);
    try {
      const user = await authService.login(email, password);
      setGlobalUser(user); // Set global session user
      toast.success(`Welcome back, ${user.name}!`);
    } catch (err) {
      toast.error(err.message || 'Login failed.');
    } finally {
      setIsLoading(false);
    }
  };

  const handleRegister = async (e) => {
    e.preventDefault();
    setErrors({});

    if (!name) {
      setErrors((prev) => ({ ...prev, name: 'Full Name is required' }));
      return;
    }
    if (!email) {
      setErrors((prev) => ({ ...prev, email: 'Email is required' }));
      return;
    }
    if (!validateEmail(email)) {
      setErrors((prev) => ({ ...prev, email: 'Please enter a valid email' }));
      return;
    }
    if (!password) {
      setErrors((prev) => ({ ...prev, password: 'Password is required' }));
      return;
    }
    if (password.length < 6) {
      setErrors((prev) => ({ ...prev, password: 'Password must be at least 6 characters' }));
      return;
    }

    setIsLoading(true);
    try {
      const user = await authService.register(name, email, password);
      setGlobalUser(user); // Set global session user
      toast.success('Registration successful! Welcome to Mortext Bank.');
    } catch (err) {
      toast.error(err.message || 'Registration failed.');
    } finally {
      setIsLoading(false);
    }
  };

  const handleForgot = async (e) => {
    e.preventDefault();
    setErrors({});

    if (!email) {
      setErrors((prev) => ({ ...prev, email: 'Email is required' }));
      return;
    }
    if (!validateEmail(email)) {
      setErrors((prev) => ({ ...prev, email: 'Please enter a valid email' }));
      return;
    }

    setIsLoading(true);
    try {
      await authService.forgotPassword(email);
      setView('otp');
      toast.info('OTP code sent! Check your inbox (1234).');
    } catch (err) {
      toast.error(err.message || 'Failed to request reset OTP.');
    } finally {
      setIsLoading(false);
    }
  };

  const handleOtp = async (e) => {
    e.preventDefault();
    setIsLoading(true);
    try {
      await authService.verifyOtp(email, otp);
      setView('reset');
      toast.success('OTP Verified successfully!');
    } catch (err) {
      toast.error(err.message || 'Invalid OTP.');
    } finally {
      setIsLoading(false);
    }
  };

  const handleReset = async (e) => {
    e.preventDefault();
    setErrors({});

    if (!password) {
      setErrors((prev) => ({ ...prev, password: 'Security Pin is required' }));
      return;
    }
    if (password.length < 4 || password.length > 6) {
      setErrors((prev) => ({ ...prev, password: 'Security Pin must be between 4 and 6 digits' }));
      return;
    }
    if (!/^\d{4,6}$/.test(password)) {
      setErrors((prev) => ({ ...prev, password: 'Security Pin must be numeric' }));
      return;
    }
    if (password !== confirmPassword) {
      setErrors((prev) => ({ ...prev, confirmPassword: 'Security Pins do not match' }));
      return;
    }

    setIsLoading(true);
    try {
      await authService.resetPassword(email, otp, password);
      setView('login');
      toast.success('Security Pin changed successfully! Please log in.');
    } catch (err) {
      toast.error(err.message || 'Failed to reset Security Pin.');
    } finally {
      setIsLoading(false);
    }
  };

  return {
    view,
    setView,
    email,
    setEmail,
    password,
    setPassword,
    confirmPassword,
    setConfirmPassword,
    name,
    setName,
    otp,
    setOtp,
    isLoading,
    errors,
    handleLogin,
    handleRegister,
    handleForgot,
    handleOtp,
    handleReset,
  };
};
