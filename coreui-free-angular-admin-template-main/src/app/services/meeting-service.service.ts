import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Meeting } from '../models/Meeting';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class MeetingServiceService {

   private baseUrl = 'http://localhost:8080/manager/meetings';

  constructor(private http: HttpClient, private auth: AuthService) {}

  private h() {
    return { headers: new HttpHeaders({ Authorization: `Bearer ${this.auth.getToken()}` }) };
  }

  create(meet: Partial<Meeting>): Observable<Meeting> {
    return this.http.post<Meeting>(this.baseUrl, meet, this.h());
  }
  listMine(): Observable<Meeting[]> {
    return this.http.get<Meeting[]>(this.baseUrl, this.h());
  }
  get(id: number): Observable<Meeting> {
    return this.http.get<Meeting>(`${this.baseUrl}/${id}`, this.h());
  }
}
