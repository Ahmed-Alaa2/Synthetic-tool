import React, { useState } from 'react';
import './ConnectionForm.css'; // make sure this file is imported

export default function ConnectionForm({ onConnect }) {
  const [fields, setFields] = useState({
    host: '',
    port: '',
    username: '',
    password: '',
  });
  const [errors, setErrors] = useState({});
  const [touched, setTouched] = useState({});

  const validate = () => {
    const errs = {};
    if (!fields.host.trim()) errs.host = 'Server host is required';
    if (!fields.port.trim()) {
      errs.port = 'Port is required';
    } else if (!/^\d+$/.test(fields.port)) {
      errs.port = 'Port must be a number';
    } else if (+fields.port < 1 || +fields.port > 65535) {
      errs.port = 'Port must be 1–65535';
    }
    if (!fields.username.trim()) errs.username = 'Username is required';
    if (!fields.password) errs.password = 'Password is required';
    setErrors(errs);
    return Object.keys(errs).length === 0;
  };

  const handleBlur = (e) => {
    setTouched(t => ({ ...t, [e.target.name]: true }));
    validate();
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFields(f => ({ ...f, [name]: value }));
    if (touched[name]) validate();
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (validate()) onConnect(fields);
  };

  return (
    <div className="connection-card">
      <form className="connection-form" onSubmit={handleSubmit} noValidate>
        <h3 className="form-title">Connect to SQL Server</h3>

        <div className="input-group">
          <label htmlFor="host">Server Host</label>
          <input
            id="host"
            name="host"
            type="text"
            placeholder="e.g. 192.168.1.1"
            value={fields.host}
            onChange={handleChange}
            onBlur={handleBlur}
            className={errors.host ? 'invalid' : ''}
          />
          {errors.host && <div className="error-message">{errors.host}</div>}
        </div>

        <div className="input-group">
          <label htmlFor="port">Port</label>
          <input
            id="port"
            name="port"
            type="text"
            placeholder="1433"
            value={fields.port}
            onChange={handleChange}
            onBlur={handleBlur}
            className={errors.port ? 'invalid' : ''}
          />
          {errors.port && <div className="error-message">{errors.port}</div>}
        </div>

        <div className="input-group">
          <label htmlFor="username">Username</label>
          <input
            id="username"
            name="username"
            type="text"
            placeholder="db_user"
            value={fields.username}
            onChange={handleChange}
            onBlur={handleBlur}
            className={errors.username ? 'invalid' : ''}
          />
          {errors.username && (
            <div className="error-message">{errors.username}</div>
          )}
        </div>

        <div className="input-group">
          <label htmlFor="password">Password</label>
          <input
            id="password"
            name="password"
            type="password"
            placeholder="••••••••"
            value={fields.password}
            onChange={handleChange}
            onBlur={handleBlur}
            className={errors.password ? 'invalid' : ''}
          />
          {errors.password && (
            <div className="error-message">{errors.password}</div>
          )}
        </div>

        <button type="submit" className="connect-btn">
          Connect
        </button>
      </form>
    </div>
  );
}
