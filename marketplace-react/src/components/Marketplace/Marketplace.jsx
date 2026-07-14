import { useState, useEffect, useCallback } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { getAllResources } from '../../services/api';
import {
  Search, Plus, RefreshCcw, Store,
  ClipboardList, Package, ArrowRight
} from 'lucide-react';
import Spinner from '../shared/Spinner';
import Badge from '../shared/Badge';
import EmptyState from '../shared/EmptyState';
import styles from './Marketplace.module.css';

const CATEGORIES = ['Accommodation', 'Electronics', 'Vehicle', 'Services', 'Other'];
const TYPES = ['SELL', 'RENT', 'FREE', 'HELP'];

const Marketplace = ({ defaultView = 'all' }) => {
  const { state } = useAuth();
  const [resources, setResources] = useState([]);
  const [loading, setLoading] = useState(true);

  const [searchTerm, setSearchTerm] = useState('');
  const [filterCategory, setFilterCategory] = useState('');
  const [filterType, setFilterType] = useState('');
  const [activeView, setActiveView] = useState(defaultView);

  const fetchResources = useCallback(async () => {
    setLoading(true);
    try {
      const res = await getAllResources({
        category: filterCategory || undefined,
        type: filterType || undefined
      });
      setResources(res.data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  }, [filterCategory, filterType]);

  useEffect(() => {
    setActiveView(defaultView);
  }, [defaultView]);

  useEffect(() => {
    fetchResources();
  }, [fetchResources]);

  const clearFilters = () => {
    setSearchTerm('');
    setFilterCategory('');
    setFilterType('');
  };

  const reqs   = resources.filter(r => r.resourceType === 'REQUIREMENT');
  const offers = resources.filter(r => r.resourceType === 'OFFER');

  const filterBySearch = (list) => {
    if (!searchTerm) return list;
    const term = searchTerm.toLowerCase();
    return list.filter(r =>
      r.title.toLowerCase().includes(term) ||
      (r.description && r.description.toLowerCase().includes(term))
    );
  };

  const displayReqs   = filterBySearch(reqs);
  const displayOffers = filterBySearch(offers);

  return (
    <div className="page-content">
      <div className="container">

        {/* Hero */}
        <div className="page-hero animate-fade-in">
          <h1>Employee Marketplace</h1>
          <p>Find what you need or offer what you have — within your company</p>
        </div>

        {/* Filters Panel */}
        <div className={`${styles.filtersPanel} animate-slide-up`}>
          <div className={styles.filtersRow}>
            <div className={styles.searchWrapper}>
              <Search className={styles.searchIcon} size={16} strokeWidth={2} />
              <input
                type="text"
                className="form-control"
                placeholder="Search requirements and offers..."
                value={searchTerm}
                onChange={e => setSearchTerm(e.target.value)}
              />
            </div>

            <select className="form-control" value={filterCategory} onChange={e => setFilterCategory(e.target.value)}>
              <option value="">All Categories</option>
              {CATEGORIES.map(c => <option key={c} value={c}>{c}</option>)}
            </select>

            <select className="form-control" value={filterType} onChange={e => setFilterType(e.target.value)}>
              <option value="">All Types</option>
              {TYPES.map(t => <option key={t} value={t}>{t}</option>)}
            </select>

            <button className="btn btn-outline btn-icon" onClick={clearFilters} title="Clear Filters">
              <RefreshCcw size={15} />
            </button>
          </div>

          <div className={styles.viewToggle}>
            <button className={activeView === 'all' ? styles.active : ''} onClick={() => setActiveView('all')}>
              All
            </button>
            <button className={activeView === 'requirements' ? styles.active : ''} onClick={() => setActiveView('requirements')}>
              <ClipboardList size={13} strokeWidth={2} />
              Requirements <span className={styles.count}>{displayReqs.length}</span>
            </button>
            <button className={activeView === 'offers' ? styles.active : ''} onClick={() => setActiveView('offers')}>
              <Package size={13} strokeWidth={2} />
              Offers <span className={styles.count}>{displayOffers.length}</span>
            </button>
          </div>
        </div>

        {loading ? (
          <Spinner />
        ) : (
          <div className={styles.contentArea}>

            {/* Requirements Section */}
            {(activeView === 'all' || activeView === 'requirements') && (
              <div className="animate-fade-in">
                <div className="section-header">
                  <h2 className="section-title">
                    <ClipboardList size={16} strokeWidth={2} />
                    Requirements
                  </h2>
                  {state.isLoggedIn && (
                    <Link to="/requirements/new" className="btn btn-primary btn-sm">
                      <Plus size={15} /> Post Requirement
                    </Link>
                  )}
                </div>

                {displayReqs.length === 0 ? (
                  <EmptyState icon={ClipboardList} message="No requirements found matching your filters." />
                ) : (
                  <div className="grid-auto">
                    {displayReqs.map(req => (
                      <div key={req.resId} className={`card ${styles.resourceCard}`}>
                        <div className={styles.cardTop}>
                          <Badge variant="primary">{req.category}</Badge>
                          <Badge variant="warning">{req.type}</Badge>
                          {req.fulfilled && <Badge variant="success">Fulfilled</Badge>}
                        </div>
                        <h3 className={styles.title}>{req.title}</h3>
                        <p className={styles.desc}>{req.description}</p>
                        <div className={styles.cardFooter}>
                          <div className={styles.meta}>
                            {req.price > 0 && <span className={styles.price}>₹{req.price.toLocaleString()}</span>}
                            <span className={styles.author}>by {req.emp?.empName}</span>
                          </div>
                          <Link to={`/requirements/${req.resId}`} className="btn btn-outline btn-sm">
                            View <ArrowRight size={13} />
                          </Link>
                        </div>
                      </div>
                    ))}
                  </div>
                )}
              </div>
            )}

            {activeView === 'all' && <div className="divider" />}

            {/* Offers Section */}
            {(activeView === 'all' || activeView === 'offers') && (
              <div className="animate-fade-in" style={{ animationDelay: '0.05s' }}>
                <div className="section-header">
                  <h2 className="section-title">
                    <Package size={16} strokeWidth={2} />
                    Offers
                  </h2>
                  {state.isLoggedIn && (
                    <Link to="/offers/new" className="btn btn-accent btn-sm">
                      <Plus size={15} /> Post Offer
                    </Link>
                  )}
                </div>

                {displayOffers.length === 0 ? (
                  <EmptyState icon={Package} message="No offers found matching your filters." />
                ) : (
                  <div className="grid-auto">
                    {displayOffers.map(offer => (
                      <div key={offer.resId} className={`card ${styles.resourceCard} ${styles.offerCard}`}>
                        <div className={styles.cardTop}>
                          <Badge variant="primary">{offer.category}</Badge>
                          <Badge variant="warning">{offer.type}</Badge>
                          {offer.available
                            ? <Badge variant="success">Available</Badge>
                            : <Badge variant="danger">Unavailable</Badge>
                          }
                        </div>
                        <h3 className={styles.title}>{offer.title}</h3>
                        <p className={styles.desc}>{offer.description}</p>
                        <div className={styles.cardFooter}>
                          <div className={styles.meta}>
                            {offer.price > 0 && <span className={styles.price}>₹{offer.price.toLocaleString()}</span>}
                            <span className={styles.author}>by {offer.emp?.empName}</span>
                          </div>
                          <Link to={`/offers/${offer.resId}`} className="btn btn-outline btn-sm">
                            View <ArrowRight size={13} />
                          </Link>
                        </div>
                      </div>
                    ))}
                  </div>
                )}
              </div>
            )}

          </div>
        )}
      </div>
    </div>
  );
};

export default Marketplace;
