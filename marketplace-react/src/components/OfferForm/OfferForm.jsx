import { useState, useEffect, useCallback } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { getOffer, addOffer, updateOffer } from '../../services/api';
import { useAuth } from '../../context/AuthContext';
import { ArrowLeft, Save, Edit2, Package } from 'lucide-react';
import Spinner from '../shared/Spinner';

const CATEGORIES = ['Accommodation', 'Electronics', 'Vehicle', 'Services', 'Other'];
const TYPES = ['SELL', 'RENT', 'FREE', 'HELP'];

const OfferForm = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const { state } = useAuth();
  
  const isEdit = !!id;
  const [loading, setLoading] = useState(isEdit);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');

  const [formData, setFormData] = useState({
    title: '',
    description: '',
    category: '',
    type: '',
    price: '',
    availableUpto: ''
  });

  const fetchOfferData = useCallback(async () => {
    try {
      const res = await getOffer(id);
      const offer = res.data;
      if (offer.emp?.empId !== state.employee?.empId) {
        navigate('/dashboard'); // Not owner
        return;
      }
      setFormData({
        title: offer.title || '',
        description: offer.description || '',
        category: offer.category || '',
        type: offer.type || '',
        price: offer.price || '',
        availableUpto: offer.availableUpto ? new Date(offer.availableUpto).toISOString().split('T')[0] : ''
      });
    } catch (err) {
      console.error(err);
      setError('Failed to load offer.');
    } finally {
      setLoading(false);
    }
  }, [id, navigate, state.employee?.empId]);

  useEffect(() => {
    if (isEdit) fetchOfferData();
  }, [isEdit, fetchOfferData]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSubmitting(true);
    setError('');

    const payload = {
      ...formData,
      price: formData.price ? Number(formData.price) : 0,
      emp: { empId: state.employee.empId }
    };

    if (!payload.availableUpto) delete payload.availableUpto;

    try {
      if (isEdit) {
        await updateOffer({ ...payload, resId: Number(id) });
      } else {
        await addOffer({ ...payload, date: new Date().toISOString().split('T')[0] });
      }
      navigate('/dashboard');
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to save offer.');
      setSubmitting(false);
    }
  };

  return (
    <div className="page-content">
      <div className="container" style={{ maxWidth: '700px' }}>
        
        <div className="mb-6 animate-fade-in">
          <Link to="/dashboard" className="back-link">
            <ArrowLeft size={16} /> Back
          </Link>
          <h1 className="text-2xl mb-2 flex items-center gap-2">
            {isEdit ? <Edit2 size={20} strokeWidth={2} /> : <Package size={20} strokeWidth={2} />}
            {isEdit ? 'Edit Offer' : 'Post an Offer'}
          </h1>
          <p className="text-secondary">
            {isEdit ? 'Update your offer details' : 'Tell others what you have to offer'}
          </p>
        </div>

        {loading ? (
          <Spinner />
        ) : (
          <div className="card animate-slide-up">
            {error && <div className="alert alert-danger">{error}</div>}

            <form onSubmit={handleSubmit}>
              <div className="form-group">
                <label className="form-label" htmlFor="title">Title *</label>
                <input 
                  id="title" name="title" type="text" className="form-control"
                  placeholder="e.g. Selling iPhone 13, Mint Condition"
                  value={formData.title} onChange={handleChange} required 
                />
              </div>

              <div className="form-group">
                <label className="form-label" htmlFor="description">Description</label>
                <textarea 
                  id="description" name="description" className="form-control" rows="4"
                  placeholder="Provide more details about the offer..."
                  value={formData.description} onChange={handleChange} 
                />
              </div>

              <div className="grid-2">
                <div className="form-group">
                  <label className="form-label" htmlFor="category">Category *</label>
                  <select id="category" name="category" className="form-control" 
                          value={formData.category} onChange={handleChange} required>
                    <option value="">Select category</option>
                    {CATEGORIES.map(c => <option key={c} value={c}>{c}</option>)}
                  </select>
                </div>
                <div className="form-group">
                  <label className="form-label" htmlFor="type">Type</label>
                  <select id="type" name="type" className="form-control" 
                          value={formData.type} onChange={handleChange}>
                    <option value="">Select type</option>
                    {TYPES.map(t => <option key={t} value={t}>{t}</option>)}
                  </select>
                </div>
              </div>

              <div className="grid-2">
                <div className="form-group">
                  <label className="form-label" htmlFor="price">Price (₹)</label>
                  <input 
                    id="price" name="price" type="number" className="form-control"
                    placeholder="0 for free or negotiable" min="0"
                    value={formData.price} onChange={handleChange} 
                  />
                </div>
                <div className="form-group">
                  <label className="form-label" htmlFor="availableUpto">Available Until (Optional)</label>
                  <input 
                    id="availableUpto" name="availableUpto" type="date" className="form-control"
                    value={formData.availableUpto} onChange={handleChange} 
                  />
                </div>
              </div>

              <div className="flex gap-4 mt-8">
                <button type="submit" className="btn btn-accent flex-1 justify-center" disabled={submitting}>
                  {submitting ? 'Saving...' : <><Save size={16} /> {isEdit ? 'Update Offer' : 'Post Offer'}</>}
                </button>
                <Link to="/dashboard" className="btn btn-outline flex-1 justify-center text-center">
                  Cancel
                </Link>
              </div>
            </form>
          </div>
        )}
      </div>
    </div>
  );
};

export default OfferForm;
