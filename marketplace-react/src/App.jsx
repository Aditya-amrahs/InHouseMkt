import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import ProtectedRoute from './components/shared/ProtectedRoute';
import Navbar from './components/Navbar/Navbar';
import Login from './components/Login/Login';
import Register from './components/Register/Register';
import Marketplace from './components/Marketplace/Marketplace';
import Dashboard from './components/Dashboard/Dashboard';
import RequirementDetail from './components/RequirementDetail/RequirementDetail';
import RequirementForm from './components/RequirementForm/RequirementForm';
import OfferDetail from './components/OfferDetail/OfferDetail';
import OfferForm from './components/OfferForm/OfferForm';

const App = () => (
  <AuthProvider>
    <BrowserRouter>
      <Navbar />
      <Routes>
        {/* Public routes */}
        <Route path="/" element={<Navigate to="/login" replace />} />
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />

        {/* Protected marketplace routes */}
        <Route path="/marketplace" element={
          <ProtectedRoute><Marketplace /></ProtectedRoute>
        } />
        <Route path="/requirements" element={
          <ProtectedRoute><Marketplace defaultView="requirements" /></ProtectedRoute>
        } />
        <Route path="/offers" element={
          <ProtectedRoute><Marketplace defaultView="offers" /></ProtectedRoute>
        } />

        {/* Protected detail & form routes */}
        <Route path="/requirements/:id" element={
          <ProtectedRoute><RequirementDetail /></ProtectedRoute>
        } />
        <Route path="/requirements/new" element={
          <ProtectedRoute><RequirementForm /></ProtectedRoute>
        } />
        <Route path="/requirements/:id/edit" element={
          <ProtectedRoute><RequirementForm /></ProtectedRoute>
        } />

        <Route path="/offers/:id" element={
          <ProtectedRoute><OfferDetail /></ProtectedRoute>
        } />
        <Route path="/offers/new" element={
          <ProtectedRoute><OfferForm /></ProtectedRoute>
        } />
        <Route path="/offers/:id/edit" element={
          <ProtectedRoute><OfferForm /></ProtectedRoute>
        } />

        {/* Protected dashboard */}
        <Route path="/dashboard" element={
          <ProtectedRoute><Dashboard /></ProtectedRoute>
        } />

        {/* Catch-all → login */}
        <Route path="*" element={<Navigate to="/login" replace />} />
      </Routes>
    </BrowserRouter>
  </AuthProvider>
);

export default App;
