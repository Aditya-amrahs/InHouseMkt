import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ApiService } from '../../services/api.service';
import { AuthService } from '../../services/auth.service';
import { Requirement, Proposal } from '../../models/models';

@Component({
  selector: 'app-requirement-detail',
  templateUrl: './requirement-detail.component.html',
  styleUrls: ['./requirement-detail.component.css']
})
export class RequirementDetailComponent implements OnInit {
  requirement!: Requirement;
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
    this.api.getRequirement(id).subscribe({
      next: (req) => { this.requirement = req; this.loading = false; },
      error: () => { this.loading = false; this.router.navigate(['/requirements']); }
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
      resource: { resId: this.requirement.resId } as any
    };

    this.api.addProposal(prop).subscribe({
      next: () => {
        this.submitting = false;
        this.successMsg = '🎉 Proposal submitted successfully!';
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
