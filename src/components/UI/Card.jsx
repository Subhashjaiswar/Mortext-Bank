import React from 'react';
import './Card.css';

const Card = ({
  children,
  title,
  subtitle,
  action,
  variant = 'default', // default, glass, primary, credit, gradient
  padding = 'normal', // none, compact, normal, loose
  hoverEffect = false,
  className = '',
  onClick,
  ...props
}) => {
  const isClickable = !!onClick;
  
  return (
    <div
      className={`card card-${variant} card-pad-${padding} ${hoverEffect ? 'card-hover' : ''} ${isClickable ? 'card-clickable' : ''} ${className}`}
      onClick={onClick}
      {...props}
    >
      {(title || subtitle || action) && (
        <div className="card-header">
          <div className="card-header-titles">
            {title && <h3 className="card-title">{title}</h3>}
            {subtitle && <p className="card-subtitle">{subtitle}</p>}
          </div>
          {action && <div className="card-action">{action}</div>}
        </div>
      )}
      <div className="card-body">
        {children}
      </div>
    </div>
  );
};

export default Card;
