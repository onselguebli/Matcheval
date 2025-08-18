import { Injectable } from '@angular/core';
import { AuthService } from './auth.service';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { TrafficDashboardDTO } from '../models/TrafficDashboar';
import { SiteStatsDTO } from '../models/SiteStatsDTO';
import { SiteTypeCount } from '../models/SiteTypeCount';

@Injectable({
  providedIn: 'root'
})
export class StatService {
 private BASE_URL="http://localhost:8080";

   constructor(private http: HttpClient,
    private authService : AuthService
    
   ) {}

   getUserStatsByRole(): Observable<any> {
  const token = this.authService.getToken();
  const headers = new HttpHeaders({
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${token}`
  });
  return this.http.get<Map<string, number>>(`${this.BASE_URL}/admin/stats/users-by-role`, { headers });
}
getUsersPerYear(): Observable<any> {
  const token = this.authService.getToken();
  const headers = new HttpHeaders({
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${token}`
  });
  return this.http.get<Map<number, number>>(`${this.BASE_URL}/admin/stats/users-per-year`, { headers });
}

getUsersByStatus(): Observable<any> {
  const token = this.authService.getToken();
  const headers = new HttpHeaders({
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${token}`
  });
  return this.http.get<Map<string, number>>(`${this.BASE_URL}/admin/stats/users-by-status`, { headers });
}

getUsersByCivility(): Observable<any> {
   const token = this.authService.getToken();
  const headers = new HttpHeaders({
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${token}`
  });
  return this.http.get<Map<string, number>>(`${this.BASE_URL}/admin/stats/users-by-civility`, { headers });
}

getRecruteursPerManagerStats(): Observable<any> {
  const token = this.authService.getToken();
  const headers = new HttpHeaders({
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${token}`
  });
  return this.http.get<Map<string, number>>(`${this.BASE_URL}/admin/stats/recruteurs-by-manager`, { headers });
}
getDashboardStats(): Observable<{
  totalUsers: number,
  totalCandidatures: number,
  totalSitesExternes: number,
  totalRecruteurs: number,
  totalManagers: number
}> {
  const headers = new HttpHeaders({ 'Authorization': `Bearer ${this.authService.getToken()}` });
  return this.http.get<any>(`${this.BASE_URL}/admin/stats/dashboard`, { headers });
}

getMonthlyStats(): Observable<any> {
  const headers = new HttpHeaders({ 'Authorization': `Bearer ${this.authService.getToken()}` });
  return this.http.get(`${this.BASE_URL}/admin/stats/monthly`, { headers });
}
getTrafficStats(period: string): Observable<TrafficDashboardDTO> {
  const headers = new HttpHeaders({ 'Authorization': `Bearer ${this.authService.getToken()}` });
    return this.http.get<TrafficDashboardDTO>(`${this.BASE_URL}/admin/stats/traffic?period=${period}`, { headers });
  }

    getActiveUsersToday() {
       const headers = new HttpHeaders({ 'Authorization': `Bearer ${this.authService.getToken()}` });
    return this.http.get<number>(`${this.BASE_URL}/admin/stats/active/today`, { headers });
  }

  getCVsToday() {
     const headers = new HttpHeaders({ 'Authorization': `Bearer ${this.authService.getToken()}` });
  return this.http.get<number>(`${this.BASE_URL}/admin/stats/newCv/today`,  { headers });
}
getOffresParRecruteur(): Observable<any[]> {
  const headers = new HttpHeaders({ 'Authorization': `Bearer ${this.authService.getToken()}` });
  return this.http.get<any[]>(`${this.BASE_URL}/admin/stats/offres-par-recruteur`, { headers });
}
getStatsBySite() {
  const headers = new HttpHeaders({ 'Authorization': `Bearer ${this.authService.getToken()}` });
  return this.http.get<SiteStatsDTO[]>(`${this.BASE_URL}/admin/stats/offre+candi-perSite`, { headers });
}
getOffresByType() {
    const headers = new HttpHeaders({ 'Authorization': `Bearer ${this.authService.getToken()}` });
  return this.http.get<{ [key: string]: number }>(`${this.BASE_URL}/admin/stats/offres-by-type`, { headers });
}

  getCandsBySiteTypeGlobal(): Observable<SiteTypeCount[]> {
    const headers = new HttpHeaders({
      Authorization: `Bearer ${this.authService.getToken()}`
    });
    return this.http.get<SiteTypeCount[]>(
      `${this.BASE_URL}/admin/stats/candidatures-par-site-type`,
      { headers }
    );
  }

  getCandsBySiteTypeForRecruteur(email: string): Observable<SiteTypeCount[]> {
    const headers = new HttpHeaders({
      Authorization: `Bearer ${this.authService.getToken()}`
    });
    return this.http.get<SiteTypeCount[]>(
      `${this.BASE_URL}/admin/stats/candidatures-par-site-type/recruteur/${encodeURIComponent(email)}`,
      { headers }
    );
  }

  getCandsByTypeGlobal() {
     const headers = new HttpHeaders({
      Authorization: `Bearer ${this.authService.getToken()}`
    });
    return this.http.get<Record<string, number>>(
      `${this.BASE_URL}/admin/stats/candidatures-par-type`,
      { headers }
    );
  }
}
