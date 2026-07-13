import { useState, useEffect } from 'react';
import { Link, NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { ShoppingBag, LayoutDashboard, LogOut, LogIn, UserPlus, Menu, X, ChevronRight } from 'lucide-react';
import styles from './Navbar.module.css';

const Navbar = () => {
  const { state, logout } = useAuth();
  const navigate = useNavigate();
  const [menuOpen, setMenuOpen] = useState(false);
  const [scrolled, setScrolled] = useState(false);

  useEffect(() => {
    const handleScroll = () => setScrolled(window.scrollY > 20);
    window.addEventListener('scroll', handleScroll);
    return () => window.removeEventListener('scroll', handleScroll);
  }, []);

  const handleLogout = () => {
    logout();
    navigate('/marketplace');
    setMenuOpen(false);
  };

  const closeMenu = () => setMenuOpen(false);

  return (
    <nav className={`${styles.navbar} ${scrolled ? styles.scrolled : ''}`}>
      <div className={styles.container}>
        {/* Brand */}
        <Link to="/marketplace" className={styles.brand} onClick={closeMenu}>
          <span className={styles.brandIcon}>🏪</span>
          <span className={styles.brandName}>
            InHouse<span className={styles.brandAccent}>Market</span>
          </span>
        </Link>

        {/* Desktop Links */}
        <ul className={`${styles.links} ${menuOpen ? styles.open : ''}`}>
          <li>
            <NavLink to="/marketplace" className={({ isActive }) => isActive ? styles.active : ''} onClick={closeMenu}>
              🔍 Browse
            </NavLink>
          </li>
          <li>
            <NavLink to="/requirements" className={({ isActive }) => isActive ? styles.active : ''} onClick={closeMenu}>
              📋 Requirements
            </NavLink>
          </li>
          <li>
            <NavLink to="/offers" className={({ isActive }) => isActive ? styles.active : ''} onClick={closeMenu}>
              📦 Offers
            </NavLink>
          </li>
          {state.isLoggedIn && (
            <li>
              <NavLink to="/dashboard" className={({ isActive }) => isActive ? styles.active : ''} onClick={closeMenu}>
                <LayoutDashboard size={15} style={{ verticalAlign: 'middle' }} /> Dashboard
              </NavLink>
            </li>
          )}
        </ul>

        {/* Auth section */}
        <div className={styles.auth}>
          {state.isLoggedIn ? (
            <>
              <span className={styles.userName}>
                Hi, {state.employee?.empName || state.user?.userId}
              </span>
              <button className="btn btn-outline btn-sm" onClick={handleLogout}>
                <LogOut size={14} /> Logout
              </button>
            </>
          ) : (
            <>
              <Link to="/login" className="btn btn-outline btn-sm" onClick={closeMenu}>
                <LogIn size={14} /> Login
              </Link>
              <Link to="/register" className="btn btn-primary btn-sm" onClick={closeMenu}>
                <UserPlus size={14} /> Register
              </Link>
            </>
          )}
        </div>

        {/* Hamburger */}
        <button
          className={styles.hamburger}
          onClick={() => setMenuOpen(!menuOpen)}
          aria-label={menuOpen ? 'Close menu' : 'Open menu'}
        >
          {menuOpen ? <X size={22} /> : <Menu size={22} />}
        </button>
      </div>
    </nav>
  );
};

export default Navbar;
