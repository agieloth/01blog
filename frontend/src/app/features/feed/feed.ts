// import { Component, OnInit, OnDestroy } from '@angular/core';
// import { CommonModule } from '@angular/common';
// import { FormsModule } from '@angular/forms';
// import { Router } from '@angular/router';
// import { Subscription } from 'rxjs';
// import { PostService } from '../../core/services/post.service';
// import { AuthService } from '../../core/services/auth.service';
// import { WebSocketService } from '../../core/services/websocket.service';
// import { ReportService } from '../../core/services/report.service';
// import { NavbarComponent } from '../../shared/navbar/navbar';
// import { Post, User } from '../../core/models/models';
// import { environment } from '../../../environments/environment';

// interface CommentItem {
//   id: number;
//   content: string;
//   authorId: number;
//   authorUsername: string;
//   createdAt: string;
// }

// @Component({
//   selector: 'app-feed',
//   standalone: true,
//   imports: [CommonModule, FormsModule, NavbarComponent],
//   templateUrl: './feed.html',
//   styleUrl: './feed.scss'
// })
// export class FeedComponent implements OnInit, OnDestroy {
//   posts: Post[] = [];
//   loading = true;
//   error = '';
//   currentUser: User | null = null;

//   // Create / Edit
//   showForm = false;
//   editingPostId: number | null = null;
//   postTitle = '';
//   postContent = '';
//   selectedImages: File[] = [];
//   imagePreviews: string[] = [];
//   formError = '';
//   formLoading = false;

//   // Confirmation modification
//   pendingEditPost: Post | null = null;

//   // Confirmation publication (nouveau post uniquement)
//   showPublishConfirm = false;

//   // Delete post
//   deletingPostId: number | null = null;
//   deleteLoading = false;

//   // Delete comment
//   pendingDeleteCommentPostId: number | null = null;
//   pendingDeleteCommentId: number | null = null;

//   // Comments
//   openComments: { [id: number]: boolean } = {};
//   comments: { [id: number]: CommentItem[] } = {};
//   commentInputs: { [id: number]: string } = {};
//   commentsLoading: { [id: number]: boolean } = {};

//   // Report
//   reportUserId: number | null = null;
//   reportUsername = '';
//   reportReason = '';
//   reportDescription = '';
//   reportError = '';
//   reportLoading = false;

//   // Expanded
//   expandedPosts: { [id: number]: boolean } = {};

//   private subs: Subscription[] = [];
//   readonly apiBase = environment.apiUrl.replace('/api', '');

//   readonly reportReasons = [
//     { value: 'SPAM', label: 'Spam' },
//     { value: 'HARASSMENT', label: 'Harcèlement' },
//     { value: 'INAPPROPRIATE_CONTENT', label: 'Contenu inapproprié' },
//     { value: 'HATE_SPEECH', label: 'Discours haineux' },
//     { value: 'OTHER', label: 'Autre' },
//   ];

//   constructor(
//     private postService: PostService,
//     public authService: AuthService,
//     private wsService: WebSocketService,
//     private reportService: ReportService,
//     private router: Router
//   ) {}

//   ngOnInit(): void {
//     this.currentUser = this.authService.getCurrentUser();
//     this.loadPosts();

//     this.subs.push(
//       this.wsService.postEvents.subscribe(event => {
//         if (event.type === 'POST_CREATED') {
//           if (event.data.authorId !== this.currentUser?.id) {
//             this.posts.unshift(event.data);
//           }
//         } else if (event.type === 'POST_UPDATED') {
//           const idx = this.posts.findIndex(p => p.id === event.data.id);
//           if (idx !== -1) this.posts[idx] = { ...this.posts[idx], ...event.data };
//         } else if (event.type === 'POST_DELETED') {
//           this.posts = this.posts.filter(p => p.id !== event.data);
//         } else if (event.type === 'COMMENT_COUNT_UPDATED') {
//           const post = this.posts.find(p => p.id === event.data.postId);
//           if (post) post.commentCount = event.data.count;
//         }
//       })
//     );

