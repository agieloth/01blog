// ═══════════════════════════════════════════════════
// MODELS - Interfaces TypeScript pour le backend
// ═══════════════════════════════════════════════════

export interface User {
  id: number;
  username: string;
  email: string;
  role: 'USER' | 'ADMIN';
  isBanned?: boolean;
  createdAt?: string;
}

export interface Post {
  id: number;
  title: string;
  content: string;
  imageUrls: string[];
  author: User;
  likeCount: number;
  commentCount: number;
  isLikedByCurrentUser: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface Comment {
  id: number;
  content: string;
  user: User;
  postId: number;
  createdAt: string;
}

export interface Notification {
  id: number;
  type: 'LIKE' | 'COMMENT' | 'FOLLOW';
  message: string;
  read: boolean;
  createdAt: string;
  relatedUserId?: number;
  relatedPostId?: number;
}

export interface Report {
  id: number;
  reporter: User;
  reportedUser: User;
  reason: 'SPAM' | 'HARASSMENT' | 'INAPPROPRIATE_CONTENT' | 'HATE_SPEECH' | 'OTHER';
  description?: string;
  status: 'PENDING' | 'REVIEWED' | 'DISMISSED';
  createdAt: string;
}

// DTOs pour les requêtes
export interface LoginRequest {
  identifier: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
}

export interface AuthResponse {
  token: string;
  id: number;
  username: string;
  email: string;
  role: string;
}

export interface CreatePostRequest {
  title: string;
  content: string;
}

export interface CreateCommentRequest {
  content: string;
}

export interface CreateReportRequest {
  reason: string;
  description?: string;
}

// Admin DTOs
export interface AdminUser {
  id: number;
  username: string;
  email: string;
  roles: string[];
  postCount: number;
  reportCount: number;
  banned: boolean;
  createdAt: string;
}

export interface AdminPost {
  id: number;
  title: string;
  content: string;
  author: string;
  likeCount: number;
  commentCount: number;
  createdAt: string;
}

export interface AdminReport {
  id: number;
  reporterUsername: string;
  reportedUsername: string;
  reason: string;
  description?: string;
  status: string;
  createdAt: string;
}