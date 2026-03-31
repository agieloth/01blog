import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './login.html',
  styleUrl: './login.scss'
})
export class LoginComponent {
  // "identifier" = email OU username (backend accepte les deux)
  identifier = '';
  password = '';
  error = '';
  loading = false;

  constructor(private authService: AuthService, private router: Router) {}

  submit(): void {
    if (!this.identifier.trim() || !this.password) {
      this.error = 'Remplis tous les champs.';
      return;
    }
    this.loading = true;
    this.error = '';
    this.authService.login({
      identifier: this.identifier.trim(),
      password: this.password
    }).subscribe({
      next: () => this.router.navigate(['/feed']),
      error: (err) => {
        this.loading = false;
        this.error = err.status === 401
          ? 'Identifiants incorrects.'
          : (err.error?.message || 'Erreur de connexion.');
      }
    });
  }

  onKey(e: KeyboardEvent): void {
    if (e.key === 'Enter') this.submit();
  }
}