//     this.subs.push(
//       this.wsService.likeEvents.subscribe(update => {
//         const post = this.posts.find(p => p.id === update.postId);
//         if (post) post.likeCount = update.likeCount;
//       })
//     );
//   }

//   ngOnDestroy(): void {
//     this.subs.forEach(s => s.unsubscribe());
//   }

//   loadPosts(): void {
//     this.loading = true;
//     this.error = '';
//     this.postService.getAllPosts().subscribe({
//       next: posts => { this.posts = posts; this.loading = false; },
//       error: () => { this.error = 'Erreur lors du chargement.'; this.loading = false; }
//     });
//   }

//   // ── FORM ──────────────────────────────────────────────────────────────────

//   showCreate(): void {
//     this.editingPostId = null;
//     this.postTitle = '';
//     this.postContent = '';
//     this.clearImages();
//     this.formError = '';
//     this.showForm = true;
//     setTimeout(() => {
//       document.querySelector('.post-form-card')?.scrollIntoView({ behavior: 'smooth', block: 'start' });
//     }, 50);
//   }

//   // Confirmation avant modification
//   requestEdit(post: Post): void {
//     this.pendingEditPost = post;
//   }

//   confirmEdit(): void {
//     if (!this.pendingEditPost) return;
//     const post = this.pendingEditPost;
//     this.pendingEditPost = null;
//     this.editingPostId = post.id;
//     this.postTitle = post.title;
//     this.postContent = post.content;
//     this.clearImages();
//     this.formError = '';
//     this.showForm = true;
//     setTimeout(() => {
//       document.querySelector('.post-form-card')?.scrollIntoView({ behavior: 'smooth', block: 'start' });
//     }, 50);
//   }

//   cancelEdit(): void {
//     this.pendingEditPost = null;
//   }

//   cancelForm(): void {
//     this.showForm = false;
//     this.editingPostId = null;
//     this.clearImages();
//   }

//   // Appelé par le bouton "Publier" — ouvre la confirmation si c'est un nouveau post
//   requestSubmitPost(): void {
//     if (!this.postTitle.trim() || !this.postContent.trim()) {
//       this.formError = 'Le titre et le contenu sont requis.';
//       return;
//     }
//     this.formError = '';
//     if (this.editingPostId) {
//       // Modification : pas de confirmation supplémentaire, on publie directement
//       this.submitPost();
//     } else {
//       // Nouveau post : demande confirmation
//       this.showPublishConfirm = true;
//     }
//   }

//   cancelPublish(): void {
//     this.showPublishConfirm = false;
//   }

//   submitPost(): void {
//     this.showPublishConfirm = false;
//     this.formLoading = true;
//     this.formError = '';

//     const formData = new FormData();
//     formData.append('title', this.postTitle.trim());
//     formData.append('content', this.postContent.trim());
//     this.selectedImages.forEach(f => formData.append('images', f));

//     const req$ = this.editingPostId
//       ? this.postService.updatePostForm(this.editingPostId, formData)
//       : this.postService.createPostForm(formData);

//     req$.subscribe({
//       next: () => {
//         this.formLoading = false;
//         this.showForm = false;
//         this.editingPostId = null;
//         this.clearImages();
//         this.loadPosts();
//       },
//       error: (err) => {
//         this.formLoading = false;
//         this.formError = err.error?.message || 'Erreur lors de la publication.';
//       }
//     });
//   }

//   // ── IMAGES ────────────────────────────────────────────────────────────────

//   onImageSelect(event: Event): void {
//     const input = event.target as HTMLInputElement;
//     const files = Array.from(input.files || []);
//     if (!files.length) return;

//     if (this.selectedImages.length + files.length > 3) {
//       this.formError = 'Maximum 3 images par post.';
//       input.value = '';
//       return;
//     }
//     for (const f of files) {
//       if (f.size > 5 * 1024 * 1024) {
//         this.formError = 'Chaque image doit faire moins de 5 MB.';
//         input.value = '';
//         return;
//       }
//     }

