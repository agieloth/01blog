// import { Component, OnInit, OnDestroy, HostListener } from '@angular/core';
// import { CommonModule } from '@angular/common';
// import { Router, RouterModule } from '@angular/router';
// import { Subscription } from 'rxjs';
// import { AuthService } from '../../core/services/auth.service';
// import { NotificationService, NotificationResponse } from '../../core/services/notification.service';
// import { WebSocketService } from '../../core/services/websocket.service';
// import { User } from '../../core/models/models';

// @Component({
//   selector: 'app-navbar',
//   standalone: true,
//   imports: [CommonModule, RouterModule],
//   templateUrl: './navbar.html',
//   styleUrl: './navbar.scss'
// })
// export class NavbarComponent implements OnInit, OnDestroy {
//   currentUser: User | null = null;
//   notifications: NotificationResponse[] = [];
//   unreadCount = 0;
//   notifOpen = false;
//   wsConnected = false;
//   private subs: Subscription[] = [];

//   constructor(
//     public authService: AuthService,
//     private notifService: NotificationService,
//     private wsService: WebSocketService,
//     private router: Router
//   ) {}

//   ngOnInit(): void {
//     this.currentUser = this.authService.getCurrentUser();
//     // Lire l'état courant immédiatement (BehaviorSubject replay la dernière valeur)
//     this.wsConnected = this.wsService.isConnected;

//     this.subs.push(
//       this.authService.currentUser$.subscribe(user => {
//         this.currentUser = user;
//       })
//     );
//     this.subs.push(
//       this.wsService.connectionStatus.subscribe(s => this.wsConnected = s)
//     );
//     this.subs.push(
//       this.wsService.notificationEvents.subscribe(notif => {
//         this.notifications.unshift(notif);
//         this.unreadCount++;
//       })
//     );
//     this.loadNotifications();
//   }

//   ngOnDestroy(): void {
//     this.subs.forEach(s => s.unsubscribe());
//   }

//   loadNotifications(): void {
//     // FIX : ne pas appeler l'API si l'utilisateur n'est pas connecté
//     // (évite une erreur 401 au chargement du composant pour les visiteurs)
//     if (!this.authService.isAuthenticated()) return;

//     this.notifService.getNotifications().subscribe({
//       next: notifs => {
//         this.notifications = notifs;
//         this.unreadCount = notifs.filter(n => !n.read).length;
//       }
//     });
//   }

//   toggleNotif(): void {
//     this.notifOpen = !this.notifOpen;
//   }

//   markAllRead(): void {
//     this.notifService.markAllAsRead().subscribe(() => {
//       this.notifications.forEach(n => n.read = true);
//       this.unreadCount = 0;
//     });
//   }

//   clickNotif(notif: NotificationResponse): void {
//     if (!notif.read) {
//       this.notifService.markAsRead(notif.id).subscribe(() => {
//         notif.read = true;
//         this.unreadCount = Math.max(0, this.unreadCount - 1);
//       });
//     }
//     this.notifOpen = false;
//     if (notif.type === 'NEW_FOLLOWER') {
//       this.router.navigate(['/profile', notif.relatedEntityId]);
//     } else if ( notif.type === 'NEW_POST') {
//       this.router.navigate(['/feed', notif.relatedEntityId]);
//     } else {
//       this.router.navigate(['/feed']);
//     }
//   }

//   getNotifIcon(type: string): string {
//     switch (type) {
//       case 'POST_LIKED': return '❤️';
//       case 'POST_COMMENTED': return '💬';
//       case 'NEW_FOLLOWER': return '👤';
//       default: return '🔔';
//     }
//   }

//   getNotifClass(type: string): string {
//     switch (type) {
//       case 'POST_LIKED': return 'like';
//       case 'POST_COMMENTED': return 'comment';
//       case 'NEW_FOLLOWER': return 'follow';
//       default: return '';
//     }
//   }

//   formatDate(s: string): string {
//     const d = new Date(s), diff = (Date.now() - d.getTime()) / 1000;
//     if (diff < 60) return "à l'instant";
//     if (diff < 3600) return `il y a ${Math.floor(diff / 60)} min`;
//     if (diff < 86400) return `il y a ${Math.floor(diff / 3600)} h`;
//     return d.toLocaleDateString('fr-FR', { day: 'numeric', month: 'long' });
//   }

//   getInitial(username?: string): string {
//     return username ? username[0].toUpperCase() : '?';
//   }

//   logout(): void { this.authService.logout(); }
//   goToProfile(): void {
//     if (this.currentUser) this.router.navigate(['/profile', this.currentUser.id]);
//   }
//   isAdmin(): boolean { return this.authService.isAdmin(); }

//   @HostListener('document:click', ['$event'])
//   onDocClick(e: Event): void {
//     const target = e.target as HTMLElement;
//     if (!target.closest('.notif-wrapper')) this.notifOpen = false;
//   }
// }



