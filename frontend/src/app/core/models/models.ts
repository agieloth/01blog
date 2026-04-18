// // ── AUTH ──────────────────────────────────────────────────────────────────────

// export interface LoginRequest {
//   identifier: string;  // email OU username (backend)
//   password: string;
// }

// export interface RegisterRequest {
//   username: string;
//   email: string;
//   password: string;
// }

// export interface AuthResponse {
//   token: string;
//   type: string;
//   id: number;
//   username: string;
//   email: string;
//   role: 'USER' | 'ADMIN';  // backend retourne un string enum, pas un tableau
// }

// // ── USER ──────────────────────────────────────────────────────────────────────

// export interface User {
//   id: number;
//   username: string;
//   email: string;
//   role: 'USER' | 'ADMIN';
// }

// // ── POST ──────────────────────────────────────────────────────────────────────

// export interface Post {
//   id: number;
//   title: string;
//   content: string;
//   authorId: number;
//   authorUsername: string;
//   imageUrls: string[];
//   likeCount: number;
//   likedByCurrentUser: boolean;
//   commentCount: number;
//   createdAt: string;
//   updatedAt?: string;
// }

// export interface CreatePostRequest {
//   title: string;
//   content: string;
// }

// // ── COMMENT ───────────────────────────────────────────────────────────────────

// export interface Comment {
//   id: number;
//   content: string;
//   authorId: number;
//   authorUsername: string;
//   postId: number;
//   createdAt: string;
// }

// export interface CreateCommentRequest {
//   content: string;
// }

// // ── ADMIN ─────────────────────────────────────────────────────────────────────

// export interface AdminUser {
//   id: number;
//   username: string;
//   email: string;
//   roles: string[];      // backend retourne ["USER"] ou ["ADMIN"]
//   postCount: number;
//   reportCount: number;
//   banned: boolean;
//   createdAt: string;
// }

// export interface AdminPost {
//   id: number;
//   title: string;
//   content: string;
//   author: string;
//   likeCount: number;
//   commentCount: number;
//   createdAt: string;
// }

// export interface AdminReport {
//   id: number;
//   reporterUsername: string;
//   reportedUsername: string;
//   reason: string;
//   description: string;
//   status: string;
//   createdAt: string;
// }



// ── AUTH ──────────────────────────────────────────────────────────────────────

export interface LoginRequest {
  identifier: string;  // email OU username (backend)
  password: string;
}

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
}

export interface AuthResponse {
  token: string;
  type: string;
  id: number;
  username: string;
  email: string;
  role: 'USER' | 'ADMIN';  // backend retourne un string enum, pas un tableau
}

// ── USER ──────────────────────────────────────────────────────────────────────

export interface User {
  id: number;
  username: string;
  email: string;
  role: 'USER' | 'ADMIN';
}

// ── POST ──────────────────────────────────────────────────────────────────────

export interface Post {
  id: number;
  title: string;
  content: string;
  authorId: number;
  authorUsername: string;
  imageUrls: string[];
  likeCount: number;
  likedByCurrentUser: boolean;
  commentCount: number;
  createdAt: string;
  updatedAt?: string;
}

export interface CreatePostRequest {
  title: string;
  content: string;
}

// ── COMMENT ───────────────────────────────────────────────────────────────────

export interface Comment {
  id: number;
  content: string;
  authorId: number;
  authorUsername: string;
  postId: number;
  createdAt: string;
}

export interface CreateCommentRequest {
  content: string;
}

// ── ADMIN ─────────────────────────────────────────────────────────────────────

export interface AdminUser {
  id: number;
  username: string;
  email: string;
  roles: string[];      // backend retourne ["USER"] ou ["ADMIN"]
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
  postId: number | null;      // null si signalement d'un user
  postTitle: string | null;   // null si signalement d'un user
  reason: string;
  description: string;
  status: string;
  createdAt: string;
}