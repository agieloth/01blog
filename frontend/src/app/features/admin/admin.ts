import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NavbarComponent } from '../../shared/navbar/navbar';
import { AdminService } from '../../core/services/admin.service';
import { AdminUser, AdminPost, AdminReport } from '../../core/models/models';

type AdminTab = 'users' | 'posts' | 'reports';

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

  constructor(private adminService: AdminService) {}

  ngOnInit(): void {
    this.loadUsers();
  }

  switchTab(tab: AdminTab): void {
    this.activeTab = tab;
    // FIX : toujours recharger les données lors du changement d'onglet
    // pour éviter d'afficher des données obsolètes (ban effectué dans un autre
    // onglet, post supprimé entre-temps, etc.)
    if (tab === 'users') this.loadUsers();
    if (tab === 'posts') this.loadPosts();
    if (tab === 'reports') this.loadReports();
  }

  loadUsers(): void {
    this.loading = true;
    this.adminService.getUsers().subscribe({
      next: u => { this.users = u; this.loading = false; },
      error: () => { this.loading = false; }
    });
  }

  loadPosts(): void {
    this.loading = true;
    this.adminService.getPosts().subscribe({
      next: p => { this.posts = p; this.loading = false; },
      error: () => { this.loading = false; }
    });
  }

  loadReports(): void {
    this.loading = true;
    this.adminService.getReports().subscribe({
      next: r => { this.reports = r; this.loading = false; },
      error: () => { this.loading = false; }
    });
  }

  toggleBan(userId: number): void {
    if (!confirm('Confirmer cette action ?')) return;
    this.adminService.toggleBanUser(userId).subscribe({
      next: (res) => {
        const u = this.users.find(u => u.id === userId);
        if (u) u.banned = res.banned;
      }
    });
  }

  deletePost(postId: number): void {
    if (!confirm('Supprimer ce post définitivement ?')) return;
    this.adminService.deletePost(postId).subscribe({
      next: () => { this.posts = this.posts.filter(p => p.id !== postId); }
    });
  }

  updateReportStatus(reportId: number, status: string): void {
    this.adminService.updateReportStatus(reportId, status).subscribe({
      next: () => {
        const r = this.reports.find(r => r.id === reportId);
        if (r) r.status = status;
      }
    });
  }

  getInitial(u: string): string { return u ? u[0].toUpperCase() : '?'; }

  formatDate(s: string): string {
    const d = new Date(s);
    return d.toLocaleDateString('fr-FR', { day: 'numeric', month: 'short', year: 'numeric' });
  }
}