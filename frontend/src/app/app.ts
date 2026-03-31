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
    const user = this.authService.getCurrentUser();
    if (user) {
      this.wsService.connect(user.id);
    }
    this.authService.currentUser$.subscribe(user => {
      if (user && !this.wsService.isConnected) {
        this.wsService.connect(user.id);
      } else if (!user) {
        this.wsService.disconnect();
      }
    });
  }
}