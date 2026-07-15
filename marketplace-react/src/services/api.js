import axios from 'axios';

const api = axios.create({
  baseURL: '/api',
  headers: { 'Content-Type': 'application/json' },
  withCredentials: true,
});

// ---- Users ----
export const register   = (user)           => api.post('/users/register', user);
export const login      = (user)           => api.post('/users/login', user);
export const logout     = (user)           => api.post('/users/logout', user);
export const updateUser = (userId, user)   => api.put(`/users/${userId}`, user);
export const deleteUser = (userId)         => api.delete(`/users/${userId}`);
export const deleteCurrentUser = ()         => api.delete('/users/me');

// ---- Employees ----
export const addEmployee                = (emp)    => api.post('/employees', emp);
export const updateEmployee             = (emp)    => api.put('/employees', emp);
export const getEmployee                = (empId)  => api.get(`/employees/${empId}`);
export const getEmployeeByUserId        = (userId) => api.get('/employees/by-user', { params: { userId } });
export const getEmployeeOffers          = (empId)  => api.get(`/employees/${empId}/offers`);
export const getEmployeeRequirements    = (empId)  => api.get(`/employees/${empId}/requirements`);
export const updateOfferAvailability    = (offer)  => api.patch('/employees/offers/availability', offer);
export const updateRequirementFulfilled = (req)    => api.patch('/employees/requirements/fulfilled', req);
export const acceptProposal             = (prop)   => api.patch('/employees/proposals/accepted', prop);

// ---- Requirements ----
export const addRequirement    = (req)           => api.post('/requirements', req);
export const updateRequirement = (req)           => api.put('/requirements', req);
export const getRequirement    = (reqId)         => api.get(`/requirements/${reqId}`);
export const deleteRequirement = (reqId)         => api.delete(`/requirements/${reqId}`);
export const getAllRequirements = (category, type) => {
  const params = {};
  if (category) params.category = category;
  if (type)     params.type     = type;
  return api.get('/requirements', { params });
};

// ---- Offers ----
export const addOffer    = (offer)           => api.post('/offers', offer);
export const updateOffer = (offer)           => api.put('/offers', offer);
export const getOffer    = (offerId)         => api.get(`/offers/${offerId}`);
export const deleteOffer = (offerId)         => api.delete(`/offers/${offerId}`);
export const getAllOffers = (category, type) => {
  const params = {};
  if (category) params.category = category;
  if (type)     params.type     = type;
  return api.get('/offers', { params });
};

// ---- Proposals ----
export const addProposal    = (prop)   => api.post('/proposals', prop);
export const updateProposal = (prop)   => api.put('/proposals', prop);
export const getProposal    = (propId) => api.get(`/proposals/${propId}`);
export const deleteProposal = (propId) => api.delete(`/proposals/${propId}`);
export const getAllProposals = ()       => api.get('/proposals');

// ---- Resources ----
export const getAllResources = (params = {}) => api.get('/resources', { params });

export default api;
