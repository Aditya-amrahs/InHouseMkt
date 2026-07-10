import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ApiService } from '../../services/api.service';
import { AuthService } from '../../services/auth.service';
import { Offer } from '../../models/models';

@Component({
  selector: 'app-offer-form',
  templateUrl: './offer-form.component.html',
  styleUrls: ['./offer-form.component.css']
})
export class OfferFormComponent implements OnInit {
  isEdit = false;
  offerId?: number;
  loading = false;
  submitting = false;
  errorMsg = '';

  title = '';
  description = '';
  category = '';
  type = '';
  price = 0;
  availableUpto = '';

  categories = ['Accommodation', 'Electronics', 'Vehicle', 'Services', 'Other'];
  types = ['SELL', 'RENT', 'FREE', 'HELP'];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private api: ApiService,
    private auth: AuthService
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id && id !== 'new') {
      this.isEdit = true;
      this.offerId = Number(id);
      this.loading = true;
      this.api.getOffer(this.offerId).subscribe({
        next: (offer) => {
          this.title = offer.title;
          this.description = offer.description || '';
          this.category = offer.category || '';
          this.type = offer.type || '';
          this.price = offer.price || 0;
          this.availableUpto = offer.availableUpto || '';
          this.loading = false;
        },
        error: () => this.router.navigate(['/offers'])
      });
    }
  }

  onSubmit(): void {
    if (!this.title || !this.category) {
      this.errorMsg = 'Title and category are required.';
      return;
    }
    this.submitting = true;
    this.errorMsg = '';

    const emp = this.auth.currentEmployee!;
    const offer: Offer = {
      resId: this.offerId,
      title: this.title,
      description: this.description,
      category: this.category,
      type: this.type,
      price: this.price,
      available: true,
      availableUpto: this.availableUpto || undefined,
      emp
    };

    const call = this.isEdit ? this.api.updateOffer(offer) : this.api.addOffer(offer);
    call.subscribe({
      next: () => {
        this.submitting = false;
        this.router.navigate(['/dashboard']);
      },
      error: (err) => {
        this.submitting = false;
        this.errorMsg = err?.error?.message || 'Failed to save offer.';
      }
    });
  }
}
