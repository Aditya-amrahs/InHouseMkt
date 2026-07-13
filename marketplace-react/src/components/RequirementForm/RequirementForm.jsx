import { useState, useEffect, useCallback } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { getRequirement, addRequirement, updateRequirement } from '../../services/api';
import { useAuth } from '../../context/AuthContext';
import { ArrowLeft, Save } from 'lucide-react';
import Spinner from '../shared/Spinner';

const CATEGORIES = ['Accommodation', 'Electronics', 'Vehicle', 'Services', 'Other'];
const TYPES = ['SELL', 'RENT', 'FREE', 'HELP'];

const RequirementForm = () => {
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
    price: ''
  });

  const fetchReq = useCallback(async () => {
    try {
      const res = await getRequirement(id);
      const req = res.data;
      if (req.emp?.empId !== state.employee?.empId) {
        navigate('/dashboard'); // Not owner
        return;
      }
      setFormData({
        title: req.title || '',
        description: req.description || '',
        category: req.category || '',
        type: req.type || '',
        price: req.price || ''
      });
    } catch (err) {
      console.error(err);
      setError('Failed to load requirement.');
    } finally {
      setLoading(false);
    }
  }, [id, navigate, state.employee?.empId]);

  useEffect(() => {
    if (isEdit) fetchReq();
  }, [isEdit, fetchReq]);

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

    try {
      if (isEdit) {
        await updateRequirement({ ...payload, resId: Number(id) });
      } else {
        await addRequirement({ ...payload, date: new Date().toISOString().split('T')[0] });
      }
      navigate('/dashboard');
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to save requirement.');
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
          <h1 className="text-2xl mb-2">{isEdit ? '✏️ Edit Requirement' : '📋 Post a Requirement'}</h1>
          <p className="text-secondary">
            {isEdit ? 'Update your requirement details' : 'Tell others what you need'}
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
                  placeholder="e.g. Looking for PG accommodation near office"
                  value={formData.title} onChange={handleChange} required 
                />
              </div>

              <div className="form-group">
                <label className="form-label" htmlFor="description">Description</label>
                <textarea 
                  id="description" name="description" className="form-control" rows="4"
                  placeholder="Provide more details..."
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

              <div className="form-group">
                <label className="form-label" htmlFor="price">Budget (₹)</label>
                <input 
                  id="price" name="price" type="number" className="form-control"
                  placeholder="0 for negotiable" min="0"
                  value={formData.price} onChange={handleChange} 
                />
              </div>

              <div className="flex gap-4 mt-8">
                <button type="submit" className="btn btn-primary flex-1 justify-center" disabled={submitting}>
                  {submitting ? 'Saving...' : <><Save size={16} /> {isEdit ? 'Update Requirement' : 'Post Requirement'}</>}
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

export default RequirementForm;