import { Component, OnInit, OnDestroy, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { Subscription } from 'rxjs';
import { AuthService } from '../../core/services/auth.service';
import { NotificationService, NotificationResponse } from '../../core/services/notification.service';
import { WebSocketService } from '../../core/services/websocket.service';
import { User } from '../../core/models/models';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './navbar.html',
  styleUrl: './navbar.scss'
})
export class NavbarComponent implements OnInit, OnDestroy {
  currentUser: User | null = null;
  notifications: NotificationResponse[] = [];
  unreadCount = 0;
  notifOpen = false;
  wsConnected = false;
  private subs: Subscription[] = [];

  constructor(
    public authService: AuthService,
    private notifService: NotificationService,
    private wsService: WebSocketService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.currentUser = this.authService.getCurrentUser();
    this.wsConnected = this.wsService.isConnected;

    this.subs.push(
      this.authService.currentUser$.subscribe(user => {
        this.currentUser = user;
      })
    );
    this.subs.push(
      this.wsService.connectionStatus.subscribe(s => this.wsConnected = s)
    );
    this.subs.push(
      this.wsService.notificationEvents.subscribe(notif => {
        this.notifications.unshift(notif);
        this.unreadCount++;
      })
    );
    this.loadNotifications();
  }

  ngOnDestroy(): void {
    this.subs.forEach(s => s.unsubscribe());
  }

  loadNotifications(): void {
    this.notifService.getNotifications().subscribe({
      next: notifs => {
        this.notifications = notifs;
        this.unreadCount = notifs.filter(n => !n.read).length;
      }
    });
  }

  toggleNotif(): void {
    this.notifOpen = !this.notifOpen;
  }

  markAllRead(): void {
    this.notifService.markAllAsRead().subscribe(() => {
      this.notifications.forEach(n => n.read = true);
      this.unreadCount = 0;
    });
  }

  clickNotif(notif: NotificationResponse): void {
    if (!notif.read) {
      this.notifService.markAsRead(notif.id).subscribe(() => {
        notif.read = true;
        this.unreadCount = Math.max(0, this.unreadCount - 1);
      });
    }
    this.notifOpen = false;

    switch (notif.type) {
      case 'NEW_FOLLOWER':
        // relatedEntityId = userId du nouveau follower → aller sur son profil
        this.router.navigate(['/profile', notif.relatedEntityId]);
        break;
      case 'NEW_POST':
        // relatedEntityId = postId → aller sur le feed et scroller vers le post
        this.router.navigate(['/feed']).then(() => {
          setTimeout(() => {
            const el = document.getElementById(`post-${notif.relatedEntityId}`);
            el?.scrollIntoView({ behavior: 'smooth', block: 'center' });
          }, 400);
        });
        break;
      case 'POST_LIKED':
      case 'POST_COMMENTED':
        // relatedEntityId = postId → aller sur le feed et scroller vers le post
        this.router.navigate(['/feed']).then(() => {
          setTimeout(() => {
            const el = document.getElementById(`post-${notif.relatedEntityId}`);
            el?.scrollIntoView({ behavior: 'smooth', block: 'center' });
          }, 400);
        });
        break;
      default:
        this.router.navigate(['/feed']);
    }
  }

  getNotifIcon(type: string): string {
    switch (type) {
      case 'POST_LIKED':    return '❤️';
      case 'POST_COMMENTED': return '💬';
      case 'NEW_FOLLOWER':  return '👤';
      case 'NEW_POST':      return '📝';
      default:              return '🔔';
    }
  }

  getNotifClass(type: string): string {
    switch (type) {
      case 'POST_LIKED':    return 'like';
      case 'POST_COMMENTED': return 'comment';
      case 'NEW_FOLLOWER':  return 'follow';
      case 'NEW_POST':      return 'new-post';
      default:              return '';
    }
  }

  /**
   * Formate une date ISO renvoyée par Spring (LocalDateTime sans timezone).
   * On ajoute 'Z' pour forcer l'interprétation UTC.
   */
  formatDate(s: string): string {
    if (!s) return '';
    const normalized = s.includes('T') && !s.includes('Z') && !s.includes('+') && !s.includes('-', 10)
      ? s + 'Z'
      : s;
    const d = new Date(normalized);
    if (isNaN(d.getTime())) return s;
    const diff = (Date.now() - d.getTime()) / 1000;
    if (diff < 60)    return "à l'instant";
    if (diff < 3600)  return `il y a ${Math.floor(diff / 60)} min`;
    if (diff < 86400) return `il y a ${Math.floor(diff / 3600)} h`;
    return d.toLocaleDateString('fr-FR', { day: 'numeric', month: 'long' });
  }

  getInitial(username?: string): string {
    return username ? username[0].toUpperCase() : '?';
  }

  logout(): void { this.authService.logout(); }

  goToProfile(): void {
    if (this.currentUser) this.router.navigate(['/profile', this.currentUser.id]);
  }

  isAdmin(): boolean { return this.authService.isAdmin(); }

  @HostListener('document:click', ['$event'])
  onDocClick(e: Event): void {
    const target = e.target as HTMLElement;
    if (!target.closest('.notif-wrapper')) this.notifOpen = false;
  }
}