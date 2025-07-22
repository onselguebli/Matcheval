import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AuthService } from './auth.service';
import { OffreEmploi } from '../models/OffreEmploi';

@Injectable({
  providedIn: 'root'
})
export class OffreService {

   private baseUrl = 'http://localhost:8080';

  constructor(private http: HttpClient, private authService: AuthService) {}

  publierOffre(offre: OffreEmploi): Observable<OffreEmploi> {
    const token = this.authService.getToken();
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });

    return this.http.post<OffreEmploi>(`${this.baseUrl}/recruiter/offre/publier`, offre, { headers });
  }
}
