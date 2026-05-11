import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface CreateReportRequest {
  reason: string;
  description?: string;
}

@Injectable({ providedIn: 'root' })
export class ReportService {
  private apiUrl = `${environment.apiUrl}/reports`;

  constructor(private http: HttpClient) {}

  /** Signale un post → POST /api/reports/post/:postId */
  reportPost(postId: number, request: CreateReportRequest): Observable<any> {
    return this.http.post(`${this.apiUrl}/post/${postId}`, request);
  }

  /** Signale un utilisateur → POST /api/reports/user/:userId */
  reportUser(userId: number, request: CreateReportRequest): Observable<any> {
    return this.http.post(`${this.apiUrl}/user/${userId}`, request);
  }
}