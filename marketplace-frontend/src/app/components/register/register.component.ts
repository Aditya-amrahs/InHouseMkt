import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { ApiService } from '../../services/api.service';
import { User, Employee } from '../../models/models';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent {
  userId = '';
  password = '';
  confirmPassword = '';
  empName = '';
  deptName = '';
  location = '';
  loading = false;
  errorMsg = '';
  successMsg = '';

  constructor(
    private auth: AuthService,
    private api: ApiService,
    private router: Router
  ) {}

  onSubmit(): void {
    if (!this.userId || !this.password || !this.empName) {
      this.errorMsg = 'User ID, password and name are required.';
      return;
    }
    if (this.password !== this.confirmPassword) {
      this.errorMsg = 'Passwords do not match.';
      return;
    }
    this.loading = true;
    this.errorMsg = '';

    const user: User = { userId: this.userId, password: this.password };

    // Step 1: Register user
    this.auth.register(user).subscribe({
      next: (savedUser) => {
        // Step 2: Create employee profile
        const emp: Employee = {
          empName: this.empName,
          deptName: this.deptName,
          location: this.location,
          user: savedUser
        };
        this.api.addEmployee(emp).subscribe({
          next: (savedEmp) => {
            this.loading = false;
            this.auth.setSession(savedUser, savedEmp);
            this.router.navigate(['/dashboard']);
          },
          error: (err) => {
            this.loading = false;
            this.errorMsg = err?.error?.message || 'Failed to create employee profile.';
          }
        });
      },
      error: (err) => {
        this.loading = false;
        this.errorMsg = err?.error?.message || 'Registration failed. User ID may already exist.';
      }
    });
  }
}
