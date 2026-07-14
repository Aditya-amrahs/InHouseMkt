import { useState, useEffect, useCallback } from 'react';
import { useParams, Link } from 'react-router-dom';
import { getOffer, addProposal } from '../../services/api';
import { useAuth } from '../../context/AuthContext';
import { ArrowLeft, CheckCircle, XCircle, Send, Handshake, Lock, Inbox, AlertCircle } from 'lucide-react';
import Spinner from '../shared/Spinner';
import Badge from '../shared/Badge';
import EmptyState from '../shared/EmptyState';

const OfferDetail = () => {
  const { id } = useParams();
  const { state } = useAuth();
  const [offer, setOffer] = useState(null);
  const [loading, setLoading] = useState(true);
  
  // Proposal Form
  const [proposalText, setProposalText] = useState('');
  const [proposalAmount, setProposalAmount] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const [msg, setMsg] = useState({ type: '', text: '' });

  const fetchOffer = useCallback(async () => {
    try {
      const res = await getOffer(id);
      setOffer(res.data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  }, [id]);

  useEffect(() => {
    fetchOffer();
  }, [fetchOffer]);

  const handleSubmitProposal = async (e) => {
    e.preventDefault();
    if (!proposalText.trim()) return;
    
    setSubmitting(true);
    setMsg({ type: '', text: '' });
    
    try {
      await addProposal({
        proposal: proposalText,
        amount: proposalAmount || 0,
        emp: { empId: state.employee.empId },
        resource: { resId: offer.resId }
      });
      setMsg({ type: 'success', text: 'Proposal submitted successfully!' });
      setProposalText('');
      setProposalAmount('');
    } catch (err) {
      setMsg({ type: 'error', text: 'Failed to submit proposal.' });
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) return <div className="page-content"><Spinner /></div>;
  if (!offer) return <div className="page-content"><EmptyState icon={AlertCircle} message="Offer not found" /></div>;

  const isOwner = state.employee?.empId === offer.emp?.empId;

  return (
    <div className="page-content">
      <div className="container">
        <Link to="/marketplace" className="back-link animate-fade-in">
          <ArrowLeft size={16} /> Back to Marketplace
        </Link>

        <div className="grid-2 animate-slide-up">
          {/* Main Info */}
          <div className="card h-full">
            <div className="flex gap-2 mb-4 flex-wrap">
              <Badge variant="primary">{offer.category}</Badge>
              <Badge variant="warning">{offer.type}</Badge>
              {offer.available 
                ? <Badge variant="success"><CheckCircle size={12} className="mr-1"/> Available</Badge>
                : <Badge variant="danger"><XCircle size={12} className="mr-1"/> Unavailable</Badge>
              }
            </div>
            
            <h1 className="mb-4 text-2xl">{offer.title}</h1>
            <p className="text-secondary mb-6 whitespace-pre-line">{offer.description}</p>
            
            <div className="grid-2 gap-4 mt-auto border-t border-gray-700 pt-6">
              {offer.price > 0 && (
                <div>
                  <div className="text-sm text-muted uppercase tracking-wider mb-1">Price</div>
                  <div className="text-warning font-bold text-xl">₹{offer.price.toLocaleString()}</div>
                </div>
              )}
              <div>
                <div className="text-sm text-muted uppercase tracking-wider mb-1">Offered By</div>
                <div className="font-medium">{offer.emp?.empName}</div>
              </div>
              <div>
                <div className="text-sm text-muted uppercase tracking-wider mb-1">Department</div>
                <div>{offer.emp?.deptName || '—'}</div>
              </div>
              <div>
                <div className="text-sm text-muted uppercase tracking-wider mb-1">Location</div>
                <div>{offer.emp?.location || '—'}</div>
              </div>
              <div>
                <div className="text-sm text-muted uppercase tracking-wider mb-1">Posted On</div>
                <div>{offer.date ? new Date(offer.date).toLocaleDateString() : '—'}</div>
              </div>
              {offer.availableUpto && (
                <div>
                  <div className="text-sm text-muted uppercase tracking-wider mb-1">Available Until</div>
                  <div>{new Date(offer.availableUpto).toLocaleDateString()}</div>
                </div>
              )}
            </div>
          </div>

          {/* Proposal Panel */}
          <div className="card">
            <h2 className="mb-2 flex items-center gap-2">
              <Handshake size={18} strokeWidth={1.75} />
              {isOwner ? 'Proposals Received' : 'Make an Offer'}
            </h2>
            
            {!state.isLoggedIn ? (
              <div className="empty-state py-8">
                <Lock size={40} strokeWidth={1.5} style={{ margin: '0 auto 16px', opacity: 0.4, display: 'block' }} />
                <p>Please <Link to="/login" className="text-primary-c font-semibold">login</Link> to submit a proposal.</p>
              </div>
            ) : isOwner ? (
              <div className="mt-6">
                {offer.proposals?.length === 0 ? (
                  <EmptyState icon={Inbox} message="No proposals received yet." />
                ) : (
                  <div className="flex flex-col gap-4">
                    {offer.proposals?.map(p => (
                      <div key={p.propId} className="p-4 rounded-lg bg-black/20 border border-gray-700">
                        <div className="flex justify-between items-start mb-2">
                          <span className="font-semibold text-primary-light">{p.emp?.empName}</span>
                          <span className="font-mono text-warning">₹{p.amount.toLocaleString()}</span>
                        </div>
                        <p className="text-sm text-secondary">{p.proposal}</p>
                      </div>
                    ))}
                  </div>
                )}
              </div>
            ) : !offer.available ? (
               <div className="empty-state py-8">
                 <XCircle size={48} className="mx-auto mb-4 text-danger opacity-50" />
                 <p>This offer is no longer available.</p>
               </div>
            ) : (
              <div className="mt-6">
                <p className="text-muted text-sm mb-6">Interested in this offer? Submit a proposal to the owner.</p>
                
                {msg.text && (
                  <div className={`alert ${msg.type === 'success' ? 'alert-success' : 'alert-danger'}`}>
                    {msg.text}
                  </div>
                )}

                <form onSubmit={handleSubmitProposal}>
                  <div className="form-group">
                    <label className="form-label" htmlFor="propText">Your Proposal *</label>
                    <textarea 
                      id="propText" 
                      className="form-control" 
                      rows="4" 
                      placeholder="Tell the seller what you'd like..."
                      value={proposalText}
                      onChange={e => setProposalText(e.target.value)}
                      required
                    />
                  </div>
                  <div className="form-group">
                    <label className="form-label" htmlFor="propAmt">Your Offer Amount (₹)</label>
                    <input 
                      id="propAmt" 
                      type="number" 
                      className="form-control" 
                      placeholder="0"
                      value={proposalAmount}
                      onChange={e => setProposalAmount(e.target.value)}
                      min="0"
                    />
                  </div>
                  <button type="submit" className="btn btn-accent w-full" disabled={submitting}>
                    {submitting ? <Spinner /> : <><Send size={16} /> Submit Proposal</>}
                  </button>
                </form>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default OfferDetail;
