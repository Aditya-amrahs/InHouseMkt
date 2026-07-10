import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ApiService } from '../../services/api.service';
import { AuthService } from '../../services/auth.service';
import { Requirement } from '../../models/models';

@Component({
  selector: 'app-requirement-form',
  templateUrl: './requirement-form.component.html',
  styleUrls: ['./requirement-form.component.css']
})
export class RequirementFormComponent implements OnInit {
  isEdit = false;
  reqId?: number;
  loading = false;
  submitting = false;
  errorMsg = '';

  title = '';
  description = '';
  category = '';
  type = '';
  price = 0;

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
      this.reqId = Number(id);
      this.loading = true;
      this.api.getRequirement(this.reqId).subscribe({
        next: (req) => {
          this.title = req.title;
          this.description = req.description || '';
          this.category = req.category || '';
          this.type = req.type || '';
          this.price = req.price || 0;
          this.loading = false;
        },
        error: () => this.router.navigate(['/requirements'])
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
    const req: Requirement = {
      resId: this.reqId,
      title: this.title,
      description: this.description,
      category: this.category,
      type: this.type,
      price: this.price,
      emp
    };

    const call = this.isEdit ? this.api.updateRequirement(req) : this.api.addRequirement(req);
    call.subscribe({
      next: () => {
        this.submitting = false;
        this.router.navigate(['/dashboard']);
      },
      error: (err) => {
        this.submitting = false;
        this.errorMsg = err?.error?.message || 'Failed to save requirement.';
      }
    });
  }
}
