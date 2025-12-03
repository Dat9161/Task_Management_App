import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import api from '../services/api';

const LoginPage = () => {
  const [formData, setFormData] = useState({
    username: '',
    password: ''
  });
  const [errors, setErrors] = useState({});
  const [loading, setLoading] = useState(false);
  const [apiError, setApiError] = useState('');
  
  const navigate = useNavigate();
  const { login } = useAuth();

  const validateForm = () => {
    const newErrors = {};
    
    if (!formData.username.trim()) {
      newErrors.username = 'Username is required';
    }
    
    if (!formData.password) {
      newErrors.password = 'Password is required';
    }
    
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
    // Clear error for this field when user starts typing
    if (errors[name]) {
      setErrors(prev => ({
        ...prev,
        [name]: ''
      }));
    }
    setApiError('');
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!validateForm()) {
      return;
    }
    
    setLoading(true);
    setApiError('');
    
    try {
      const response = await api.post('/auth/login', {
        username: formData.username,
        password: formData.password
      });
      
      // Store JWT token and user data
      const { token, user } = response.data;
      login(token, user);
      
      // Redirect to dashboard on success
      navigate('/dashboard');
    } catch (error) {
      console.error('Login error:', error);
      if (error.response?.data?.message) {
        setApiError(error.response.data.message);
      } else {
        setApiError('Login failed. Please check your credentials and try again.');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={styles.container}>
      <div style={styles.formCard}>
        <h1 style={styles.title}>Login</h1>
        <p style={styles.subtitle}>Sign in to your account</p>
        
        {apiError && (
          <div style={styles.errorAlert}>
            {apiError}
          </div>
        )}
        
        <form onSubmit={handleSubmit} style={styles.form}>
          <div style={styles.formGroup}>
            <label htmlFor="username" style={styles.label}>
              Username
            </label>
            <input
              type="text"
              id="username"
              name="username"
              value={formData.username}
              onChange={handleChange}
              style={{
                ...styles.input,
                ...(errors.username ? styles.inputError : {})
              }}
              disabled={loading}
            />
            {errors.username && (
              <span style={styles.errorText}>{errors.username}</span>
            )}
          </div>
          
          <div style={styles.formGroup}>
            <label htmlFor="password" style={styles.label}>
              Password
            </label>
            <input
              type="password"
              id="password"
              name="password"
              value={formData.password}
              onChange={handleChange}
              style={{
                ...styles.input,
                ...(errors.password ? styles.inputError : {})
              }}
              disabled={loading}
            />
            {errors.password && (
              <span style={styles.errorText}>{errors.password}</span>
            )}
          </div>
          
          <button
            type="submit"
            style={{
              ...styles.button,
              ...(loading ? styles.buttonDisabled : {})
            }}
            disabled={loading}
          >
            {loading ? 'Logging in...' : 'Login'}
          </button>
        </form>
        
        <p style={styles.footer}>
          Don't have an account?{' '}
          <a href="/register" style={styles.link}>
            Register here
          </a>
        </p>
      </div>
    </div>
  );
};

const styles = {
  container: {
    minHeight: '100vh',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: '#f5f5f5',
    padding: '20px'
  },
  formCard: {
    backgroundColor: 'white',
    padding: '40px',
    borderRadius: '8px',
    boxShadow: '0 2px 10px rgba(0,0,0,0.1)',
    width: '100%',
    maxWidth: '400px'
  },
  title: {
    margin: '0 0 10px 0',
    fontSize: '28px',
    fontWeight: 'bold',
    color: '#333',
    textAlign: 'center'
  },
  subtitle: {
    margin: '0 0 30px 0',
    fontSize: '14px',
    color: '#666',
    textAlign: 'center'
  },
  errorAlert: {
    backgroundColor: '#fee',
    color: '#c33',
    padding: '12px',
    borderRadius: '4px',
    marginBottom: '20px',
    fontSize: '14px',
    border: '1px solid #fcc'
  },
  form: {
    display: 'flex',
    flexDirection: 'column'
  },
  formGroup: {
    marginBottom: '20px'
  },
  label: {
    display: 'block',
    marginBottom: '8px',
    fontSize: '14px',
    fontWeight: '500',
    color: '#333'
  },
  input: {
    width: '100%',
    padding: '10px 12px',
    fontSize: '14px',
    border: '1px solid #ddd',
    borderRadius: '4px',
    boxSizing: 'border-box',
    transition: 'border-color 0.2s'
  },
  inputError: {
    borderColor: '#c33'
  },
  errorText: {
    display: 'block',
    marginTop: '6px',
    fontSize: '12px',
    color: '#c33'
  },
  button: {
    width: '100%',
    padding: '12px',
    fontSize: '16px',
    fontWeight: '500',
    color: 'white',
    backgroundColor: '#007bff',
    border: 'none',
    borderRadius: '4px',
    cursor: 'pointer',
    transition: 'background-color 0.2s'
  },
  buttonDisabled: {
    backgroundColor: '#6c757d',
    cursor: 'not-allowed'
  },
  footer: {
    marginTop: '20px',
    textAlign: 'center',
    fontSize: '14px',
    color: '#666'
  },
  link: {
    color: '#007bff',
    textDecoration: 'none',
    fontWeight: '500'
  }
};

export default LoginPage;
