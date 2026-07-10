import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { MarketplaceComponent } from './components/marketplace/marketplace.component';
import { RequirementDetailComponent } from './components/requirement-detail/requirement-detail.component';
import { RequirementFormComponent } from './components/requirement-form/requirement-form.component';
import { OfferDetailComponent } from './components/offer-detail/offer-detail.component';
import { OfferFormComponent } from './components/offer-form/offer-form.component';
import { AuthGuard } from './guards/auth.guard';

const routes: Routes = [
  { path: '', redirectTo: '/marketplace', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'marketplace', component: MarketplaceComponent },

  // Requirements
  { path: 'requirements', component: MarketplaceComponent },
  { path: 'requirements/new', component: RequirementFormComponent, canActivate: [AuthGuard] },
  { path: 'requirements/:id', component: RequirementDetailComponent },
  { path: 'requirements/:id/edit', component: RequirementFormComponent, canActivate: [AuthGuard] },

  // Offers
  { path: 'offers', component: MarketplaceComponent },
  { path: 'offers/new', component: OfferFormComponent, canActivate: [AuthGuard] },
  { path: 'offers/:id', component: OfferDetailComponent },
  { path: 'offers/:id/edit', component: OfferFormComponent, canActivate: [AuthGuard] },

  // Dashboard
  { path: 'dashboard', component: DashboardComponent, canActivate: [AuthGuard] },

  // Fallback
  { path: '**', redirectTo: '/marketplace' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes, { scrollPositionRestoration: 'top' })],
  exports: [RouterModule]
})
export class AppRoutingModule {}
