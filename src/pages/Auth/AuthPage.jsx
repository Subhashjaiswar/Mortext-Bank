import React from 'react';
import { Mail, Lock, User, ShieldAlert, KeyRound, ArrowRight, TrendingUp } from 'lucide-react';
import { useAuthViewModel } from '../../viewmodels/useAuthViewModel';
import Input from '../../components/UI/Input';
import Button from '../../components/UI/Button';
import './AuthPage.css';

const AuthPage = () => {
  const {
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
  } = useAuthViewModel();

  return (
    <div className="auth-page-container">
      {/* Left side visual graphic advertisement banner */}
      <div className="auth-visual-panel">
        <div className="visual-overlay"></div>
        <div className="visual-content">
          <div className="visual-logo">
            <TrendingUp size={36} />
            <h2>Mortext Bank</h2>
          </div>
          <div className="visual-main-text">
            <h1>Unlocking the Power of Digital Finance.</h1>
            <p>Experience seamless global money transfers, advanced card analytics, premium security features, and absolute control over your earnings, all under one unified platform.</p>
          </div>
          <div className="visual-features">
            <div className="v-feature-item">
              <span className="v-feat-badge">Instant</span>
              <p>QR and UPI-enabled global payments</p>
            </div>
            <div className="v-feature-item">
              <span className="v-feat-badge">Secure</span>
              <p>Biometric authentication and card freezing</p>
            </div>
            <div className="v-feature-item">
              <span className="v-feat-badge">Refined</span>
              <p>Custom analytics for intelligent expense planning</p>
            </div>
          </div>
          <div className="visual-footer">
            <span>© 2026 Mortext Bank. All rights reserved.</span>
          </div>
        </div>
      </div>

      {/* Right side form card */}
      <div className="auth-form-panel">
        <div className="auth-card-wrapper glass animate-scale-in">

          {/* LOGIN SCREEN */}
          {view === 'login' && (
            <form onSubmit={handleLogin} className="auth-form">
              <div className="auth-header-sec">
                <h2>Welcome Back</h2>
                <p>Log in to access your secure bank dashboard</p>
              </div>
              <Input
                label="Email Address"
                id="login-email"
                type="email"
                placeholder="you@example.com"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                icon={Mail}
                error={errors.email}
              />
              <Input
                label="Security Password"
                id="login-password"
                type="password"
                placeholder="••••••••"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                icon={Lock}
                error={errors.password}
              />
              <div className="forgot-password-link">
                <button type="button" className="text-btn" onClick={() => setView('forgot')}>
                  Forgot your security pin?
                </button>
              </div>
              <Button type="submit" isLoading={isLoading} fullWidth icon={ArrowRight} iconPosition="right">
                Log In Securely
              </Button>
              <div className="auth-bottom-switch">
                <span>New to Mortext?</span>
                <button type="button" className="switch-view-btn" onClick={() => setView('register')}>
                  Create account
                </button>
              </div>
            </form>
          )}

          {/* REGISTER SCREEN */}
          {view === 'register' && (
            <form onSubmit={handleRegister} className="auth-form">
              <div className="auth-header-sec">
                <h2>Create Account</h2>
                <p>Join millions of smarter global savers today</p>
              </div>
              <Input
                label="Full Name"
                id="reg-name"
                placeholder="Alex Carter"
                value={name}
                onChange={(e) => setName(e.target.value)}
                icon={User}
                error={errors.name}
              />
              <Input
                label="Email Address"
                id="reg-email"
                type="email"
                placeholder="you@example.com"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                icon={Mail}
                error={errors.email}
              />
              <Input
                label="Security Password"
                id="reg-password"
                type="password"
                placeholder="•••••••• (Min 6 chars)"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                icon={Lock}
                error={errors.password}
              />
              <Button type="submit" isLoading={isLoading} fullWidth>
                Register & Open Account
              </Button>
              <div className="auth-bottom-switch">
                <span>Already have an account?</span>
                <button type="button" className="switch-view-btn" onClick={() => setView('login')}>
                  Sign In
                </button>
              </div>
            </form>
          )}

          {/* FORGOT PASSWORD SCREEN */}
          {view === 'forgot' && (
            <form onSubmit={handleForgot} className="auth-form">
              <div className="auth-header-sec">
                <h2>Reset Security Pin</h2>
                <p>Enter email to receive your OTP authorization code</p>
              </div>
              <Input
                label="Registered Email"
                id="forgot-email"
                type="email"
                placeholder="you@example.com"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                icon={Mail}
                error={errors.email}
              />
              <Button type="submit" isLoading={isLoading} fullWidth>
                Send OTP Verification Code
              </Button>
              <div className="auth-bottom-switch">
                <button type="button" className="switch-view-btn back-btn" onClick={() => setView('login')}>
                  Back to Login
                </button>
              </div>
            </form>
          )}

          {/* OTP VERIFICATION SCREEN */}
          {view === 'otp' && (
            <form onSubmit={handleOtp} className="auth-form">
              <div className="auth-header-sec">
                <h2>Verify Identity</h2>
                <p>Enter the 6-digit code sent to your email inbox</p>
              </div>
              <div className="otp-demo-hint">
                <ShieldAlert size={16} />
                <span>Check your email inbox or backend logs for the OTP code.</span>
              </div>
              <Input
                label="One-Time PIN (OTP)"
                id="otp-code"
                placeholder="••••••"
                maxLength={6}
                value={otp}
                onChange={(e) => setOtp(e.target.value.replace(/\D/g, ''))}
                icon={KeyRound}
              />
              <Button type="submit" isLoading={isLoading} fullWidth>
                Confirm Verification
              </Button>
              <div className="auth-bottom-switch">
                <button type="button" className="switch-view-btn back-btn" onClick={() => setView('forgot')}>
                  Resend Code
                </button>
              </div>
            </form>
          )}

          {/* RESET PASSWORD SCREEN */}
          {view === 'reset' && (
            <form onSubmit={handleReset} className="auth-form">
              <div className="auth-header-sec">
                <h2>New Security Pin</h2>
                <p>Configure a new secure numeric pin for your account</p>
              </div>
              <Input
                label="New Security Pin"
                id="reset-pass"
                type="password"
                placeholder="•••• (4-6 digits)"
                maxLength={6}
                value={password}
                onChange={(e) => setPassword(e.target.value.replace(/\D/g, ''))}
                icon={Lock}
                error={errors.password}
              />
              <Input
                label="Confirm New Security Pin"
                id="reset-confirm"
                type="password"
                placeholder="••••"
                maxLength={6}
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value.replace(/\D/g, ''))}
                icon={Lock}
                error={errors.confirmPassword}
              />
              <Button type="submit" isLoading={isLoading} fullWidth>
                Save & Login
              </Button>
            </form>
          )}

        </div>
      </div>
    </div>
  );
};

export default AuthPage;
