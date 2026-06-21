// import { Component, OnInit, OnDestroy } from '@angular/core';
// import { CommonModule } from '@angular/common';
// import { ActivatedRoute, Router } from '@angular/router';
// import { forkJoin, Subscription } from 'rxjs';
// import { UserService, UserStatsResponse } from '../../core/services/user.service';
// import { PostService } from '../../core/services/post.service';
// import { ReportService } from '../../core/services/report.service';
// import { AuthService } from '../../core/services/auth.service';
// import { WebSocketService } from '../../core/services/websocket.service';
// import { NavbarComponent } from '../../shared/navbar/navbar';
// import { Post, User } from '../../core/models/models';
// import { environment } from '../../../environments/environment';
// import { FormsModule } from '@angular/forms';

// @Component({
//   selector: 'app-profile',
//   standalone: true,
//   imports: [CommonModule, FormsModule, NavbarComponent],
//   templateUrl: './profile.html',
//   styleUrl: './profile.scss'
// })
// export class ProfileComponent implements OnInit, OnDestroy {
//   stats: UserStatsResponse | null = null;
//   posts: Post[] = [];
//   loading = true;
//   error = '';
//   currentUser: User | null = null;
//   profileUserId!: number;

//   // Report
//   reportReason = '';
//   reportDescription = '';
//   reportError = '';
//   reportLoading = false;
//   showReport = false;

//   readonly reportReasons = [
//     { value: 'SPAM', label: 'Spam' },
//     { value: 'HARASSMENT', label: 'Harcèlement' },
//     { value: 'INAPPROPRIATE_CONTENT', label: 'Contenu inapproprié' },
//     { value: 'HATE_SPEECH', label: 'Discours haineux' },
//     { value: 'OTHER', label: 'Autre' },
//   ];

//   private subs: Subscription[] = [];
//   private apiBase = environment.apiUrl.replace('/api', '');

//   constructor(
//     private route: ActivatedRoute,
//     private router: Router,
//     private userService: UserService,
//     private postService: PostService,
//     private reportService: ReportService,
//     public authService: AuthService,
//     private wsService: WebSocketService
//   ) {}

//   ngOnInit(): void {
//     this.currentUser = this.authService.getCurrentUser();

//     this.subs.push(
//       this.route.paramMap.subscribe(params => {
//         this.profileUserId = Number(params.get('id'));
//         this.loadProfile();
//       })
//     );

//     this.subs.push(
//       this.wsService.followEvents.subscribe(update => {
//         if (this.stats && update.userId === this.profileUserId) {
//           this.stats.followerCount = update.followerCount;
//         }
//       })
//     );
//   }

//   ngOnDestroy(): void {
//     this.subs.forEach(s => s.unsubscribe());
//   }

//   loadProfile(): void {
//     this.loading = true;
//     this.error = '';

//     // FIX : forkJoin pour lancer les deux appels HTTP en parallèle
//     // au lieu d'imbriquer (getUserStats → getUserPosts en séquence).
//     // Gain de performance : les deux requêtes partent simultanément.
//     forkJoin({
//       stats: this.userService.getUserStats(this.profileUserId),
//       posts: this.userService.getUserPosts(this.profileUserId)
//     }).subscribe({
//       next: ({ stats, posts }) => {
//         this.stats = stats;
//         this.posts = posts;
//         this.loading = false;
//       },
//       error: () => {
//         this.error = 'Profil introuvable.';
//         this.loading = false;
//       }
//     });
//   }

//   toggleFollow(): void {
//     this.userService.toggleFollow(this.profileUserId).subscribe({
//       next: data => {
//         if (this.stats) {
//           this.stats.followedByCurrentUser = data.following;
//           this.stats.followerCount = data.followerCount;
//         }
//       }
//     });
//   }

//   get isOwn(): boolean {
//     return this.currentUser?.id === this.profileUserId;
//   }

//   get isAuthenticated(): boolean {
//     return this.authService.isAuthenticated();
//   }

//   goBack(): void { this.router.navigate(['/feed']); }
//   goToProfile(userId: number): void { this.router.navigate(['/profile', userId]); }

//   toggleExpanded: { [postId: number]: boolean } = {};
//   toggleExpand(postId: number): void { this.toggleExpanded[postId] = !this.toggleExpanded[postId]; }

//   isOwnPost(post: Post): boolean { return this.currentUser?.id === post.authorId; }

//   getInitial(u?: string): string { return u ? u[0].toUpperCase() : '?'; }

//   getImageUrl(url: string): string {
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

//   openReport(): void { this.showReport = true; this.reportReason = ''; this.reportDescription = ''; this.reportError = ''; }
//   closeReport(): void { this.showReport = false; }
//   selectReason(r: string): void { this.reportReason = r; this.reportError = ''; }

//   submitReport(): void {
//     if (!this.reportReason) { this.reportError = 'Sélectionnez une raison.'; return; }
//     this.reportLoading = true;
//     this.reportService.reportUser(this.profileUserId, {
//       reason: this.reportReason,
//       description: this.reportDescription || undefined
//     }).subscribe({
//       next: () => { this.reportLoading = false; this.showReport = false; },
//       error: (err) => {
//         this.reportLoading = false;
//         this.reportError = err.error?.message || 'Erreur lors du signalement.';
//       }
//     });
//   }
// }





import { Component, OnInit, OnDestroy } from '@angular/core';

import { CommonModule } from '@angular/common';

import { ActivatedRoute, Router } from '@angular/router';

