import { createContext, useContext, useReducer, useCallback, useEffect } from 'react';
import { login as apiLogin, logout as apiLogout, register as apiRegister } from '../services/api';

const SESSION_KEY = 'mp_auth';

const loadSession = () => {
  try {
    const raw = localStorage.getItem(SESSION_KEY) || sessionStorage.getItem(SESSION_KEY);
    if (raw) return JSON.parse(raw);
  } catch { /* ignore */ }
  return { user: null, employee: null, isLoggedIn: false };
};

const initialState = loadSession();

const authReducer = (state, action) => {
  switch (action.type) {
    case 'LOGIN':
      return { user: action.user, employee: action.employee, isLoggedIn: true };
    case 'LOGOUT':
      return { user: null, employee: null, isLoggedIn: false };
    case 'UPDATE_EMPLOYEE':
      return { ...state, employee: action.employee };
    default:
      return state;
  }
};

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [state, dispatch] = useReducer(authReducer, initialState);

  useEffect(() => {
    const syncSession = (event) => {
      if (event.key !== SESSION_KEY) return;
      const nextState = event.newValue ? JSON.parse(event.newValue) : null;
      dispatch(nextState?.isLoggedIn
        ? { type: 'LOGIN', user: nextState.user, employee: nextState.employee }
        : { type: 'LOGOUT' });
    };
    window.addEventListener('storage', syncSession);
    return () => window.removeEventListener('storage', syncSession);
  }, []);

  const login = useCallback(async (userId, password) => {
    const res = await apiLogin({ userId, password });
    return res.data;
  }, []);

  const register = useCallback(async (user) => {
    const res = await apiRegister(user);
    return res.data;
  }, []);

  const setSession = useCallback((user, employee) => {
    const newState = { user, employee, isLoggedIn: true };
    dispatch({ type: 'LOGIN', user, employee });
    localStorage.setItem(SESSION_KEY, JSON.stringify(newState));
  }, []);

  const logout = useCallback(async () => {
    if (state.user) {
      await apiLogout().catch(() => {});
    }
    dispatch({ type: 'LOGOUT' });
    localStorage.removeItem(SESSION_KEY);
    sessionStorage.removeItem(SESSION_KEY);
  }, [state.user]);

  const updateEmployee = useCallback((employee) => {
    dispatch({ type: 'UPDATE_EMPLOYEE', employee });
    const current = loadSession();
    localStorage.setItem(SESSION_KEY, JSON.stringify({ ...current, employee }));
  }, []);

  return (
    <AuthContext.Provider value={{ state, login, register, setSession, logout, updateEmployee }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be used inside AuthProvider');
  return ctx;
};
