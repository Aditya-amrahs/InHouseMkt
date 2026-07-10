import { Component, OnInit } from '@angular/core';
import { ApiService } from '../../services/api.service';
import { AuthService } from '../../services/auth.service';
import { Requirement, Offer } from '../../models/models';

@Component({
  selector: 'app-marketplace',
  templateUrl: './marketplace.component.html',
  styleUrls: ['./marketplace.component.css']
})
export class MarketplaceComponent implements OnInit {
  requirements: Requirement[] = [];
  offers: Offer[] = [];
  loading = true;
  activeView: 'requirements' | 'offers' | 'all' = 'all';
  filterCategory = '';
  filterType = '';
  searchTerm = '';

  categories = ['Accommodation', 'Electronics', 'Vehicle', 'Services', 'Other'];
  types = ['SELL', 'RENT', 'FREE', 'HELP'];

  get isLoggedIn() { return this.auth.isLoggedIn; }

  constructor(private api: ApiService, public auth: AuthService) {}

  ngOnInit(): void {
    this.loadAll();
  }

  loadAll(): void {
    this.loading = true;
    const category = this.filterCategory || undefined;
    const type = this.filterType || undefined;

    this.api.getAllRequirements(category, type).subscribe({
      next: (r) => { this.requirements = r; this.checkDone(); },
      error: () => this.checkDone()
    });
    this.api.getAllOffers(category, type).subscribe({
      next: (o) => { this.offers = o; this.checkDone(); },
      error: () => this.checkDone()
    });
  }

  private loaded = 0;
  checkDone(): void {
    this.loaded++;
    if (this.loaded >= 2) { this.loading = false; this.loaded = 0; }
  }

  applyFilters(): void {
    this.loadAll();
  }

  clearFilters(): void {
    this.filterCategory = '';
    this.filterType = '';
    this.searchTerm = '';
    this.loadAll();
  }

  get filteredRequirements(): Requirement[] {
    if (!this.searchTerm) return this.requirements;
    const t = this.searchTerm.toLowerCase();
    return this.requirements.filter(r =>
      r.title?.toLowerCase().includes(t) || r.description?.toLowerCase().includes(t)
    );
  }

  get filteredOffers(): Offer[] {
    if (!this.searchTerm) return this.offers;
    const t = this.searchTerm.toLowerCase();
    return this.offers.filter(o =>
      o.title?.toLowerCase().includes(t) || o.description?.toLowerCase().includes(t)
    );
  }
}
