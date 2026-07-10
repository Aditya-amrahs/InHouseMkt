import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { ApiService } from '../../services/api.service';
import { User, Employee } from '../../models/models';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  userId = '';
  password = '';
  loading = false;
  errorMsg = '';

  constructor(
    private auth: AuthService,
    private api: ApiService,
    private router: Router
  ) {}

  onSubmit(): void {
    if (!this.userId || !this.password) {
      this.errorMsg = 'Please fill in all fields.';
      return;
    }
    this.loading = true;
    this.errorMsg = '';

    const user: User = { userId: this.userId, password: this.password };

    this.auth.login(user).subscribe({
      next: (loggedInUser) => {
        // Resolve the real Employee profile linked to this user
        this.api.getEmployeeByUserId(loggedInUser.userId).subscribe({
          next: (emp) => {
            this.auth.setSession(loggedInUser, emp);
            this.router.navigate(['/dashboard']);
          },
          error: () => {
            // No employee profile yet — create one so empId is always populated
            const newEmp: Employee = { empName: loggedInUser.userId, user: loggedInUser };
            this.api.addEmployee(newEmp).subscribe({
              next: (savedEmp) => {
                this.auth.setSession(loggedInUser, savedEmp);
                this.router.navigate(['/dashboard']);
              },
              error: () => {
                this.loading = false;
                this.errorMsg = 'Logged in, but could not load your employee profile.';
              }
            });
          }
        });
      },
      error: (err) => {
        this.loading = false;
        this.errorMsg = err?.error?.message || 'Invalid credentials. Please try again.';
      }
    });
  }
}
