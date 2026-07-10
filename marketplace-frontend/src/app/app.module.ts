import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule } from '@angular/forms';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';

// Components
import { NavbarComponent } from './components/navbar/navbar.component';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { MarketplaceComponent } from './components/marketplace/marketplace.component';
import { RequirementDetailComponent } from './components/requirement-detail/requirement-detail.component';
import { RequirementFormComponent } from './components/requirement-form/requirement-form.component';
import { OfferDetailComponent } from './components/offer-detail/offer-detail.component';
import { OfferFormComponent } from './components/offer-form/offer-form.component';

@NgModule({
  declarations: [
    AppComponent,
    NavbarComponent,
    LoginComponent,
    RegisterComponent,
    DashboardComponent,
    MarketplaceComponent,
    RequirementDetailComponent,
    RequirementFormComponent,
    OfferDetailComponent,
    OfferFormComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule,
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule {}
