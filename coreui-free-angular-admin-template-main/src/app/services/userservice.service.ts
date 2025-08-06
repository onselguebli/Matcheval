import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { OffreWithCandidaturesDTO } from '../models/OffreWithCandidaturesDTO'; // Adjust the import path as necessary
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class UserserviceService {
 
  private BASE_URL="http://localhost:8080";

   constructor(private http: HttpClient,
    private router: Router,
    private authService: AuthService 
   ) {}

// userservice.service.ts
login(email: string, password: string) {
    const url = `${this.BASE_URL}/recruiter/login`;
  return this.http.post(
   url,
    { email, password }, // Ensure this matches Spring's expected JSON
    { headers: { 'Content-Type': 'application/json' } }
  );
  
}

logout(): void {
    localStorage.removeItem('jwt');  // ou sessionStorage.clear();
    this.router.navigate(['/login']); // Redirige vers la page de connexion
  }


getOffresWithCandidaturesByRecruteur(recruteurEmail: string): Observable<OffreWithCandidaturesDTO[]> {
  const headers = new HttpHeaders({ 'Authorization': `Bearer ${this.authService.getToken()}` });
    return this.http.get<OffreWithCandidaturesDTO[]>(`${this.BASE_URL}/recruiter/offres-candidatures/${recruteurEmail}`, { headers });
  }
  
getCandidatureById(id: number) {
  const headers = new HttpHeaders({ 'Authorization': `Bearer ${this.authService.getToken()}` });
  return this.http.get(`${this.BASE_URL}/recruiter/candidature/${id}`,  { headers });
}

updateCandidatureStatut(id: number, dto: any) {
   const headers = new HttpHeaders({ 'Authorization': `Bearer ${this.authService.getToken()}` });
  return this.http.put(`${this.BASE_URL}/recruiter/candidature/${id}/statut`, dto, { headers });
}

getOffresByRecruteur(email: string): Observable<any[]> {
  const headers = new HttpHeaders({ Authorization: `Bearer ${this.authService.getToken()}` });
  return this.http.get<any[]>(`${this.BASE_URL}/offresByrecruteur/${email}`, { headers });
}

updateOffre(id: number, data: any) {
  const headers = new HttpHeaders({ Authorization: `Bearer ${this.authService.getToken()}` });
  return this.http.put(`${this.BASE_URL}/recruiter/offre/${id}`, data, { headers });
}


}
