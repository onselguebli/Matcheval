import { Injectable } from '@angular/core';
import { AuthService } from './auth.service';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SiteTypeCount } from '../models/SiteTypeCount';
import { environment } from '../../environments/environment';
export interface OverviewStats {
  totalOffers: number;
  activeOffers: number;
  expiredOffers: number;
  totalCandidatures: number;
  pendingCandidatures: number;
  acceptedCandidatures: number;
  rejectedCandidatures: number;
  avgCandidatesPerOffer: number | null;
  avgDaysToFirstCandidate: number | null;
}
export interface CountLabel { label: string; count: number; }
export interface MonthlyCount { period: string; count: number; }
export interface OfferTop { id: number; titre: string; datePublication: string; statut: string; candidates: number; }
@Injectable({
  providedIn: 'root'
})
export class StatRecService {

 private BASE_URL=environment.apiUrl+'/recruiter/stats';

   constructor(private http: HttpClient,
    private authService : AuthService
    
   ) {}

  getCandsBySiteTypeForRecruteur(email: string): Observable<SiteTypeCount[]> {
    const headers = new HttpHeaders({Authorization: `Bearer ${this.authService.getToken()}`});
    return this.http.get<SiteTypeCount[]>(
      `${this.BASE_URL}/candidatures-par-site-type/recruteur/${encodeURIComponent(email)}`,
      { headers }
    );
  }
  overview(email: string): Observable<OverviewStats> {
    const headers = new HttpHeaders({Authorization: `Bearer ${this.authService.getToken()}`});
    return this.http.get<OverviewStats>(`${this.BASE_URL}/overview`, { params: new HttpParams().set('email', email) ,headers});
    // en prod: pas de param email -> le backend lit le token
  }
  offersByStatus(email: string) {
    const headers = new HttpHeaders({Authorization: `Bearer ${this.authService.getToken()}`});
    return this.http.get<CountLabel[]>(`${this.BASE_URL}/offers-by-status`, { params: { email } ,headers});
  }
  offersByType(email: string) {
    const headers = new HttpHeaders({Authorization: `Bearer ${this.authService.getToken()}`});
    return this.http.get<CountLabel[]>(`${this.BASE_URL}/offers-by-type`, { params: { email } ,headers});
  }
  candidaturesMonthly(email: string, year: number) {
    const headers = new HttpHeaders({Authorization: `Bearer ${this.authService.getToken()}`});
    return this.http.get<MonthlyCount[]>(`${this.BASE_URL}/candidatures-monthly`, { params: { email, year } as any ,headers });
  }
  candidaturesBySource(email: string, days = 90) {
    const headers = new HttpHeaders({Authorization: `Bearer ${this.authService.getToken()}`});
    return this.http.get<CountLabel[]>(`${this.BASE_URL}/candidatures-by-source`, { params: { email, days } as any ,headers});
  }
  topOffers(email: string, limit = 5) {
    const headers = new HttpHeaders({Authorization: `Bearer ${this.authService.getToken()}`});
    return this.http.get<OfferTop[]>(`${this.BASE_URL}/top-offers`, { params: { email, limit } as any ,headers});
  }
}
