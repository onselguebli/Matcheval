import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from './auth.service';
import { environment } from '../../environments/environment';

export interface OverviewManager {
  totalRecruiters: number;
  totalOffers: number;
  activeOffers: number;
  expiredOffers: number;
  totalCandidatures: number;
  pending: number; accepted: number; rejected: number;
  avgCandidatesPerOffer: number;
  avgDaysToFirstCandidate: number | null;
  checkedMatchesLast30: number;
}
export interface CountLabel { label: string; count: number; }
export interface MonthlyCount { period: string; count: number; }
export interface OfferTop { id: number; titre: string; datePublication: string; statut: string; candidates: number; }
export interface RecruiterPipeline { recruiter: string; pending: number; accepted: number; rejected: number; total: number; }
export interface MeetingDto { id: number; title: string; roomName: string; startAt: string; durationMin: number; }


@Injectable({
  providedIn: 'root'
})

export class StatMangService {

     private base = environment.apiUrl+'/manager/stats';

  constructor(
    private http: HttpClient,
    private authService: AuthService // Injection du service d'authentification
  ) {}

  private qp(p: any) { 
    let params = new HttpParams(); 
    Object.keys(p).forEach(k => params = params.set(k, p[k])); 
    return { params }; 
  }

  private getHeaders(): HttpHeaders {
    const token = this.authService.getToken();
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
  }

  overview(email: string): Observable<OverviewManager> { 
    const headers = this.getHeaders();
    return this.http.get<OverviewManager>(
      `${this.base}/overview`, 
      { ...this.qp({email}), headers }
    ); 
  }

  monthly(email: string, year: number): Observable<MonthlyCount[]> { 
    const headers = this.getHeaders();
    return this.http.get<MonthlyCount[]>(
      `${this.base}/candidatures-monthly`, 
      { ...this.qp({email, year}), headers }
    ); 
  }

  sources(email: string, days = 90): Observable<CountLabel[]> { 
    const headers = this.getHeaders();
    return this.http.get<CountLabel[]>(
      `${this.base}/sources`, 
      { ...this.qp({email, days}), headers }
    ); 
  }

  byRecruiter(email: string, days = 30): Observable<CountLabel[]> { 
    const headers = this.getHeaders();
    return this.http.get<CountLabel[]>(
      `${this.base}/by-recruiter`, 
      { ...this.qp({email, days}), headers }
    ); 
  }

  pipeline(email: string, days = 90): Observable<RecruiterPipeline[]> { 
    const headers = this.getHeaders();
    return this.http.get<RecruiterPipeline[]>(
      `${this.base}/pipeline-by-recruiter`, 
      { ...this.qp({email, days}), headers }
    ); 
  }

  topOffers(email: string, limit = 5): Observable<OfferTop[]> { 
    const headers = this.getHeaders();
    return this.http.get<OfferTop[]>(
      `${this.base}/top-offers`, 
      { ...this.qp({email, limit}), headers }
    ); 
  }

  topRecruitersChecked(email: string, limit = 5): Observable<CountLabel[]> { 
    const headers = this.getHeaders();
    return this.http.get<CountLabel[]>(
      `${this.base}/top-recruiters-checked`, 
      { ...this.qp({email, limit}), headers }
    ); 
  }

  upcoming(email: string, days = 14): Observable<MeetingDto[]> { 
    const headers = this.getHeaders();
    return this.http.get<MeetingDto[]>(
      `${this.base}/meetings-upcoming`, 
      { ...this.qp({email, days}), headers }
    ); 
  }
}
