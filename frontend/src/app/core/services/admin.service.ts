import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AdminUser, AdminPost, AdminReport } from '../models/models';

@Injectable({ providedIn: 'root' })
export class AdminService {
  private apiUrl = `${environment.apiUrl}/admin`;

  constructor(private http: HttpClient) {}

  // Users
  getUsers(): Observable<AdminUser[]> {
    return this.http.get<AdminUser[]>(`${this.apiUrl}/users`);
  }

  toggleBanUser(userId: number): Observable<{ id: number; username: string; banned: boolean }> {
    return this.http.patch<{ id: number; username: string; banned: boolean }>(
      `${this.apiUrl}/users/${userId}/ban`, {}
    );
  }

  // Posts
  getPosts(): Observable<AdminPost[]> {
    return this.http.get<AdminPost[]>(`${this.apiUrl}/posts`);
  }

  deletePost(postId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/posts/${postId}`);
  }

  toggleHidePost(postId: number): Observable<{ id: number; title: string; hidden: boolean }> {
    return this.http.patch<{ id: number; title: string; hidden: boolean }>(
      `${this.apiUrl}/posts/${postId}/hide`, {}
    );
  }

  // Reports
  getReports(): Observable<AdminReport[]> {
    return this.http.get<AdminReport[]>(`${this.apiUrl}/reports`);
  }

  updateReportStatus(reportId: number, status: string): Observable<{ id: number; status: string }> {
    return this.http.patch<{ id: number; status: string }>(
      `${this.apiUrl}/reports/${reportId}/status`, { status }
    );
  }
}