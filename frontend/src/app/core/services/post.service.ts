import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Post, Comment, CreateCommentRequest } from '../models/models';

@Injectable({ providedIn: 'root' })
export class PostService {
  private apiUrl = `${environment.apiUrl}/posts`;

  constructor(private http: HttpClient) {}

  // ── POSTS ─────────────────────────────────────────

  getAllPosts(): Observable<Post[]> {
    return this.http.get<Post[]>(this.apiUrl);
  }

  getPostById(id: number): Observable<Post> {
    return this.http.get<Post>(`${this.apiUrl}/${id}`);
  }

  createPostForm(formData: FormData): Observable<Post> {
    return this.http.post<Post>(this.apiUrl, formData);
  }

  updatePostForm(id: number, formData: FormData): Observable<Post> {
    return this.http.put<Post>(`${this.apiUrl}/${id}`, formData);
  }

  deletePost(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  // ── LIKES ─────────────────────────────────────────

  toggleLike(postId: number): Observable<{ postId: number; likeCount: number; likedByCurrentUser: boolean }> {
    return this.http.post<{ postId: number; likeCount: number; likedByCurrentUser: boolean }>(
      `${this.apiUrl}/${postId}/like`, {}
    );
  }

  // ── COMMENTS ──────────────────────────────────────

  getComments(postId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/${postId}/comments`);
  }

  addComment(postId: number, data: CreateCommentRequest): Observable<Comment> {
    return this.http.post<Comment>(`${this.apiUrl}/${postId}/comments`, data);
  }

  deleteComment(postId: number, commentId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${postId}/comments/${commentId}`);
  }
}