//     this.selectedImages.push(...files);
//     files.forEach(f => {
//       const reader = new FileReader();
//       reader.onload = e => this.imagePreviews.push(e.target?.result as string);
//       reader.readAsDataURL(f);
//     });
//     this.formError = '';
//     input.value = '';
//   }

//   removeImage(i: number): void {
//     this.selectedImages.splice(i, 1);
//     this.imagePreviews.splice(i, 1);
//   }

//   clearImages(): void {
//     this.selectedImages = [];
//     this.imagePreviews = [];
//   }

//   // ── DELETE POST ───────────────────────────────────────────────────────────

//   openDelete(id: number): void { this.deletingPostId = id; }
//   closeDelete(): void { this.deletingPostId = null; }

//   confirmDelete(): void {
//     if (!this.deletingPostId) return;
//     this.deleteLoading = true;
//     this.postService.deletePost(this.deletingPostId).subscribe({
//       next: () => {
//         this.posts = this.posts.filter(p => p.id !== this.deletingPostId);
//         this.deletingPostId = null;
//         this.deleteLoading = false;
//       },
//       error: () => { this.deleteLoading = false; this.closeDelete(); }
//     });
//   }

//   // ── LIKES ─────────────────────────────────────────────────────────────────

//   toggleLike(postId: number): void {
//     this.postService.toggleLike(postId).subscribe({
//       next: data => {
//         const post = this.posts.find(p => p.id === postId);
//         if (post) {
//           post.likeCount = data.likeCount;
//           post.likedByCurrentUser = data.likedByCurrentUser;
//         }
//       }
//     });
//   }

//   // ── COMMENTS ──────────────────────────────────────────────────────────────

//   toggleComments(postId: number): void {
//     this.openComments[postId] = !this.openComments[postId];
//     if (this.openComments[postId] && !this.comments[postId]) {
//       this.loadComments(postId);
//       this.wsService.subscribeToComments(postId).subscribe(event => {
//         if (event.type === 'COMMENT_ADDED' || event.type === 'COMMENT_DELETED') {
//           this.loadComments(postId);
//         }
//       });
//     }
//   }

//   loadComments(postId: number): void {
//     this.commentsLoading[postId] = true;
//     this.postService.getComments(postId).subscribe({
//       next: data => { this.comments[postId] = data as CommentItem[]; this.commentsLoading[postId] = false; },
//       error: () => { this.commentsLoading[postId] = false; }
//     });
//   }

//   submitComment(postId: number): void {
//     const content = (this.commentInputs[postId] || '').trim();
//     if (!content) return;
//     this.postService.addComment(postId, { content }).subscribe({
//       next: () => {
//         this.commentInputs[postId] = '';
//         const post = this.posts.find(p => p.id === postId);
//         if (post) post.commentCount++;
//         this.loadComments(postId);
//       }
//     });
//   }

//   // Ouvre la confirmation avant de supprimer un commentaire
//   requestDeleteComment(postId: number, commentId: number): void {
//     this.pendingDeleteCommentPostId = postId;
//     this.pendingDeleteCommentId = commentId;
//   }

//   cancelDeleteComment(): void {
//     this.pendingDeleteCommentPostId = null;
//     this.pendingDeleteCommentId = null;
//   }

//   confirmDeleteComment(): void {
//     if (!this.pendingDeleteCommentPostId || !this.pendingDeleteCommentId) return;
//     const postId = this.pendingDeleteCommentPostId;
//     const commentId = this.pendingDeleteCommentId;
//     this.pendingDeleteCommentPostId = null;
//     this.pendingDeleteCommentId = null;

//     this.postService.deleteComment(postId, commentId).subscribe({
//       next: () => {
//         const post = this.posts.find(p => p.id === postId);
//         if (post && post.commentCount > 0) post.commentCount--;
//         this.loadComments(postId);
//       }
//     });
//   }

