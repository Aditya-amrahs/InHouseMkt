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
        <Route path="/" element={<Navigate to="/marketplace" replace />} />
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/marketplace" element={<Marketplace />} />
        <Route path="/requirements" element={<Marketplace defaultView="requirements" />} />
        <Route path="/offers" element={<Marketplace defaultView="offers" />} />

        <Route path="/requirements/:id" element={<RequirementDetail />} />
        <Route path="/requirements/new" element={
          <ProtectedRoute><RequirementForm /></ProtectedRoute>
        } />
        <Route path="/requirements/:id/edit" element={
          <ProtectedRoute><RequirementForm /></ProtectedRoute>
        } />

        <Route path="/offers/:id" element={<OfferDetail />} />
        <Route path="/offers/new" element={
          <ProtectedRoute><OfferForm /></ProtectedRoute>
        } />
        <Route path="/offers/:id/edit" element={
          <ProtectedRoute><OfferForm /></ProtectedRoute>
        } />

        <Route path="/dashboard" element={
          <ProtectedRoute><Dashboard /></ProtectedRoute>
        } />

        <Route path="*" element={<Navigate to="/marketplace" replace />} />
      </Routes>
    </BrowserRouter>
  </AuthProvider>
);

export default App;
