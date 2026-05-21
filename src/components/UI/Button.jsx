import React from 'react';
import './Button.css';

const Button = ({
  children,
  onClick,
  type = 'button',
  variant = 'primary', // primary, secondary, outline, danger, success, ghost
  size = 'md', // sm, md, lg
  disabled = false,
  isLoading = false,
  icon: Icon,
  iconPosition = 'left', // left, right
  fullWidth = false,
  className = '',
  ...props
}) => {
  return (
    <button
      type={type}
      className={`btn btn-${variant} btn-${size} ${fullWidth ? 'btn-full' : ''} ${isLoading ? 'btn-loading' : ''} ${className}`}
      onClick={onClick}
      disabled={disabled || isLoading}
      {...props}
    >
      {isLoading && <span className="btn-spinner"></span>}
      {!isLoading && Icon && iconPosition === 'left' && (
        <span className="btn-icon btn-icon-left"><Icon size={size === 'sm' ? 16 : size === 'lg' ? 22 : 19} /></span>
      )}
      <span className="btn-content">{children}</span>
      {!isLoading && Icon && iconPosition === 'right' && (
        <span className="btn-icon btn-icon-right"><Icon size={size === 'sm' ? 16 : size === 'lg' ? 22 : 19} /></span>
      )}
    </button>
  );
};

export default Button;
