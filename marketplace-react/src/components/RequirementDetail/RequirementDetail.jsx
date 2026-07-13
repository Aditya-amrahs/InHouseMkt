import { useState, useEffect, useCallback } from 'react';
import { useParams, Link } from 'react-router-dom';
import { getRequirement, addProposal } from '../../services/api';
import { useAuth } from '../../context/AuthContext';
import { ArrowLeft, CheckCircle, Send } from 'lucide-react';
import Spinner from '../shared/Spinner';
import Badge from '../shared/Badge';
import EmptyState from '../shared/EmptyState';

const RequirementDetail = () => {
  const { id } = useParams();
  const { state } = useAuth();
  const [req, setReq] = useState(null);
  const [loading, setLoading] = useState(true);
  
  // Proposal Form
  const [proposalText, setProposalText] = useState('');
  const [proposalAmount, setProposalAmount] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const [msg, setMsg] = useState({ type: '', text: '' });

  const fetchRequirement = useCallback(async () => {
    try {
      const res = await getRequirement(id);
      setReq(res.data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  }, [id]);

  useEffect(() => {
    fetchRequirement();
  }, [fetchRequirement]);

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
        resource: { resId: req.resId }
      });
      setMsg({ type: 'success', text: 'Proposal submitted successfully!' });
      setProposalText('');
      setProposalAmount('');
      // Optionally fetch again to show the proposal if we want
    } catch (err) {
      setMsg({ type: 'error', text: 'Failed to submit proposal.' });
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) return <div className="page-content"><Spinner /></div>;
  if (!req) return <div className="page-content"><EmptyState icon="😕" message="Requirement not found" /></div>;

  const isOwner = state.employee?.empId === req.emp?.empId;

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
              <Badge variant="primary">{req.category}</Badge>
              <Badge variant="warning">{req.type}</Badge>
              {req.fulfilled 
                ? <Badge variant="success"><CheckCircle size={12} className="mr-1"/> Fulfilled</Badge>
                : <Badge variant="warning">Open</Badge>
              }
            </div>
            
            <h1 className="mb-4 text-2xl">{req.title}</h1>
            <p className="text-secondary mb-6 whitespace-pre-line">{req.description}</p>
            
            <div className="grid-2 gap-4 mt-auto border-t border-gray-700 pt-6">
              {req.price > 0 && (
                <div>
                  <div className="text-sm text-muted uppercase tracking-wider mb-1">Budget</div>
                  <div className="text-warning font-bold text-xl">₹{req.price.toLocaleString()}</div>
                </div>
              )}
              <div>
                <div className="text-sm text-muted uppercase tracking-wider mb-1">Posted By</div>
                <div className="font-medium">{req.emp?.empName}</div>
              </div>
              <div>
                <div className="text-sm text-muted uppercase tracking-wider mb-1">Department</div>
                <div>{req.emp?.deptName || '—'}</div>
              </div>
              <div>
                <div className="text-sm text-muted uppercase tracking-wider mb-1">Location</div>
                <div>{req.emp?.location || '—'}</div>
              </div>
              <div>
                <div className="text-sm text-muted uppercase tracking-wider mb-1">Posted On</div>
                <div>{req.date ? new Date(req.date).toLocaleDateString() : '—'}</div>
              </div>
            </div>
          </div>

          {/* Proposal Panel */}
          <div className="card">
            <h2 className="mb-2 flex items-center gap-2">🤝 {isOwner ? 'Proposals Received' : 'Submit Proposal'}</h2>
            
            {!state.isLoggedIn ? (
              <div className="empty-state py-8">
                <div className="icon mb-4">🔐</div>
                <p>Please <Link to="/login" className="text-primary-light font-bold">login</Link> to submit a proposal.</p>
              </div>
            ) : isOwner ? (
              <div className="mt-6">
                {req.proposals?.length === 0 ? (
                  <EmptyState icon="📬" message="No proposals received yet." />
                ) : (
                  <div className="flex flex-col gap-4">
                    {req.proposals?.map(p => (
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
            ) : req.fulfilled ? (
               <div className="empty-state py-8">
                 <CheckCircle size={48} className="mx-auto mb-4 text-success opacity-50" />
                 <p>This requirement has already been fulfilled.</p>
               </div>
            ) : (
              <div className="mt-6">
                <p className="text-muted text-sm mb-6">Offer your help, product, or service for this requirement.</p>
                
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
                      placeholder="Describe what you're offering..."
                      value={proposalText}
                      onChange={e => setProposalText(e.target.value)}
                      required
                    />
                  </div>
                  <div className="form-group">
                    <label className="form-label" htmlFor="propAmt">Proposed Amount (₹)</label>
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
                  <button type="submit" className="btn btn-primary w-full" disabled={submitting}>
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

export default RequirementDetail;