//   onCommentKey(e: KeyboardEvent, postId: number): void {
//     if (e.key === 'Enter' && !e.shiftKey) { e.preventDefault(); this.submitComment(postId); }
//   }

//   // ── REPORT ────────────────────────────────────────────────────────────────

//   openReport(userId: number, username: string): void {
//     this.reportUserId = userId;
//     this.reportUsername = username;
//     this.reportReason = '';
//     this.reportDescription = '';
//     this.reportError = '';
//   }

//   closeReport(): void { this.reportUserId = null; }
//   selectReason(r: string): void { this.reportReason = r; this.reportError = ''; }

//   submitReport(): void {
//     if (!this.reportReason) { this.reportError = 'Sélectionnez une raison.'; return; }
//     if (!this.reportUserId) return;
//     this.reportLoading = true;
//     this.reportService.reportUser(this.reportUserId, {
//       reason: this.reportReason,
//       description: this.reportDescription || undefined
//     }).subscribe({
//       next: () => { this.reportLoading = false; this.closeReport(); },
//       error: (err) => {
//         this.reportLoading = false;
//         this.reportError = err.error?.message || 'Erreur lors du signalement.';
//       }
//     });
//   }

//   // ── HELPERS ───────────────────────────────────────────────────────────────

//   toggleExpand(postId: number): void {
//     this.expandedPosts[postId] = !this.expandedPosts[postId];
//   }

//   isOwn(post: Post): boolean {
//     return this.currentUser?.id === post.authorId;
//   }

//   goToProfile(userId: number): void {
//     this.router.navigate(['/profile', userId]);
//   }

//   getInitial(u?: string): string { return u ? u[0].toUpperCase() : '?'; }

//   getImageUrl(url: string): string {
//     if (!url) return '';
//     if (url.startsWith('http')) return url;
//     return `${this.apiBase}${url}`;
//   }

//   formatDate(s: string): string {
//     const d = new Date(s), diff = (Date.now() - d.getTime()) / 1000;
//     if (diff < 60) return "à l'instant";
//     if (diff < 3600) return `il y a ${Math.floor(diff / 60)} min`;
//     if (diff < 86400) return `il y a ${Math.floor(diff / 3600)} h`;
//     return d.toLocaleDateString('fr-FR', { day: 'numeric', month: 'long', year: 'numeric' });
//   }
// }


import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { PostService } from '../../core/services/post.service';
import { AuthService } from '../../core/services/auth.service';
import { WebSocketService } from '../../core/services/websocket.service';
import { ReportService } from '../../core/services/report.service';
import { ToastService } from '../../core/services/toast.service';
import { NavbarComponent } from '../../shared/navbar/navbar';
import { Post, User } from '../../core/models/models';
import { environment } from '../../../environments/environment';

interface CommentItem {
  id: number;
  content: string;
  authorId: number;
  authorUsername: string;
  createdAt: string;
}

@Component({
  selector: 'app-feed',
  standalone: true,
  imports: [CommonModule, FormsModule, NavbarComponent],
  templateUrl: './feed.html',
  styleUrl: './feed.scss'
})
export class FeedComponent implements OnInit, OnDestroy {
  posts: Post[] = [];
  loading = true;
  error = '';
  currentUser: User | null = null;

  // Create / Edit
  showForm = false;
  editingPostId: number | null = null;
  postTitle = '';
  postContent = '';
  selectedImages: File[] = [];
  imagePreviews: string[] = [];
  formError = '';
  formLoading = false;

  // Confirmation modification
  pendingEditPost: Post | null = null;

  // Confirmation publication (nouveau post uniquement)
  showPublishConfirm = false;

  // Delete post
  deletingPostId: number | null = null;
  deleteLoading = false;

  // Delete comment
  pendingDeleteCommentPostId: number | null = null;
  pendingDeleteCommentId: number | null = null;

