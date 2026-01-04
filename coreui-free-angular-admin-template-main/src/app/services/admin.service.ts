import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { ReqRes } from '../models/ReqRes';
import { AuthService } from './auth.service';
import { Observable } from 'rxjs';
import { User } from '../models/User';
import { environment } from '../../environments/environment';
@Injectable({
  providedIn: 'root'
})
export class AdminService {
 private BASE_URL=environment.apiUrl;

   constructor(private http: HttpClient,
    private authService : AuthService
   ) {}

   register(userData: ReqRes): Observable<ReqRes> {
    const token = this.authService.getToken();

   
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    });

    console.log("✅ Token envoyé dans les headers:", token);
    return this.http.post<ReqRes>(`${this.BASE_URL}/admin/register`, userData, { headers });
  }

 getAllUsers(): Observable<{ listusers: User[] }> {
  const token = this.authService.getToken();
  const headers = new HttpHeaders({
    'Authorization': `Bearer ${token}`
  });

  return this.http.get<{ listusers: User[] }>(`${this.BASE_URL}/admin/get-all-users`, { headers });
}
getManagers(): Observable<any> {
  const token = this.authService.getToken();
  const headers = new HttpHeaders({
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${token}`
  });
  return this.http.get<any>(`${this.BASE_URL}/admin/list-managers`, { headers });
}

getRecruteursByManager(managerId: number): Observable<User[]> {
  const token = this.authService.getToken();
  const headers = new HttpHeaders({
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${token}`
  });
  return this.http.get<User[]>(`${this.BASE_URL}/admin/recruteurs-par-manager/${managerId}`, { headers });
}



updateUser(id: number, userData: Partial<User>): Observable<ReqRes> {
  const token = this.authService.getToken();
  const headers = new HttpHeaders({
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json'
  });

  return this.http.put<ReqRes>(`${this.BASE_URL}/admin/update-user/${id}`, userData, { headers });
}

getUserById(id: number): Observable<{ user: User }> {
  const token = this.authService.getToken();
  const headers = new HttpHeaders({
    'Authorization': `Bearer ${token}`
  });

  return this.http.get<{ user: User }>(`${this.BASE_URL}/admin/get-user/${id}`, { headers });
}

block(id: number): Observable<{ user: User }> {
  const token = this.authService.getToken();
  const headers = new HttpHeaders({
    'Authorization': `Bearer ${token}`
  });

  return this.http.post<{ user: User }>(
    `${this.BASE_URL}/admin/block/${id}`,
    {},
    { headers }
  );
}



}
