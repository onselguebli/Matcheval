import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { MatchingApiResponse } from '../models/MatchingApiResponse';
import { MatchingResult } from '../models/MatchingResult';
import { AuthService } from './auth.service';
import { Candidature } from '../models/candidature';
import { CheckedMatchDTO } from '../models/CheckedMatchDTO';

@Injectable({
  providedIn: 'root'
})
export class CvMatchingServiceService {
  private apiUrl = 'http://localhost:8080/recruiter/matching';
   private baseUrl = 'http://localhost:8080';     

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

  getCvMatchDetails(offreId: number, candidatureId: number): Observable<any> {
  const headers = new HttpHeaders({ 
    Authorization: `Bearer ${this.authService.getToken()}` 
  });

  return this.http.get<any>(
    `${this.apiUrl}/${offreId}/candidature/${candidatureId}`, 
    { headers }
  );
}

   getCandidaturesForOffre(offreId: number): Observable<Candidature[]> {
    const headers = new HttpHeaders({ 
      Authorization: `Bearer ${this.authService.getToken()}` 
    });

    return this.http.get<Candidature[]>(
      `http://localhost:8080/recruiter/${offreId}/candidatures`, 
      { headers }
    );
  }

  
  /** Ajoute un check (recruteurEmail requis par ton backend actuel) */
  addCheckedMatch(payload: {
    offreId: number;
    candidatureId: number;
    recruteurEmail: string;
    scoreOverall?: number | null;
    filenameSnapshot?: string | null;
  }): Observable<CheckedMatchDTO> {
    const headers = new HttpHeaders({ Authorization: `Bearer ${this.authService.getToken()}` });
    return this.http.post<CheckedMatchDTO>(
      `${this.baseUrl}/recruiter/checked-matches/add`,
      payload,
      { headers }
    );
  }

  /** Liste des checks du recruteur (par email) */
  getCheckedForRecruteur(email: string): Observable<CheckedMatchDTO[]> {
    const headers = new HttpHeaders({ Authorization: `Bearer ${this.authService.getToken()}` });
    return this.http.get<CheckedMatchDTO[]>(
      `${this.baseUrl}/recruiter/checked-matches?email=${encodeURIComponent(email)}`,
      { headers }
    );
  }

  /** Liste agrégée Manager, filtre recruteur optionnel */
  getCheckedForManager(managerEmail: string, recruteurEmail?: string): Observable<CheckedMatchDTO[]> {
    const headers = new HttpHeaders({ Authorization: `Bearer ${this.authService.getToken()}` });
    const url = recruteurEmail
      ? `${this.baseUrl}/manager/checked-matches?email=${encodeURIComponent(managerEmail)}&recruteurEmail=${encodeURIComponent(recruteurEmail)}`
      : `${this.baseUrl}/manager/checked-matches?email=${encodeURIComponent(managerEmail)}`;
    return this.http.get<CheckedMatchDTO[]>(url, { headers });
  }

  /** Suppression d’un check (ton mapping actuel est /{checkId}/checked-matches) */
  deleteChecked(checkId: number): Observable<void> {
    const headers = new HttpHeaders({ Authorization: `Bearer ${this.authService.getToken()}` });
    return this.http.delete<void>(
      `${this.baseUrl}/${checkId}/checked-matches`,
      { headers }
    );
  }
}