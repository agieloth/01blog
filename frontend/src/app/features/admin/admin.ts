import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NavbarComponent } from '../../shared/navbar/navbar';
import { AdminService } from '../../core/services/admin.service';
import { AdminUser, AdminPost, AdminReport } from '../../core/models/models';

type AdminTab = 'users' | 'posts' | 'reports';

interface AdminStats {
  totalUsers: number;
  totalPosts: number;
  pendingReports: number;
  bannedUsers: number;
}

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [CommonModule, NavbarComponent],
  templateUrl: './admin.html',
  styleUrl: './admin.scss'
})
export class AdminComponent implements OnInit {
  activeTab: AdminTab = 'users';
  users: AdminUser[] = [];
  posts: AdminPost[] = [];
  reports: AdminReport[] = [];
  loading = false;
  loadError = '';

  // Stats calculées à partir des données chargées
  stats: AdminStats = { totalUsers: 0, totalPosts: 0, pendingReports: 0, bannedUsers: 0 };

  // Loading individuel par item (clé = id)
  actionLoading: { [id: number]: boolean } = {};

  // Modal suppression de post
  deletingAdminPostId: number | null = null;
  deletingAdminPostTitle = '';
  deleteAdminLoading = false;

  // Modal confirmation de ban/déban
  pendingBanUserId: number | null = null;
  pendingBanUsername = '';
  pendingBanIsBanned = false; // true = currently banned → on va débannir
  banLoading = false;

  constructor(private adminService: AdminService) {}

  ngOnInit(): void {
    this.loadAll();
  }

  // Charge tout d'un coup pour calculer les stats globales dès le départ
  loadAll(): void {
    this.loading = true;
    this.loadError = '';

    this.adminService.getUsers().subscribe({
      next: users => {
        this.users = users;
        this.stats.totalUsers = users.length;
        this.stats.bannedUsers = users.filter(u => u.banned).length;

        this.adminService.getPosts().subscribe({
          next: posts => {
            this.posts = posts;
            this.stats.totalPosts = posts.length;

            this.adminService.getReports().subscribe({
              next: reports => {
                this.reports = reports;
                this.stats.pendingReports = reports.filter(r => r.status === 'PENDING').length;
                this.loading = false;
              },
              error: () => { this.loading = false; this.loadError = 'Erreur lors du chargement des signalements.'; }
            });
          },
          error: () => { this.loading = false; this.loadError = 'Erreur lors du chargement des posts.'; }
        });
      },
      error: () => { this.loading = false; this.loadError = 'Erreur lors du chargement des utilisateurs.'; }
    });
  }

  switchTab(tab: AdminTab): void {
    this.activeTab = tab;
  }

  // ── BAN ───────────────────────────────────────────────────────────────────

  requestToggleBan(user: AdminUser): void {
    this.pendingBanUserId = user.id;
    this.pendingBanUsername = user.username;
    this.pendingBanIsBanned = user.banned;
  }

  closeBanModal(): void {
    this.pendingBanUserId = null;
    this.pendingBanUsername = '';
    this.pendingBanIsBanned = false;
  }

  confirmToggleBan(): void {
    if (!this.pendingBanUserId) return;
    const userId = this.pendingBanUserId;
    this.banLoading = true;
    this.actionLoading[userId] = true;

    this.adminService.toggleBanUser(userId).subscribe({
      next: (res) => {
        const u = this.users.find(u => u.id === userId);
        if (u) {
          u.banned = res.banned;
          this.stats.bannedUsers = this.users.filter(u => u.banned).length;
        }
        this.banLoading = false;
        this.actionLoading[userId] = false;
        this.closeBanModal();
      },
      error: () => {
        this.banLoading = false;
        this.actionLoading[userId] = false;
        this.closeBanModal();
      }
    });
  }

  // ── DELETE POST ───────────────────────────────────────────────────────────

  openDeletePostModal(post: AdminPost): void {
    this.deletingAdminPostId = post.id;
    this.deletingAdminPostTitle = post.title;
  }

  closeDeletePostModal(): void {
    this.deletingAdminPostId = null;
    this.deletingAdminPostTitle = '';
  }

  confirmDeletePost(): void {
    if (!this.deletingAdminPostId) return;
    const postId = this.deletingAdminPostId;
    this.deleteAdminLoading = true;
    this.actionLoading[postId] = true;

    this.adminService.deletePost(postId).subscribe({
      next: () => {
        this.posts = this.posts.filter(p => p.id !== postId);
        this.stats.totalPosts = this.posts.length;
        this.deleteAdminLoading = false;
        this.actionLoading[postId] = false;
        this.closeDeletePostModal();
      },
      error: () => {
        this.deleteAdminLoading = false;
        this.actionLoading[postId] = false;
        this.closeDeletePostModal();
      }
    });
  }

  // ── REPORTS ───────────────────────────────────────────────────────────────

  updateReportStatus(reportId: number, status: string): void {
    this.actionLoading[reportId] = true;
    this.adminService.updateReportStatus(reportId, status).subscribe({
      next: () => {
        const r = this.reports.find(r => r.id === reportId);
        if (r) r.status = status;
        this.stats.pendingReports = this.reports.filter(r => r.status === 'PENDING').length;
        this.actionLoading[reportId] = false;
      },
      error: () => { this.actionLoading[reportId] = false; }
    });
  }

  // ── HELPERS ───────────────────────────────────────────────────────────────

  getInitial(u: string): string { return u ? u[0].toUpperCase() : '?'; }

  formatDate(s: string): string {
    const d = new Date(s);
    return d.toLocaleDateString('fr-FR', { day: 'numeric', month: 'short', year: 'numeric' });
  }
}