import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ApiService } from '../../services/api.service';
import { AuthService } from '../../services/auth.service';
import { Offer, Proposal } from '../../models/models';

@Component({
  selector: 'app-offer-detail',
  templateUrl: './offer-detail.component.html',
  styleUrls: ['./offer-detail.component.css']
})
export class OfferDetailComponent implements OnInit {
  offer!: Offer;
  loading = true;
  proposalText = '';
  proposalAmount = 0;
  submitting = false;
  successMsg = '';
  errorMsg = '';

  get isLoggedIn() { return this.auth.isLoggedIn; }
  get currentEmp() { return this.auth.currentEmployee; }

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private api: ApiService,
    private auth: AuthService
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.api.getOffer(id).subscribe({
      next: (offer) => { this.offer = offer; this.loading = false; },
      error: () => { this.loading = false; this.router.navigate(['/offers']); }
    });
  }

  submitProposal(): void {
    if (!this.proposalText) { this.errorMsg = 'Proposal text is required.'; return; }
    this.submitting = true;
    this.errorMsg = '';

    const prop: Proposal = {
      proposal: this.proposalText,
      amount: this.proposalAmount,
      emp: this.currentEmp!,
      resource: { resId: this.offer.resId } as any
    };

    this.api.addProposal(prop).subscribe({
      next: () => {
        this.submitting = false;
        this.successMsg = '🎉 Proposal submitted!';
        this.proposalText = '';
        this.proposalAmount = 0;
      },
      error: (err) => {
        this.submitting = false;
        this.errorMsg = err?.error?.message || 'Failed to submit proposal.';
      }
    });
  }
}
