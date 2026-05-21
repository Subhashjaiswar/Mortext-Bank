import React from 'react';
import './Input.css';

const Input = ({
  label,
  id,
  type = 'text',
  placeholder,
  value,
  onChange,
  error,
  helperText,
  icon: Icon,
  disabled = false,
  required = false,
  className = '',
  ...props
}) => {
  return (
    <div className={`input-group-container ${error ? 'has-error' : ''} ${className}`}>
      {label && (
        <label htmlFor={id} className="input-label">
          {label} {required && <span className="required-star">*</span>}
        </label>
      )}
      <div className="input-wrapper">
        {Icon && (
          <span className="input-icon-left">
            <Icon size={20} />
          </span>
        )}
        <input
          id={id}
          type={type}
          placeholder={placeholder}
          value={value}
          onChange={onChange}
          disabled={disabled}
          required={required}
          className={`input-field ${Icon ? 'with-icon-left' : ''}`}
          {...props}
        />
      </div>
      {error && <span className="input-error-msg">{error}</span>}
      {!error && helperText && <span className="input-helper-msg">{helperText}</span>}
    </div>
  );
};

export default Input;
