import React from 'react';
import './Badge.css';

const Badge = ({
  children,
  variant = 'primary', // primary, secondary, success, danger, warning, info
  size = 'md', // sm, md
  rounded = true,
  className = '',
  ...props
}) => {
  return (
    <span
      className={`badge badge-${variant} badge-${size} ${rounded ? 'badge-rounded' : ''} ${className}`}
      {...props}
    >
      {children}
    </span>
  );
};

export default Badge;