  // Comments
  openComments: { [id: number]: boolean } = {};
  comments: { [id: number]: CommentItem[] } = {};
  commentInputs: { [id: number]: string } = {};
  commentsLoading: { [id: number]: boolean } = {};

  // Report
  reportUserId: number | null = null;
  reportUsername = '';
  reportReason = '';
  reportDescription = '';
  reportError = '';
  reportLoading = false;

  // Expanded
  expandedPosts: { [id: number]: boolean } = {};

  private subs: Subscription[] = [];
  readonly apiBase = environment.apiUrl.replace('/api', '');

  readonly reportReasons = [
    { value: 'SPAM', label: 'Spam' },
    { value: 'HARASSMENT', label: 'Harcèlement' },
    { value: 'INAPPROPRIATE_CONTENT', label: 'Contenu inapproprié' },
    { value: 'HATE_SPEECH', label: 'Discours haineux' },
    { value: 'OTHER', label: 'Autre' },
  ];

  constructor(
    private postService: PostService,
    public authService: AuthService,
    private wsService: WebSocketService,
    private reportService: ReportService,
    private toast: ToastService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.currentUser = this.authService.getCurrentUser();
    this.loadPosts();

    this.subs.push(
      this.wsService.postEvents.subscribe(event => {
        if (event.type === 'POST_CREATED') {
          if (event.data.authorId !== this.currentUser?.id) {
            this.posts.unshift(event.data);
          }
        } else if (event.type === 'POST_UPDATED') {
          const idx = this.posts.findIndex(p => p.id === event.data.id);
          if (idx !== -1) this.posts[idx] = { ...this.posts[idx], ...event.data };
        } else if (event.type === 'POST_DELETED') {
          this.posts = this.posts.filter(p => p.id !== event.data);
        } else if (event.type === 'COMMENT_COUNT_UPDATED') {
          const post = this.posts.find(p => p.id === event.data.postId);
          if (post) post.commentCount = event.data.count;
        }
      })
    );

    this.subs.push(
      this.wsService.likeEvents.subscribe(update => {
        const post = this.posts.find(p => p.id === update.postId);
        if (post) post.likeCount = update.likeCount;
      })
    );
  }

  ngOnDestroy(): void {
    this.subs.forEach(s => s.unsubscribe());
  }

  loadPosts(): void {
    this.loading = true;
    this.error = '';
    this.postService.getAllPosts().subscribe({
      next: posts => { this.posts = posts; this.loading = false; },
      error: () => { this.error = 'Erreur lors du chargement.'; this.loading = false; }
    });
  }

  // ── FORM ──────────────────────────────────────────────────────────────────

  showCreate(): void {
    this.editingPostId = null;
    this.postTitle = '';
    this.postContent = '';
    this.clearImages();
    this.formError = '';
    this.showForm = true;
    setTimeout(() => {
      document.querySelector('.post-form-card')?.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }, 50);
  }

  // Confirmation avant modification
  requestEdit(post: Post): void {
    this.pendingEditPost = post;
  }

  confirmEdit(): void {
    if (!this.pendingEditPost) return;
    const post = this.pendingEditPost;
    this.pendingEditPost = null;
    this.editingPostId = post.id;
    this.postTitle = post.title;
    this.postContent = post.content;
    this.clearImages();
    this.formError = '';
    this.showForm = true;
    setTimeout(() => {
      document.querySelector('.post-form-card')?.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }, 50);
  }

  cancelEdit(): void {
    this.pendingEditPost = null;
  }

  cancelForm(): void {
    this.showForm = false;
    this.editingPostId = null;
    this.clearImages();
  }

  // Appelé par le bouton "Publier" — ouvre la confirmation si c'est un nouveau post
  requestSubmitPost(): void {
    if (!this.postTitle.trim() || !this.postContent.trim()) {
      this.formError = 'Le titre et le contenu sont requis.';
      return;
    }
    this.formError = '';
    if (this.editingPostId) {
      // Modification : pas de confirmation supplémentaire, on publie directement
      this.submitPost();
    } else {
      // Nouveau post : demande confirmation
      this.showPublishConfirm = true;
    }
  }

