import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { Router } from '@angular/router';
import { environment } from '../../../environments/environment';
import { AuthResponse, LoginRequest, RegisterRequest, User } from '../models/models';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private apiUrl = `${environment.apiUrl}/auth`;
  private _currentUser$ = new BehaviorSubject<User | null>(this.loadUser());

  constructor(private http: HttpClient, private router: Router) {}

  get currentUser$(): Observable<User | null> {
    return this._currentUser$.asObservable();
  }

  getCurrentUser(): User | null {
    return this._currentUser$.getValue();
  }

  login(req: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, req).pipe(
      tap(res => this.handleAuth(res))
    );
  }

  register(req: RegisterRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/register`, req).pipe(
      tap(res => this.handleAuth(res))
    );
  }

  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    this._currentUser$.next(null);
    // FIX : naviguer vers /login sans conserver l'historique
    // (évite le retour en arrière vers une page authentifiée après logout)
    this.router.navigate(['/login'], { replaceUrl: true });
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  isAuthenticated(): boolean {
    return !!this.getToken();
  }

  isAdmin(): boolean {
    return this.getCurrentUser()?.role === 'ADMIN';
  }

  private handleAuth(res: AuthResponse): void {
    const user: User = {
      id: res.id,
      username: res.username,
      email: res.email,
      role: res.role
    };
    localStorage.setItem('token', res.token);
    localStorage.setItem('user', JSON.stringify(user));
    this._currentUser$.next(user);
  }

  private loadUser(): User | null {
    try {
      const raw = localStorage.getItem('user');
      if (!raw) return null;
      const user = JSON.parse(raw) as User;
      // FIX : validation basique de la structure pour détecter les données corrompues
      if (!user || typeof user.id !== 'number' || !user.username || !user.role) {
        localStorage.removeItem('user');
        localStorage.removeItem('token');
        return null;
      }
      return user;
    } catch {
      // JSON corrompu → nettoyer le localStorage
      localStorage.removeItem('user');
      localStorage.removeItem('token');
      return null;
    }
  }
}