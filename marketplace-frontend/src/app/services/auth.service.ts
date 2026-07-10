import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { Employee, User } from '../models/models';
import { ApiService } from './api.service';

export interface AuthState {
  user: User | null;
  employee: Employee | null;
  isLoggedIn: boolean;
}

/**
 * Authentication service managing the current user session.
 * Persists login state to sessionStorage so refreshes don't log out the user.
 */
@Injectable({ providedIn: 'root' })
export class AuthService {

  private readonly SESSION_KEY = 'mp_auth';

  private state$ = new BehaviorSubject<AuthState>(this.loadSession());

  constructor(private api: ApiService) {}

  get currentState(): AuthState {
    return this.state$.value;
  }

  get state(): Observable<AuthState> {
    return this.state$.asObservable();
  }

  get isLoggedIn(): boolean {
    return this.state$.value.isLoggedIn;
  }

  get currentEmployee(): Employee | null {
    return this.state$.value.employee;
  }

  register(user: User): Observable<User> {
    return this.api.register(user);
  }

  login(user: User): Observable<User> {
    return this.api.login(user);
  }

  setSession(user: User, employee: Employee): void {
    const newState: AuthState = { user, employee, isLoggedIn: true };
    this.state$.next(newState);
    sessionStorage.setItem(this.SESSION_KEY, JSON.stringify(newState));
  }

  logout(): void {
    const user = this.state$.value.user;
    if (user) {
      this.api.logout(user).subscribe();
    }
    const emptyState: AuthState = { user: null, employee: null, isLoggedIn: false };
    this.state$.next(emptyState);
    sessionStorage.removeItem(this.SESSION_KEY);
  }

  private loadSession(): AuthState {
    try {
      const raw = sessionStorage.getItem(this.SESSION_KEY);
      if (raw) return JSON.parse(raw) as AuthState;
    } catch {
      // ignore
    }
    return { user: null, employee: null, isLoggedIn: false };
  }
}