  cancelPublish(): void {
    this.showPublishConfirm = false;
  }

  submitPost(): void {
    this.showPublishConfirm = false;
    this.formLoading = true;
    this.formError = '';

    const formData = new FormData();
    formData.append('title', this.postTitle.trim());
    formData.append('content', this.postContent.trim());
    this.selectedImages.forEach(f => formData.append('images', f));

    const req$ = this.editingPostId
      ? this.postService.updatePostForm(this.editingPostId, formData)
      : this.postService.createPostForm(formData);

    const isEdit = !!this.editingPostId;

    req$.subscribe({
      next: () => {
        this.formLoading = false;
        this.showForm = false;
        this.editingPostId = null;
        this.clearImages();
        this.loadPosts();
        this.toast.success(isEdit ? 'Post modifié avec succès !' : 'Post publié avec succès !');
      },
      error: (err) => {
        this.formLoading = false;
        this.formError = err.error?.message || 'Erreur lors de la publication.';
        this.toast.error(this.formError);
      }
    });
  }

  // ── IMAGES ────────────────────────────────────────────────────────────────

  onImageSelect(event: Event): void {
    const input = event.target as HTMLInputElement;
    const files = Array.from(input.files || []);
    if (!files.length) return;

    if (this.selectedImages.length + files.length > 3) {
      this.formError = 'Maximum 3 images par post.';
      input.value = '';
      return;
    }
    for (const f of files) {
      if (f.size > 5 * 1024 * 1024) {
        this.formError = 'Chaque image doit faire moins de 5 MB.';
        input.value = '';
        return;
      }
    }

    this.selectedImages.push(...files);
    files.forEach(f => {
      const reader = new FileReader();
      reader.onload = e => this.imagePreviews.push(e.target?.result as string);
      reader.readAsDataURL(f);
    });
    this.formError = '';
    input.value = '';
  }

  removeImage(i: number): void {
    this.selectedImages.splice(i, 1);
    this.imagePreviews.splice(i, 1);
  }

  clearImages(): void {
    this.selectedImages = [];
    this.imagePreviews = [];
  }

  // ── DELETE POST ───────────────────────────────────────────────────────────

  openDelete(id: number): void { this.deletingPostId = id; }
  closeDelete(): void { this.deletingPostId = null; }

  confirmDelete(): void {
    if (!this.deletingPostId) return;
    this.deleteLoading = true;
    this.postService.deletePost(this.deletingPostId).subscribe({
      next: () => {
        this.posts = this.posts.filter(p => p.id !== this.deletingPostId);
        this.deletingPostId = null;
        this.deleteLoading = false;
        this.toast.success('Post supprimé.');
      },
      error: () => {
        this.deleteLoading = false;
        this.closeDelete();
        this.toast.error('Erreur lors de la suppression du post.');
      }
    });
  }

  // ── LIKES ─────────────────────────────────────────────────────────────────

  toggleLike(postId: number): void {
    this.postService.toggleLike(postId).subscribe({
      next: data => {
        const post = this.posts.find(p => p.id === postId);
        if (post) {
          post.likeCount = data.likeCount;
          post.likedByCurrentUser = data.likedByCurrentUser;
        }
      }
    });
  }

  // ── COMMENTS ──────────────────────────────────────────────────────────────

  toggleComments(postId: number): void {
    this.openComments[postId] = !this.openComments[postId];
    if (this.openComments[postId] && !this.comments[postId]) {
      this.loadComments(postId);
      this.wsService.subscribeToComments(postId).subscribe(event => {
        if (event.type === 'COMMENT_ADDED' || event.type === 'COMMENT_DELETED') {
          this.loadComments(postId);
        }
      });
    }
  }

