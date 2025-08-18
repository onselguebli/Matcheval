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
export class StatRecService {

 private BASE_URL="http://localhost:8080/recruiter";

   constructor(private http: HttpClient,
    private authService : AuthService
    
   ) {}

  getCandsBySiteTypeForRecruteur(email: string): Observable<SiteTypeCount[]> {
    const headers = new HttpHeaders({
      Authorization: `Bearer ${this.authService.getToken()}`
    });
    return this.http.get<SiteTypeCount[]>(
      `${this.BASE_URL}/stats/candidatures-par-site-type/recruteur/${encodeURIComponent(email)}`,
      { headers }
    );
  }
}
