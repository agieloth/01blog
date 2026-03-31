import { Injectable, OnDestroy } from '@angular/core';
import { Subject, Observable } from 'rxjs';
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
  private subscriptions: Map<string, any> = new Map();

  private postEvents$ = new Subject<WsEvent>();
  private likeEvents$ = new Subject<any>();
  private followEvents$ = new Subject<any>();
  private commentEvents$ = new Map<number, Subject<WsEvent>>();
  private notificationEvents$ = new Subject<any>();
  private connectionStatus$ = new Subject<boolean>();

  connect(userId?: number): void {
    if (this.connected) return;

    try {
      const socket = new SockJS(environment.wsUrl);
      this.stompClient = Stomp.over(socket);
      this.stompClient.debug = null;

      this.stompClient.connect({}, () => {
        this.connected = true;
        this.connectionStatus$.next(true);

        this.stompClient.subscribe('/topic/posts', (msg: any) => {
          this.postEvents$.next(JSON.parse(msg.body));
        });

        this.stompClient.subscribe('/topic/likes', (msg: any) => {
          this.likeEvents$.next(JSON.parse(msg.body));
        });

        this.stompClient.subscribe('/topic/follows', (msg: any) => {
          this.followEvents$.next(JSON.parse(msg.body));
        });

        if (userId) {
          this.stompClient.subscribe(`/topic/notifications/${userId}`, (msg: any) => {
            this.notificationEvents$.next(JSON.parse(msg.body));
          });
        }
      }, () => {
        this.connected = false;
        this.connectionStatus$.next(false);
        setTimeout(() => this.connect(userId), 3000);
      });
    } catch (e) {
      console.error('WebSocket connection failed', e);
    }
  }

  subscribeToComments(postId: number): Observable<WsEvent> {
    if (!this.commentEvents$.has(postId)) {
      this.commentEvents$.set(postId, new Subject<WsEvent>());
    }
    if (this.connected && this.stompClient) {
      const sub = this.stompClient.subscribe(`/topic/comments/${postId}`, (msg: any) => {
        this.commentEvents$.get(postId)?.next(JSON.parse(msg.body));
      });
      this.subscriptions.set(`comments-${postId}`, sub);
    }
    return this.commentEvents$.get(postId)!.asObservable();
  }

  disconnect(): void {
    if (this.stompClient && this.connected) {
      this.stompClient.disconnect();
      this.connected = false;
      this.connectionStatus$.next(false);
    }
  }

  get postEvents(): Observable<WsEvent> { return this.postEvents$.asObservable(); }
  get likeEvents(): Observable<any> { return this.likeEvents$.asObservable(); }
  get followEvents(): Observable<any> { return this.followEvents$.asObservable(); }
  get notificationEvents(): Observable<any> { return this.notificationEvents$.asObservable(); }
  get connectionStatus(): Observable<boolean> { return this.connectionStatus$.asObservable(); }
  get isConnected(): boolean { return this.connected; }

  ngOnDestroy(): void {
    this.disconnect();
  }
}