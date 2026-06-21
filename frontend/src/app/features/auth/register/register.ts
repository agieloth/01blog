// import { Component } from '@angular/core';
// import { CommonModule } from '@angular/common';
// import { FormsModule } from '@angular/forms';
// import { RouterModule, Router } from '@angular/router';
// import { AuthService } from '../../../core/services/auth.service';

// @Component({
//   selector: 'app-register',
//   standalone: true,
//   imports: [CommonModule, FormsModule, RouterModule],
//   templateUrl: './register.html',
//   styleUrl: './register.scss'
// })
// export class RegisterComponent {
//   username = '';
//   email = '';
//   password = '';
//   confirmPassword = '';
//   error = '';
//   loading = false;

//   constructor(private authService: AuthService, private router: Router) {}

//   submit(): void {
//     if (!this.username.trim() || !this.email.trim() || !this.password) {
//       this.error = 'Remplis tous les champs.';
//       return;
//     }
//     if (this.password !== this.confirmPassword) {
//       this.error = 'Les mots de passe ne correspondent pas.';
//       return;
//     }
//     if (this.password.length < 6) {
//       this.error = 'Le mot de passe doit faire au moins 6 caractères.';
//       return;
//     }
//     this.loading = true;
//     this.error = '';
//     this.authService.register({
//       username: this.username.trim(),
//       email: this.email.trim(),
//       password: this.password
//     }).subscribe({
//       next: () => this.router.navigate(['/feed']),
//       error: (err) => {
//         this.loading = false;
//         this.error = err.error?.message || 'Erreur lors de l\'inscription.';
//       }
//     });
//   }
// }


import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { AuthService } from '../../../core/services/auth.service';
import { ToastService } from '../../../core/services/toast.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    RouterModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
  ],
  templateUrl: './register.html',
  styleUrl: './register.scss'
})
export class RegisterComponent {
  username = '';
  email = '';
  password = '';
  confirmPassword = '';
  error = '';
  loading = false;

  constructor(
    private authService: AuthService,
    private router: Router,
    private toast: ToastService,
  ) {}

  submit(): void {
    if (!this.username.trim() || !this.email.trim() || !this.password) {
      this.error = 'Remplis tous les champs.';
      return;
    }
    if (this.password !== this.confirmPassword) {
      this.error = 'Les mots de passe ne correspondent pas.';
      return;
    }
    if (this.password.length < 6) {
      this.error = 'Le mot de passe doit faire au moins 6 caractères.';
      return;
    }
    this.loading = true;
    this.error = '';
    this.authService.register({
      username: this.username.trim(),
      email: this.email.trim(),
      password: this.password
    }).subscribe({
      next: () => {
        this.toast.success('Compte créé avec succès !');
        this.router.navigate(['/feed']);
      },
      error: (err) => {
        this.loading = false;
        this.error = err.error?.message || 'Erreur lors de l\'inscription.';
        this.toast.error(this.error);
      }
    });
  }
}