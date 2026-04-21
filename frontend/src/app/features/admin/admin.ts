// import { Component, OnInit } from '@angular/core';
// import { CommonModule } from '@angular/common';
// import { NavbarComponent } from '../../shared/navbar/navbar';
// import { AdminService } from '../../core/services/admin.service';
// import { AdminUser, AdminPost, AdminReport } from '../../core/models/models';

// type AdminTab = 'users' | 'posts' | 'reports';

// @Component({
//   selector: 'app-admin',
//   standalone: true,
//   imports: [CommonModule, NavbarComponent],
//   templateUrl: './admin.html',
//   styleUrl: './admin.scss'
// })
// export class AdminComponent implements OnInit {
//   activeTab: AdminTab = 'users';
//   users: AdminUser[] = [];
//   posts: AdminPost[] = [];
//   reports: AdminReport[] = [];
//   loading = false;

//   constructor(private adminService: AdminService) {}

//   ngOnInit(): void {
//     this.loadUsers();
//   }

//   switchTab(tab: AdminTab): void {
//     this.activeTab = tab;
//     // FIX : toujours recharger les données lors du changement d'onglet
//     // pour éviter d'afficher des données obsolètes (ban effectué dans un autre
//     // onglet, post supprimé entre-temps, etc.)
//     if (tab === 'users') this.loadUsers();
//     if (tab === 'posts') this.loadPosts();
//     if (tab === 'reports') this.loadReports();
//   }

//   loadUsers(): void {
//     this.loading = true;
//     this.adminService.getUsers().subscribe({
//       next: u => { this.users = u; this.loading = false; },
//       error: () => { this.loading = false; }
//     });
//   }

//   loadPosts(): void {
//     this.loading = true;
//     this.adminService.getPosts().subscribe({
//       next: p => { this.posts = p; this.loading = false; },
//       error: () => { this.loading = false; }
//     });
//   }

//   loadReports(): void {
//     this.loading = true;
//     this.adminService.getReports().subscribe({
//       next: r => { this.reports = r; this.loading = false; },
//       error: () => { this.loading = false; }
//     });
//   }

//   toggleBan(userId: number): void {
//     if (!confirm('Confirmer cette action ?')) return;
//     this.adminService.toggleBanUser(userId).subscribe({
//       next: (res) => {
//         const u = this.users.find(u => u.id === userId);
//         if (u) u.banned = res.banned;
//       }
//     });
//   }

//   deletePost(postId: number): void {
//     if (!confirm('Supprimer ce post définitivement ?')) return;
//     this.adminService.deletePost(postId).subscribe({
//       next: () => { this.posts = this.posts.filter(p => p.id !== postId); }
//     });
//   }

//   updateReportStatus(reportId: number, status: string): void {
//     this.adminService.updateReportStatus(reportId, status).subscribe({
//       next: () => {
//         const r = this.reports.find(r => r.id === reportId);
//         if (r) r.status = status;
//       }
//     });
//   }

//   getInitial(u: string): string { return u ? u[0].toUpperCase() : '?'; }

//   formatDate(s: string): string {
//     const d = new Date(s);
//     return d.toLocaleDateString('fr-FR', { day: 'numeric', month: 'short', year: 'numeric' });
//   }
// }



import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { NavbarComponent } from '../../shared/navbar/navbar';
import { AdminService } from '../../core/services/admin.service';
import { AdminUser, AdminPost, AdminReport } from '../../core/models/models';
import { forkJoin } from 'rxjs';

type AdminTab = 'users' | 'posts' | 'reports';

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [CommonModule, RouterModule, NavbarComponent],
  templateUrl: './admin.html',
  styleUrl: './admin.scss'
})
export class AdminComponent implements OnInit {
  activeTab: AdminTab = 'users';
  users: AdminUser[]     = [];
  posts: AdminPost[]     = [];
  reports: AdminReport[] = [];

  // loading = true dès le départ pour éviter un flash du contenu vide
  loading = true;
  loadError = '';

  actionLoading: { [id: number]: boolean } = {};

  stats = { totalUsers: 0, totalPosts: 0, pendingReports: 0, bannedUsers: 0 };

  constructor(private adminService: AdminService) {}

  ngOnInit(): void {
    this.loadAll();
  }

  loadAll(): void {
    this.loading = true;
    this.loadError = '';

    // forkJoin attend que les 3 requêtes terminent (succès ou erreur)
    forkJoin({
      users:   this.adminService.getUsers(),
      posts:   this.adminService.getPosts(),
      reports: this.adminService.getReports()
    }).subscribe({
      next: ({ users, posts, reports }) => {
        this.users   = users;
        this.posts   = posts;
        this.reports = reports;
        this.loading = false;
        this.computeStats();
      },
      error: (err) => {
        this.loading   = false;
        this.loadError = 'Erreur lors du chargement des données. Vérifiez votre connexion.';
        console.error('Admin load error:', err);
      }
    });
  }

  computeStats(): void {
    this.stats = {
      totalUsers:     this.users.length,
      totalPosts:     this.posts.length,
      pendingReports: this.reports.filter(r => r.status === 'PENDING').length,
      bannedUsers:    this.users.filter(u => u.banned).length,
    };
  }

  switchTab(tab: AdminTab): void {
    this.activeTab = tab;
  }

  // ── Users ──────────────────────────────────────────────────────────────────

  toggleBan(userId: number): void {
    const user   = this.users.find(u => u.id === userId);
    const action = user?.banned ? 'débannir' : 'bannir';
    if (!confirm(`Êtes-vous sûr de vouloir ${action} cet utilisateur ?`)) return;

    this.actionLoading[userId] = true;
    this.adminService.toggleBanUser(userId).subscribe({
      next: res => {
        if (user) user.banned = res.banned;
        this.actionLoading[userId] = false;
        this.computeStats();
      },
      error: () => { this.actionLoading[userId] = false; }
    });
  }

  // ── Posts ──────────────────────────────────────────────────────────────────

  deletePost(postId: number): void {
    if (!confirm('Supprimer ce post définitivement ? Cette action est irréversible.')) return;

    this.actionLoading[postId] = true;
    this.adminService.deletePost(postId).subscribe({
      next: () => {
        this.posts = this.posts.filter(p => p.id !== postId);
        this.actionLoading[postId] = false;
        this.computeStats();
      },
      error: () => { this.actionLoading[postId] = false; }
    });
  }

  // ── Reports ────────────────────────────────────────────────────────────────

  updateReportStatus(reportId: number, status: string): void {
    this.actionLoading[reportId] = true;
    this.adminService.updateReportStatus(reportId, status).subscribe({
      next: () => {
        const r = this.reports.find(r => r.id === reportId);
        if (r) r.status = status;
        this.actionLoading[reportId] = false;
        this.computeStats();
      },
      error: () => { this.actionLoading[reportId] = false; }
    });
  }

  // ── Helpers ────────────────────────────────────────────────────────────────

  getInitial(u: string): string { return u ? u[0].toUpperCase() : '?'; }

  formatDate(s: string): string {
    if (!s) return '';
    const normalized = s.includes('T') && !s.includes('Z') && !s.includes('+') && !s.includes('-', 10)
      ? s + 'Z' : s;
    const d = new Date(normalized);
    if (isNaN(d.getTime())) return s;
    return d.toLocaleDateString('fr-FR', { day: 'numeric', month: 'short', year: 'numeric' });
  }
}