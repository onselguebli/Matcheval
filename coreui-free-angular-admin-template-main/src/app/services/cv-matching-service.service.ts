import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { MatchingApiResponse } from '../models/MatchingApiResponse';
import { MatchingResult } from '../models/MatchingResult';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class CvMatchingServiceService {
  private apiUrl = 'http://localhost:8080/recruiter/matching';

  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) { }

  matchUploadedCv(offreId: number, cvFile: File): Observable<MatchingResult> {
    const formData = new FormData();
    formData.append('cv', cvFile);
    const headers = new HttpHeaders({ 
      Authorization: `Bearer ${this.authService.getToken()}` 
    });

    return this.http.post<MatchingApiResponse>(
      `${this.apiUrl}/${offreId}/upload-cv`, 
      formData, 
      { headers }
    ).pipe(
      map(response => {
        if (response.results && response.results.length > 0) {
          return response.results[0];
        }
        throw new Error('Aucun résultat trouvé dans la réponse');
      })
    );
  }

  matchAllCvsForOffre(offreId: number): Observable<MatchingResult[]> {
    const headers = new HttpHeaders({ 
      Authorization: `Bearer ${this.authService.getToken()}` 
    });

    return this.http.get<MatchingResult[]>(
      `${this.apiUrl}/${offreId}/all-cvs`, 
      { headers }
    );
  }

  getCvMatchDetails(offreId: number, candidatureId: number): Observable<MatchingResult> {
    const headers = new HttpHeaders({ 
      Authorization: `Bearer ${this.authService.getToken()}` 
    });

    return this.http.get<MatchingResult>(
      `${this.apiUrl}/${offreId}/candidature/${candidatureId}`, 
      { headers }
    );
  }
}