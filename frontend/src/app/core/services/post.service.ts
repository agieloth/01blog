import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Post, CreatePostRequest, Comment, CreateCommentRequest } from '../models/models';

@Injectable({
  providedIn: 'root'
})
export class PostService {
  private apiUrl = `${environment.apiUrl}/posts`;

  constructor(private http: HttpClient) {}

  // ═══════════════════════════════════════════════════
  // POSTS
  // ═══════════════════════════════════════════════════

  getAllPosts(): Observable<Post[]> {
    return this.http.get<Post[]>(this.apiUrl);
  }

  getPostById(id: number): Observable<Post> {
    return this.http.get<Post>(`${this.apiUrl}/${id}`);
  }

  getPostsByUser(userId: number): Observable<Post[]> {
    return this.http.get<Post[]>(`${this.apiUrl}/user/${userId}`);
  }

  createPost(data: CreatePostRequest): Observable<Post> {
    return this.http.post<Post>(this.apiUrl, data);
  }

  updatePost(id: number, data: CreatePostRequest): Observable<Post> {
    return this.http.put<Post>(`${this.apiUrl}/${id}`, data);
  }

  deletePost(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  // ═══════════════════════════════════════════════════
  // LIKES
  // ═══════════════════════════════════════════════════

  toggleLike(postId: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/${postId}/like`, {});
  }

  // ═══════════════════════════════════════════════════
  // COMMENTS
  // ═══════════════════════════════════════════════════

  getComments(postId: number): Observable<Comment[]> {
    return this.http.get<Comment[]>(`${this.apiUrl}/${postId}/comments`);
  }

  addComment(postId: number, data: CreateCommentRequest): Observable<Comment> {
    return this.http.post<Comment>(`${this.apiUrl}/${postId}/comments`, data);
  }

  deleteComment(postId: number, commentId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${postId}/comments/${commentId}`);
  }

  // ═══════════════════════════════════════════════════
  // IMAGES UPLOAD
  // ═══════════════════════════════════════════════════

  uploadImages(postId: number, files: File[]): Observable<any> {
    const formData = new FormData();
    files.forEach(file => formData.append('images', file));
    return this.http.post(`${this.apiUrl}/${postId}/images`, formData);
  }
}