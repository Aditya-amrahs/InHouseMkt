import { useState, useEffect, useCallback } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { getEmployeeRequirements, getEmployeeOffers, getAllProposals, updateRequirementFulfilled, updateOfferAvailability, deleteRequirement, deleteOffer, deleteProposal, acceptProposal, deleteCurrentUser } from '../../services/api';
import { ClipboardList, PackageOpen, Handshake, Plus, Trash2, Edit2, CheckCircle, XCircle, AlertTriangle } from 'lucide-react';
import Spinner from '../shared/Spinner';
import Badge from '../shared/Badge';
import EmptyState from '../shared/EmptyState';
import styles from './Dashboard.module.css';

const Dashboard = () => {
  const { state, logout } = useAuth();
  const navigate = useNavigate();
  const empId = state.employee?.empId;

  const [activeTab, setActiveTab] = useState('requirements');
  const [loading, setLoading] = useState(true);
  
  const [requirements, setRequirements] = useState([]);
  const [offers, setOffers] = useState([]);
  const [proposals, setProposals] = useState([]);
  const [receivedProposals, setReceivedProposals] = useState([]);
  const [loadWarning, setLoadWarning] = useState(false);

  const loadData = useCallback(async () => {
    if (!empId) {
      setLoading(false);
      return;
    }
    setLoading(true);
    try {
      const results = await Promise.allSettled([
        getEmployeeRequirements(empId),
        getEmployeeOffers(empId),
        getAllProposals()
      ]);
      const [reqsResult, offersResult, propsResult] = results;
      const reqs = reqsResult.status === 'fulfilled' && Array.isArray(reqsResult.value.data) ? reqsResult.value.data : [];
      const offers = offersResult.status === 'fulfilled' && Array.isArray(offersResult.value.data) ? offersResult.value.data : [];
      const props = propsResult.status === 'fulfilled' && Array.isArray(propsResult.value.data) ? propsResult.value.data : [];
      setRequirements(reqs);
      setOffers(offers);
      setProposals(props.filter(p => p.emp?.empId === empId));
      setReceivedProposals(props.filter(p => p.resource?.emp?.empId === empId));
      setLoadWarning(results.some(result => result.status === 'rejected'));
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  }, [empId]);

  useEffect(() => {
    loadData();
  }, [loadData]);

  // Actions
  const handleMarkFulfilled = async (req) => {
    try {
      const res = await updateRequirementFulfilled(req);
      setRequirements(requirements.map(r => r.resId === res.data.resId ? res.data : r));
    } catch (err) { console.error(err); }
  };

  const handleMarkUnavailable = async (offer) => {
    try {
      const updated = { ...offer, available: false };
      const res = await updateOfferAvailability(updated);
      setOffers(offers.map(o => o.resId === res.data.resId ? res.data : o));
    } catch (err) { console.error(err); }
  };

  const handleDeleteReq = async (id) => {
    if (!window.confirm('Delete this requirement?')) return;
    try {
      await deleteRequirement(id);
      setRequirements(requirements.filter(r => r.resId !== id));
    } catch (err) { console.error(err); }
  };

  const handleDeleteOffer = async (id) => {
    if (!window.confirm('Delete this offer?')) return;
    try {
      await deleteOffer(id);
      setOffers(offers.filter(o => o.resId !== id));
    } catch (err) { console.error(err); }
  };

  const handleDeleteProp = async (id) => {
    if (!window.confirm('Delete this proposal?')) return;
    try {
      await deleteProposal(id);
      setProposals(proposals.filter(p => p.propId !== id));
    } catch (err) { console.error(err); }
  };

  const handleAcceptProposal = async (id) => {
    try {
      const res = await acceptProposal({ propId: id });
      setReceivedProposals(receivedProposals.map(p => p.propId === id ? res.data : p));
    } catch (err) {
      console.error(err);
    }
  };

  const handleDeleteAccount = async () => {
    if (!window.confirm('Delete your account and all of your marketplace posts? This cannot be undone.')) return;
    try {
      await deleteCurrentUser();
      await logout();
      navigate('/login');
    } catch (err) {
      console.error(err);
      window.alert(err.response?.data?.message || 'Unable to delete your account.');
    }
  };

  if (!empId) {
    return <div className="page-content"><div className="container"><EmptyState icon={AlertTriangle} message="Employee profile not found." /></div></div>;
  }

  return (
    <div className="page-content">
      <div className="container">
        
        {/* Header */}
        <div className={`section-header animate-fade-in ${styles.header}`}>
          <div>
            <h1 className={styles.title}>My Dashboard</h1>
            <p className="text-muted">Manage your requirements, offers and proposals</p>
          </div>
          <div className={styles.actions}>
            <Link to="/requirements/new" className="btn btn-primary">
              <Plus size={16} /> New Requirement
            </Link>
            <Link to="/offers/new" className="btn btn-accent">
              <Plus size={16} /> New Offer
            </Link>
            <button type="button" className="btn btn-danger" onClick={handleDeleteAccount}>
              <Trash2 size={16} /> Delete Account
            </button>
          </div>
        </div>

        {loading ? (
          <Spinner />
        ) : (
          <div className="animate-slide-up">
            {loadWarning && (
              <div className="alert alert-danger mb-4">
                Some dashboard data could not be loaded. Available sections are still shown.
              </div>
            )}
            {/* Stats Row */}
            <div className={styles.statsRow}>
              <div 
                className={`card ${styles.statCard} ${activeTab === 'requirements' ? styles.active : ''}`}
                onClick={() => setActiveTab('requirements')}
              >
                <ClipboardList className={styles.statIcon} size={28} color="var(--primary-light)" />
                <div className={styles.statCount}>{requirements.length}</div>
                <div className={styles.statLabel}>Requirements</div>
              </div>

              <div 
                className={`card ${styles.statCard} ${activeTab === 'offers' ? styles.active : ''}`}
                onClick={() => setActiveTab('offers')}
              >
                <PackageOpen className={styles.statIcon} size={28} color="var(--accent)" />
                <div className={styles.statCount}>{offers.length}</div>
                <div className={styles.statLabel}>Offers</div>
              </div>

              <div 
                className={`card ${styles.statCard} ${activeTab === 'proposals' ? styles.active : ''}`}
                onClick={() => setActiveTab('proposals')}
              >
                <Handshake className={styles.statIcon} size={28} color="var(--success)" />
                <div className={styles.statCount}>{proposals.length}</div>
                <div className={styles.statLabel}>My Proposals</div>
              </div>
            </div>

            {/* Tab Content */}
            <div className={styles.tabContent}>
              {activeTab === 'requirements' && (
                <>
                  {requirements.length === 0 ? (
                    <EmptyState icon={ClipboardList} message="No requirements yet. Post your first requirement!" />
                  ) : (
                    <div className="grid-auto animate-fade-in">
                      {requirements.map(req => (
                        <div key={req.resId} className="card flex-col justify-between">
                          <div>
                            <div className="flex gap-2 mb-4">
                              <Badge variant="primary">{req.category}</Badge>
                              {req.fulfilled 
                                ? <Badge variant="success">Fulfilled</Badge>
                                : <Badge variant="warning">Open</Badge>
                              }
                            </div>
                            <h3 className="mb-4">{req.title}</h3>
                            <p className="text-muted text-sm mb-4 line-clamp-2">{req.description}</p>
                          </div>
                          
                          <div className={styles.cardActions}>
                            {!req.fulfilled && (
                              <button className="btn btn-success btn-sm" onClick={() => handleMarkFulfilled(req)}>
                                <CheckCircle size={14} /> Fulfill
                              </button>
                            )}
                            <Link to={`/requirements/${req.resId}/edit`} className="btn btn-outline btn-sm">
                              <Edit2 size={14} /> Edit
                            </Link>
                            <button className="btn btn-danger btn-sm" onClick={() => handleDeleteReq(req.resId)}>
                              <Trash2 size={14} />
                            </button>
                          </div>
                        </div>
                      ))}
                    </div>
                  )}
                </>
              )}

              {activeTab === 'offers' && (
                <>
                  {offers.length === 0 ? (
                    <EmptyState icon={PackageOpen} message="No offers yet. Post your first offer!" />
                  ) : (
                    <div className="grid-auto animate-fade-in">
                      {offers.map(offer => (
                        <div key={offer.resId} className="card flex-col justify-between">
                          <div>
                            <div className="flex gap-2 mb-4">
                              <Badge variant="primary">{offer.category}</Badge>
                              {offer.available 
                                ? <Badge variant="success">Available</Badge>
                                : <Badge variant="danger">Unavailable</Badge>
                              }
                            </div>
                            <h3 className="mb-4">{offer.title}</h3>
                            <p className="text-muted text-sm mb-4 line-clamp-2">{offer.description}</p>
                          </div>
                          
                          <div className={styles.cardActions}>
                            {offer.available && (
                              <button className="btn btn-outline btn-sm" onClick={() => handleMarkUnavailable(offer)}>
                                <XCircle size={14} /> Mark Unavailable
                              </button>
                            )}
                            <Link to={`/offers/${offer.resId}/edit`} className="btn btn-outline btn-sm">
                              <Edit2 size={14} /> Edit
                            </Link>
                            <button className="btn btn-danger btn-sm" onClick={() => handleDeleteOffer(offer.resId)}>
                              <Trash2 size={14} />
                            </button>
                          </div>
                        </div>
                      ))}
                    </div>
                  )}
                </>
              )}

              {activeTab === 'proposals' && (
                <>
                  {proposals.length === 0 ? (
                    <EmptyState icon={Handshake} message="You haven't submitted any proposals yet." />
                  ) : (
                    <div className="grid-auto animate-fade-in">
                      {proposals.map(prop => (
                        <div key={prop.propId} className="card flex-col justify-between">
                          <div>
                            <div className="flex gap-2 mb-4">
                              {prop.accepted 
                                ? <Badge variant="success">Accepted</Badge>
                                : <Badge variant="warning">Pending</Badge>
                              }
                            </div>
                            <p className="mb-4">"{prop.proposal}"</p>
                            <div className="text-sm text-secondary mb-4">
                              <div>For: {prop.resource?.title || 'Marketplace resource'}</div>
                              <div>Amount: ₹{prop.amount.toLocaleString()}</div>
                              <div>Date: {new Date(prop.proposalDate).toLocaleDateString()}</div>
                            </div>
                          </div>
                          <div className={styles.cardActions}>
                            <button className="btn btn-danger btn-sm" onClick={() => handleDeleteProp(prop.propId)}>
                              <Trash2 size={14} /> Withdraw
                            </button>
                          </div>
                        </div>
                      ))}
                    </div>
                  )}
                  {receivedProposals.length > 0 && (
                    <div className="mt-8">
                      <h3 className="mb-4">Proposals Received</h3>
                      <div className="grid-auto animate-fade-in">
                        {receivedProposals.map(prop => (
                          <div key={`received-${prop.propId}`} className="card flex-col justify-between">
                            <div>
                              <div className="flex gap-2 mb-4">
                                {prop.accepted ? <Badge variant="success">Accepted</Badge> : <Badge variant="warning">Pending</Badge>}
                              </div>
                              <p className="mb-2">"{prop.proposal}"</p>
                              <div className="text-sm text-secondary">From: {prop.emp?.empName || 'Employee'}</div>
                            </div>
                            {!prop.accepted && (
                              <button className="btn btn-success btn-sm mt-4" onClick={() => handleAcceptProposal(prop.propId)}>
                                <CheckCircle size={14} /> Accept Proposal
                              </button>
                            )}
                          </div>
                        ))}
                      </div>
                    </div>
                  )}
                </>
              )}
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default Dashboard;
