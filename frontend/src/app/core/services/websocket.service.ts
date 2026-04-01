import { Injectable, OnDestroy } from '@angular/core';
import { BehaviorSubject, Subject, Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

declare var SockJS: any;
declare var Stomp: any;

export interface WsEvent {
  type: string;
  data: any;
}

@Injectable({ providedIn: 'root' })
export class WebSocketService implements OnDestroy {
  private stompClient: any = null;
  private connected = false;
  private reconnectTimer: any = null;
  private currentUserId?: number;

  // BehaviorSubject → replay la dernière valeur aux nouveaux abonnés (navbar, etc.)
  private connectionStatus$ = new BehaviorSubject<boolean>(false);

  private postEvents$         = new Subject<WsEvent>();
  private likeEvents$         = new Subject<any>();
  private followEvents$       = new Subject<any>();
  private notificationEvents$ = new Subject<any>();
  private commentEvents$      = new Map<number, Subject<WsEvent>>();
  private stompCommentSubs    = new Map<number, any>();

  // ── CONNEXION ────────────────────────────────────────────────────────────

  connect(userId?: number): void {
    // Déjà connecté → rien à faire
    if (this.connected && this.stompClient) return;

    // Tentative déjà en cours (stompClient créé mais pas encore connecté)
    if (this.stompClient && !this.connected) return;

    this.currentUserId = userId;
    this._doConnect();
  }

  private _doConnect(): void {
    try {
      const socket = new SockJS(environment.wsUrl);
      this.stompClient = Stomp.over(socket);
      this.stompClient.debug = null;

      this.stompClient.connect(
        {},
        // Succès
        () => {
          this.connected = true;
          this.connectionStatus$.next(true);

          // Topics globaux
          this.stompClient.subscribe('/topic/posts', (msg: any) => {
            this.postEvents$.next(JSON.parse(msg.body));
          });
          this.stompClient.subscribe('/topic/likes', (msg: any) => {
            this.likeEvents$.next(JSON.parse(msg.body));
          });
          this.stompClient.subscribe('/topic/follows', (msg: any) => {
            this.followEvents$.next(JSON.parse(msg.body));
          });

          // Notifications perso
          if (this.currentUserId) {
            this.stompClient.subscribe(
              `/topic/notifications/${this.currentUserId}`,
              (msg: any) => this.notificationEvents$.next(JSON.parse(msg.body))
            );
          }

          // Ré-abonner aux topics commentaires déjà demandés
          this.commentEvents$.forEach((_, postId) => {
            this._subscribeCommentTopic(postId);
          });
        },
        // Erreur / déconnexion inattendue
        () => {
          this.connected = false;
          this.stompClient = null;
          this.connectionStatus$.next(false);
          this._scheduleReconnect();
        }
      );
    } catch (e) {
      console.error('WebSocket init error:', e);
      this._scheduleReconnect();
    }
  }

  private _scheduleReconnect(): void {
    if (this.reconnectTimer) return;
    this.reconnectTimer = setTimeout(() => {
      this.reconnectTimer = null;
      this._doConnect();
    }, 3000);
  }

  // ── COMMENTAIRES ─────────────────────────────────────────────────────────

  subscribeToComments(postId: number): Observable<WsEvent> {
    if (!this.commentEvents$.has(postId)) {
      this.commentEvents$.set(postId, new Subject<WsEvent>());
    }
    if (this.connected && !this.stompCommentSubs.has(postId)) {
      this._subscribeCommentTopic(postId);
    }
    return this.commentEvents$.get(postId)!.asObservable();
  }

  private _subscribeCommentTopic(postId: number): void {
    if (!this.stompClient || !this.connected) return;
    if (this.stompCommentSubs.has(postId)) return;
    const sub = this.stompClient.subscribe(
      `/topic/comments/${postId}`,
      (msg: any) => this.commentEvents$.get(postId)?.next(JSON.parse(msg.body))
    );
    this.stompCommentSubs.set(postId, sub);
  }

  // ── DÉCONNEXION (logout seulement) ───────────────────────────────────────

  disconnect(): void {
    if (this.reconnectTimer) {
      clearTimeout(this.reconnectTimer);
      this.reconnectTimer = null;
    }
    if (this.stompClient) {
      try { this.stompClient.disconnect(); } catch {}
      this.stompClient = null;
    }
    this.connected = false;
    this.connectionStatus$.next(false);
    this.stompCommentSubs.clear();
  }

  // ── OBSERVABLES ──────────────────────────────────────────────────────────

  get postEvents():         Observable<WsEvent> { return this.postEvents$.asObservable(); }
  get likeEvents():         Observable<any>     { return this.likeEvents$.asObservable(); }
  get followEvents():       Observable<any>     { return this.followEvents$.asObservable(); }
  get notificationEvents(): Observable<any>     { return this.notificationEvents$.asObservable(); }
  get connectionStatus():   Observable<boolean> { return this.connectionStatus$.asObservable(); }
  get isConnected():        boolean             { return this.connected; }

  ngOnDestroy(): void {
    this.disconnect();
  }
}