  loadComments(postId: number): void {
    this.commentsLoading[postId] = true;
    this.postService.getComments(postId).subscribe({
      next: data => { this.comments[postId] = data as CommentItem[]; this.commentsLoading[postId] = false; },
      error: () => { this.commentsLoading[postId] = false; }
    });
  }

  submitComment(postId: number): void {
    const content = (this.commentInputs[postId] || '').trim();
    if (!content) return;
    this.postService.addComment(postId, { content }).subscribe({
      next: () => {
        this.commentInputs[postId] = '';
        const post = this.posts.find(p => p.id === postId);
        if (post) post.commentCount++;
        this.loadComments(postId);
      }
    });
  }

  // Ouvre la confirmation avant de supprimer un commentaire
  requestDeleteComment(postId: number, commentId: number): void {
    this.pendingDeleteCommentPostId = postId;
    this.pendingDeleteCommentId = commentId;
  }

  cancelDeleteComment(): void {
    this.pendingDeleteCommentPostId = null;
    this.pendingDeleteCommentId = null;
  }

  confirmDeleteComment(): void {
    if (!this.pendingDeleteCommentPostId || !this.pendingDeleteCommentId) return;
    const postId = this.pendingDeleteCommentPostId;
    const commentId = this.pendingDeleteCommentId;
    this.pendingDeleteCommentPostId = null;
    this.pendingDeleteCommentId = null;

    this.postService.deleteComment(postId, commentId).subscribe({
      next: () => {
        const post = this.posts.find(p => p.id === postId);
        if (post && post.commentCount > 0) post.commentCount--;
        this.loadComments(postId);
        this.toast.success('Commentaire supprimé.');
      },
      error: () => {
        this.toast.error('Erreur lors de la suppression du commentaire.');
      }
    });
  }

  onCommentKey(e: KeyboardEvent, postId: number): void {
    if (e.key === 'Enter' && !e.shiftKey) { e.preventDefault(); this.submitComment(postId); }
  }

  // ── REPORT ────────────────────────────────────────────────────────────────

  openReport(userId: number, username: string): void {
    this.reportUserId = userId;
    this.reportUsername = username;
    this.reportReason = '';
    this.reportDescription = '';
    this.reportError = '';
  }

  closeReport(): void { this.reportUserId = null; }
  selectReason(r: string): void { this.reportReason = r; this.reportError = ''; }

  submitReport(): void {
    if (!this.reportReason) { this.reportError = 'Sélectionnez une raison.'; return; }
    if (!this.reportUserId) return;
    this.reportLoading = true;
    this.reportService.reportUser(this.reportUserId, {
      reason: this.reportReason,
      description: this.reportDescription || undefined
    }).subscribe({
      next: () => {
        this.reportLoading = false;
        this.closeReport();
        this.toast.success('Signalement envoyé.');
      },
      error: (err) => {
        this.reportLoading = false;
        this.reportError = err.error?.message || 'Erreur lors du signalement.';
        this.toast.error(this.reportError);
      }
    });
  }

  // ── HELPERS ───────────────────────────────────────────────────────────────

  toggleExpand(postId: number): void {
    this.expandedPosts[postId] = !this.expandedPosts[postId];
  }

  isOwn(post: Post): boolean {
    return this.currentUser?.id === post.authorId;
  }

  goToProfile(userId: number): void {
    this.router.navigate(['/profile', userId]);
  }

  getInitial(u?: string): string { return u ? u[0].toUpperCase() : '?'; }

  getImageUrl(url: string): string {
    if (!url) return '';
    if (url.startsWith('http')) return url;
    return `${this.apiBase}${url}`;
  }

  formatDate(s: string): string {
    const d = new Date(s), diff = (Date.now() - d.getTime()) / 1000;
    if (diff < 60) return "à l'instant";
    if (diff < 3600) return `il y a ${Math.floor(diff / 60)} min`;
    if (diff < 86400) return `il y a ${Math.floor(diff / 3600)} h`;
    return d.toLocaleDateString('fr-FR', { day: 'numeric', month: 'long', year: 'numeric' });
  }
}