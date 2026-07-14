import { useState } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { getEmployeeByUserId } from '../../services/api';
import { LogIn, Eye, EyeOff, AlertCircle, Store } from 'lucide-react';
import styles from './Login.module.css';

const Login = () => {
  const { login, setSession } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const from = location.state?.from?.pathname || '/marketplace';

  const [userId, setUserId]     = useState('');
  const [password, setPassword] = useState('');
  const [showPass, setShowPass] = useState(false);
  const [loading, setLoading]   = useState(false);
  const [error, setError]       = useState('');

  const onSubmit = async (e) => {
    e.preventDefault();
    if (!userId.trim() || !password.trim()) {
      setError('Please enter your Employee ID and password.');
      return;
    }
    setLoading(true);
    setError('');
    try {
      const user = await login(userId.trim(), password);
      // Fetch linked employee
      let employee = null;
      try {
        const empRes = await getEmployeeByUserId(userId.trim());
        employee = empRes.data;
      } catch { /* no employee yet */ }
      setSession(user, employee);
      navigate(from, { replace: true });
    } catch (err) {
      setError(err.response?.data?.message || 'Invalid credentials. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className={styles.page}>
      {/* Background orbs */}
      <div className={styles.orb1} />
      <div className={styles.orb2} />

      <div className={`${styles.card} animate-fade-in`}>
        <div className={styles.header}>
          <div className={styles.logoWrap}><Store size={24} strokeWidth={1.75} /></div>
          <h1>Welcome Back</h1>
          <p>Sign in to your InHouseMarket account</p>
        </div>

        {error && (
          <div className="alert alert-danger">
            <AlertCircle size={16} /> {error}
          </div>
        )}

        <form id="login-form" onSubmit={onSubmit}>
          <div className="form-group">
            <label className="form-label" htmlFor="login-userId">Employee ID / Email</label>
            <input
              id="login-userId"
              type="text"
              className="form-control"
              placeholder="e.g. emp001@company.com"
              value={userId}
              onChange={e => setUserId(e.target.value)}
              autoComplete="username"
              required
            />
          </div>

          <div className="form-group">
            <label className="form-label" htmlFor="login-password">Password</label>
            <div className={styles.passWrapper}>
              <input
                id="login-password"
                type={showPass ? 'text' : 'password'}
                className="form-control"
                placeholder="Enter your password"
                value={password}
                onChange={e => setPassword(e.target.value)}
                autoComplete="current-password"
                required
              />
              <button
                type="button"
                className={styles.eyeBtn}
                onClick={() => setShowPass(!showPass)}
                tabIndex={-1}
                aria-label="Toggle password visibility"
              >
                {showPass ? <EyeOff size={16} /> : <Eye size={16} />}
              </button>
            </div>
          </div>

          <button
            id="login-submit"
            type="submit"
            className="btn btn-primary w-full btn-lg"
            disabled={loading}
          >
            {loading ? (
              <><span className={styles.spinnerInline} /> Signing in...</>
            ) : (
              <><LogIn size={17} /> Sign In</>
            )}
          </button>
        </form>

        <div className={styles.footer}>
          <p>Don&apos;t have an account?{' '}
            <Link to="/register">Register here</Link>
          </p>
        </div>
      </div>
    </div>
  );
};

export default Login;