import { Subscription } from 'rxjs';

import { UserService, UserStatsResponse } from '../../core/services/user.service';

import { PostService } from '../../core/services/post.service';

import { ReportService } from '../../core/services/report.service';

import { ToastService } from '../../core/services/toast.service';

import { AuthService } from '../../core/services/auth.service';

import { WebSocketService } from '../../core/services/websocket.service';

import { NavbarComponent } from '../../shared/navbar/navbar';

import { Post, User } from '../../core/models/models';

import { environment } from '../../../environments/environment';

import { FormsModule } from '@angular/forms';



@Component({

  selector: 'app-profile',

  standalone: true,

  imports: [CommonModule, FormsModule, NavbarComponent],

  templateUrl: './profile.html',

  styleUrl: './profile.scss'

})

export class ProfileComponent implements OnInit, OnDestroy {

  stats: UserStatsResponse | null = null;

  posts: Post[] = [];

  loading = true;

  error = '';

  currentUser: User | null = null;

  profileUserId!: number;



  // Confirmation d'abonnement / désabonnement

  showFollowConfirm = false;

  followLoading = false;



  // Report

  reportReason = '';

  reportDescription = '';

  reportError = '';

  reportLoading = false;

  showReport = false;



  readonly reportReasons = [

    { value: 'SPAM', label: 'Spam' },

    { value: 'HARASSMENT', label: 'Harcèlement' },

    { value: 'INAPPROPRIATE_CONTENT', label: 'Contenu inapproprié' },

    { value: 'HATE_SPEECH', label: 'Discours haineux' },

    { value: 'OTHER', label: 'Autre' },

  ];



  private subs: Subscription[] = [];

  private apiBase = environment.apiUrl.replace('/api', '');



  constructor(

    private route: ActivatedRoute,

    private router: Router,

    private userService: UserService,

    private postService: PostService,

    private reportService: ReportService,

    private toast: ToastService,

    public authService: AuthService,

    private wsService: WebSocketService

  ) {}



  ngOnInit(): void {

    this.currentUser = this.authService.getCurrentUser();



    this.subs.push(

      this.route.paramMap.subscribe(params => {

        this.profileUserId = Number(params.get('id'));

        this.loadProfile();

      })

    );



    this.subs.push(

      this.wsService.followEvents.subscribe(update => {

        if (this.stats && update.userId === this.profileUserId) {

          this.stats.followerCount = update.followerCount;

        }

      })

    );

  }



  ngOnDestroy(): void {

    this.subs.forEach(s => s.unsubscribe());

  }



  loadProfile(): void {

    this.loading = true;

    this.error = '';



    this.userService.getUserStats(this.profileUserId).subscribe({

      next: stats => {

        this.stats = stats;

        this.userService.getUserPosts(this.profileUserId).subscribe({

          next: posts => { this.posts = posts; this.loading = false; },

          error: () => { this.loading = false; }

        });

      },

      error: () => { this.error = 'Profil introuvable.'; this.loading = false; }

    });

  }



  // ── FOLLOW ────────────────────────────────────────────────────────────────



  requestFollow(): void {

    this.showFollowConfirm = true;

  }



  cancelFollow(): void {

    this.showFollowConfirm = false;

  }



  confirmFollow(): void {

    this.followLoading = true;

    this.userService.toggleFollow(this.profileUserId).subscribe({

      next: data => {

        if (this.stats) {

          this.stats.followedByCurrentUser = data.following;

          this.stats.followerCount = data.followerCount;

        }

        this.followLoading = false;

        this.showFollowConfirm = false;

        this.toast.success(data.following ? `Vous suivez maintenant ${this.stats?.username}.` : `Vous ne suivez plus ${this.stats?.username}.`);

      },

      error: () => {

        this.followLoading = false;

        this.showFollowConfirm = false;

        this.toast.error("Erreur lors de l'opération.");

      }

    });

  }



  // ── HELPERS ───────────────────────────────────────────────────────────────



  get isOwn(): boolean {

    return this.currentUser?.id === this.profileUserId;

  }



  get isAuthenticated(): boolean {

    return this.authService.isAuthenticated();

  }



  goBack(): void { this.router.navigate(['/feed']); }

  goToProfile(userId: number): void { this.router.navigate(['/profile', userId]); }



  toggleExpanded: { [postId: number]: boolean } = {};

  toggleExpand(postId: number): void { this.toggleExpanded[postId] = !this.toggleExpanded[postId]; }



  isOwnPost(post: Post): boolean { return this.currentUser?.id === post.authorId; }



  getInitial(u?: string): string { return u ? u[0].toUpperCase() : '?'; }



  getImageUrl(url: string): string {

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



  openReport(): void { this.showReport = true; this.reportReason = ''; this.reportDescription = ''; this.reportError = ''; }

  closeReport(): void { this.showReport = false; }

  selectReason(r: string): void { this.reportReason = r; this.reportError = ''; }



  submitReport(): void {

    if (!this.reportReason) { this.reportError = 'Sélectionnez une raison.'; return; }

    this.reportLoading = true;

    this.reportService.reportUser(this.profileUserId, {

      reason: this.reportReason,

      description: this.reportDescription || undefined

    }).subscribe({

      next: () => {

        this.reportLoading = false;

        this.showReport = false;

        this.toast.success('Signalement envoyé.');

      },

      error: (err) => {

        this.reportLoading = false;

        this.reportError = err.error?.message || 'Erreur lors du signalement.';

        this.toast.error(this.reportError);

      }

    });

  }

}
