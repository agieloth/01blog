import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { User } from '../../core/models/models';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './navbar.html',
  styleUrl: './navbar.scss'
})
export class NavbarComponent implements OnInit {
  currentUser: User | null = null;

  constructor(
    public authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.authService.currentUser$.subscribe(user => {
      this.currentUser = user;
    });
  }

  getInitial(username?: string): string {
    return username ? username[0].toUpperCase() : '?';
  }

  logout(): void {
    this.authService.logout();
  }

  goToProfile(): void {
    if (this.currentUser) {
      this.router.navigate(['/profile', this.currentUser.id]);
    }
  }

  isAdmin(): boolean {
    return this.authService.isAdmin();
  }
}