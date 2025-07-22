import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class UserserviceService {
  private BASE_URL="http://localhost:8080";

   constructor(private http: HttpClient,
    private router: Router
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

}
