import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Employee, Offer, Proposal, Requirement, Resource, User } from '../models/models';

const BASE = 'http://localhost:8080/api';

/**
 * Centralised HTTP client for all marketplace API calls.
 */
@Injectable({ providedIn: 'root' })
export class ApiService {

  constructor(private http: HttpClient) {}

  // ---- Users ----
  register(user: User): Observable<User> {
    return this.http.post<User>(`${BASE}/users/register`, user);
  }

  login(user: User): Observable<User> {
    return this.http.post<User>(`${BASE}/users/login`, user);
  }

  logout(user: User): Observable<User> {
    return this.http.post<User>(`${BASE}/users/logout`, user);
  }

  updateUser(userId: string, user: User): Observable<User> {
    return this.http.put<User>(`${BASE}/users/${userId}`, user);
  }

  deleteUser(userId: string): Observable<void> {
    return this.http.delete<void>(`${BASE}/users/${userId}`);
  }

  // ---- Employees ----
  addEmployee(emp: Employee): Observable<Employee> {
    return this.http.post<Employee>(`${BASE}/employees`, emp);
  }

  updateEmployee(emp: Employee): Observable<Employee> {
    return this.http.put<Employee>(`${BASE}/employees`, emp);
  }

  getEmployee(empId: number): Observable<Employee> {
    return this.http.get<Employee>(`${BASE}/employees/${empId}`);
  }

  getEmployeeByUserId(userId: string): Observable<Employee> {
    const params = new HttpParams().set('userId', userId);
    return this.http.get<Employee>(`${BASE}/employees/by-user`, { params });
  }

  getEmployeeOffers(empId: number): Observable<Offer[]> {
    return this.http.get<Offer[]>(`${BASE}/employees/${empId}/offers`);
  }

  getEmployeeRequirements(empId: number): Observable<Requirement[]> {
    return this.http.get<Requirement[]>(`${BASE}/employees/${empId}/requirements`);
  }

  updateOfferAvailability(offer: Offer): Observable<Offer> {
    return this.http.patch<Offer>(`${BASE}/employees/offers/availability`, offer);
  }

  updateRequirementFulfilled(req: Requirement): Observable<Requirement> {
    return this.http.patch<Requirement>(`${BASE}/employees/requirements/fulfilled`, req);
  }

  acceptProposal(prop: Proposal): Observable<Proposal> {
    return this.http.patch<Proposal>(`${BASE}/employees/proposals/accepted`, prop);
  }

  // ---- Requirements ----
  addRequirement(req: Requirement): Observable<Requirement> {
    return this.http.post<Requirement>(`${BASE}/requirements`, req);
  }

  updateRequirement(req: Requirement): Observable<Requirement> {
    return this.http.put<Requirement>(`${BASE}/requirements`, req);
  }

  getRequirement(reqId: number): Observable<Requirement> {
    return this.http.get<Requirement>(`${BASE}/requirements/${reqId}`);
  }

  deleteRequirement(reqId: number): Observable<void> {
    return this.http.delete<void>(`${BASE}/requirements/${reqId}`);
  }

  getAllRequirements(category?: string, type?: string): Observable<Requirement[]> {
    let params = new HttpParams();
    if (category) params = params.set('category', category);
    if (type) params = params.set('type', type);
    return this.http.get<Requirement[]>(`${BASE}/requirements`, { params });
  }

  // ---- Offers ----
  addOffer(offer: Offer): Observable<Offer> {
    return this.http.post<Offer>(`${BASE}/offers`, offer);
  }

  updateOffer(offer: Offer): Observable<Offer> {
    return this.http.put<Offer>(`${BASE}/offers`, offer);
  }

  getOffer(offerId: number): Observable<Offer> {
    return this.http.get<Offer>(`${BASE}/offers/${offerId}`);
  }

  deleteOffer(offerId: number): Observable<void> {
    return this.http.delete<void>(`${BASE}/offers/${offerId}`);
  }

  getAllOffers(category?: string, type?: string): Observable<Offer[]> {
    let params = new HttpParams();
    if (category) params = params.set('category', category);
    if (type) params = params.set('type', type);
    return this.http.get<Offer[]>(`${BASE}/offers`, { params });
  }

  // ---- Proposals ----
  addProposal(prop: Proposal): Observable<Proposal> {
    return this.http.post<Proposal>(`${BASE}/proposals`, prop);
  }

  updateProposal(prop: Proposal): Observable<Proposal> {
    return this.http.put<Proposal>(`${BASE}/proposals`, prop);
  }

  getProposal(propId: number): Observable<Proposal> {
    return this.http.get<Proposal>(`${BASE}/proposals/${propId}`);
  }

  deleteProposal(propId: number): Observable<void> {
    return this.http.delete<void>(`${BASE}/proposals/${propId}`);
  }

  getAllProposals(): Observable<Proposal[]> {
    return this.http.get<Proposal[]>(`${BASE}/proposals`);
  }

  // ---- Resources ----
  getAllResources(params?: { category?: string; type?: string; empId?: number }): Observable<Resource[]> {
    let httpParams = new HttpParams();
    if (params?.category) httpParams = httpParams.set('category', params.category);
    if (params?.type) httpParams = httpParams.set('type', params.type);
    if (params?.empId) httpParams = httpParams.set('empId', params.empId);
    return this.http.get<Resource[]>(`${BASE}/resources`, { params: httpParams });
  }
}
