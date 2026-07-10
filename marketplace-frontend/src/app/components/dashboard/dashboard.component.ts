import { Component, OnInit } from '@angular/core';
import { ApiService } from '../../services/api.service';
import { AuthService } from '../../services/auth.service';
import { Requirement, Offer, Proposal, Employee } from '../../models/models';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  employee!: Employee;
  myRequirements: Requirement[] = [];
  myOffers: Offer[] = [];
  myProposals: Proposal[] = [];
  loading = true;
  activeTab: 'requirements' | 'offers' | 'proposals' = 'requirements';

  constructor(private api: ApiService, private auth: AuthService) {}

  ngOnInit(): void {
    const emp = this.auth.currentEmployee;
    if (emp?.empId) {
      this.employee = emp;
      this.loadData(emp.empId);
    } else {
      this.loading = false;
    }
  }

  loadData(empId: number): void {
    this.api.getEmployeeRequirements(empId).subscribe({
      next: (reqs) => { this.myRequirements = reqs; },
      error: () => {}
    });
    this.api.getEmployeeOffers(empId).subscribe({
      next: (offers) => { this.myOffers = offers; },
      error: () => {}
    });
    this.api.getAllProposals().subscribe({
      next: (props) => {
        this.myProposals = props.filter(p => p.emp?.empId === empId);
        this.loading = false;
      },
      error: () => { this.loading = false; }
    });
  }

  markFulfilled(req: Requirement): void {
    this.api.updateRequirementFulfilled(req).subscribe({
      next: (updated) => {
        const idx = this.myRequirements.findIndex(r => r.resId === updated.resId);
        if (idx > -1) this.myRequirements[idx] = updated;
      }
    });
  }

  markUnavailable(offer: Offer): void {
    const updated = { ...offer, available: false };
    this.api.updateOfferAvailability(updated).subscribe({
      next: (res) => {
        const idx = this.myOffers.findIndex(o => o.resId === res.resId);
        if (idx > -1) this.myOffers[idx] = res;
      }
    });
  }

  deleteRequirement(reqId: number): void {
    if (!confirm('Delete this requirement?')) return;
    this.api.deleteRequirement(reqId).subscribe({
      next: () => { this.myRequirements = this.myRequirements.filter(r => r.resId !== reqId); }
    });
  }

  deleteOffer(offerId: number): void {
    if (!confirm('Delete this offer?')) return;
    this.api.deleteOffer(offerId).subscribe({
      next: () => { this.myOffers = this.myOffers.filter(o => o.resId !== offerId); }
    });
  }

  deleteProposal(propId: number): void {
    if (!confirm('Delete this proposal?')) return;
    this.api.deleteProposal(propId).subscribe({
      next: () => { this.myProposals = this.myProposals.filter(p => p.propId !== propId); }
    });
  }
}
