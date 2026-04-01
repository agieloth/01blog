import { Component, OnInit } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { AuthService } from './core/services/auth.service';
import { WebSocketService } from './core/services/websocket.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet],
  template: `<router-outlet></router-outlet>`
})
export class AppComponent implements OnInit {
  constructor(private authService: AuthService, private wsService: WebSocketService) {}

  ngOnInit(): void {
    // Connexion initiale si déjà authentifié (ex: rechargement page)
    const user = this.authService.getCurrentUser();
    if (user) {
      this.wsService.connect(user.id);
    }

    // Réagir aux changements d'auth :
    // - login  → connecter
    // - logout → déconnecter (disconnect() coupe la reconnexion auto aussi)
    this.authService.currentUser$.subscribe(u => {
      if (u) {
        // Connecter seulement si pas encore connecté
        this.wsService.connect(u.id);
      } else {
        // Logout explicite → couper proprement
        this.wsService.disconnect();
      }
    });
  }
}