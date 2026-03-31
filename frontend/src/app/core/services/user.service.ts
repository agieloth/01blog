import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Post } from '../models/models';

export interface UserStatsResponse {
  userId: number;
  username: string;
  postCount: number;
  followerCount: number;
  followingCount: number;
  followedByCurrentUser: boolean;
}

export interface FollowResponse {
  following: boolean;
  followerCount: number;
}

@Injectable({ providedIn: 'root' })
export class UserService {
  private apiUrl = `${environment.apiUrl}/users`;

  constructor(private http: HttpClient) {}

  getUserStats(userId: number): Observable<UserStatsResponse> {
    return this.http.get<UserStatsResponse>(`${this.apiUrl}/${userId}/stats`);
  }

  toggleFollow(userId: number): Observable<FollowResponse> {
    return this.http.post<FollowResponse>(`${this.apiUrl}/${userId}/follow`, {});
  }

  getUserPosts(userId: number): Observable<Post[]> {
    return this.http.get<Post[]>(`${this.apiUrl}/${userId}/posts`);
  }
}