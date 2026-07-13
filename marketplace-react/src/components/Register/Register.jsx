import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { addEmployee } from '../../services/api';
import { UserPlus, AlertCircle, Eye, EyeOff, CheckCircle } from 'lucide-react';
import styles from './Register.module.css';

const Register = () => {
  const { register, setSession } = useAuth();
  const navigate = useNavigate();

  const [step, setStep] = useState(1);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [showPass, setShowPass] = useState(false);

  // Step 1: User
  const [userId, setUserId] = useState('');
  const [password, setPassword] = useState('');
  const [createdUser, setCreatedUser] = useState(null);

  // Step 2: Employee
  const [empName, setEmpName] = useState('');
  const [deptName, setDeptName] = useState('');
  const [location, setLocation] = useState('');

  const handleRegisterUser = async (e) => {
    e.preventDefault();
    if (!userId.trim() || !password.trim()) {
      setError('Please fill all fields');
      return;
    }
    setLoading(true);
    setError('');
    try {
      const user = await register({ userId: userId.trim(), password });
      setCreatedUser(user);
      setStep(2);
    } catch (err) {
      setError(err.response?.data?.message || 'Registration failed');
    } finally {
      setLoading(false);
    }
  };

  const handleCreateEmployee = async (e) => {
    e.preventDefault();
    if (!empName.trim()) {
      setError('Name is required');
      return;
    }
    setLoading(true);
    setError('');
    try {
      const empRes = await addEmployee({
        empName: empName.trim(),
        deptName: deptName.trim(),
        location: location.trim(),
        user: createdUser
      });
      // Auto login
      setSession(createdUser, empRes.data);
      navigate('/marketplace');
    } catch (err) {
      setError(err.response?.data?.message || 'Profile creation failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className={styles.page}>
      <div className={styles.orb1} />
      <div className={styles.orb2} />

      <div className={`${styles.card} animate-fade-in`}>
        <div className={styles.header}>
          <div className={styles.logo}>👋</div>
          <h1>{step === 1 ? 'Join the Marketplace' : 'Complete Profile'}</h1>
          <p className="text-secondary">
            {step === 1 ? 'Create your account first' : 'Tell us a bit about yourself'}
          </p>
        </div>

        {/* Stepper */}
        <div className={styles.stepper}>
          <div className={`${styles.step} ${step >= 1 ? styles.active : ''}`}>1</div>
          <div className={`${styles.line} ${step >= 2 ? styles.active : ''}`} />
          <div className={`${styles.step} ${step >= 2 ? styles.active : ''}`}>2</div>
        </div>

        {error && (
          <div className="alert alert-danger mb-4">
            <AlertCircle size={16} /> {error}
          </div>
        )}

        {step === 1 && (
          <form onSubmit={handleRegisterUser} className="animate-fade-in">
            <div className="form-group">
              <label className="form-label" htmlFor="reg-userId">Employee ID / Email *</label>
              <input
                id="reg-userId"
                type="text"
                className="form-control"
                placeholder="e.g. emp001"
                value={userId}
                onChange={e => setUserId(e.target.value)}
                required
              />
            </div>
            <div className="form-group">
              <label className="form-label" htmlFor="reg-password">Password *</label>
              <div className={styles.passWrapper}>
                <input
                  id="reg-password"
                  type={showPass ? 'text' : 'password'}
                  className="form-control"
                  placeholder="Create a password"
                  value={password}
                  onChange={e => setPassword(e.target.value)}
                  required
                />
                <button
                  type="button"
                  className={styles.eyeBtn}
                  onClick={() => setShowPass(!showPass)}
                  tabIndex={-1}
                >
                  {showPass ? <EyeOff size={16} /> : <Eye size={16} />}
                </button>
              </div>
            </div>
            <button type="submit" className="btn btn-primary w-full btn-lg" disabled={loading}>
              {loading ? <span className={styles.spinnerInline} /> : 'Continue →'}
            </button>
          </form>
        )}

        {step === 2 && (
          <form onSubmit={handleCreateEmployee} className="animate-slide-up">
            <div className="alert alert-success mb-4" style={{ padding: '10px' }}>
              <CheckCircle size={16} /> Account created!
            </div>
            <div className="form-group">
              <label className="form-label" htmlFor="emp-name">Full Name *</label>
              <input
                id="emp-name"
                type="text"
                className="form-control"
                placeholder="Your name"
                value={empName}
                onChange={e => setEmpName(e.target.value)}
                required
              />
            </div>
            <div className="form-group">
              <label className="form-label" htmlFor="emp-dept">Department</label>
              <input
                id="emp-dept"
                type="text"
                className="form-control"
                placeholder="e.g. Engineering"
                value={deptName}
                onChange={e => setDeptName(e.target.value)}
              />
            </div>
            <div className="form-group">
              <label className="form-label" htmlFor="emp-loc">Location</label>
              <input
                id="emp-loc"
                type="text"
                className="form-control"
                placeholder="e.g. Bangalore Office"
                value={location}
                onChange={e => setLocation(e.target.value)}
              />
            </div>
            <button type="submit" className="btn btn-primary w-full btn-lg" disabled={loading}>
              {loading ? <span className={styles.spinnerInline} /> : <><UserPlus size={17} /> Finish Registration</>}
            </button>
          </form>
        )}

        {step === 1 && (
          <div className={styles.footer}>
            <p>Already have an account? <Link to="/login">Sign in</Link></p>
          </div>
        )}
      </div>
    </div>
  );
};

export default Register;
