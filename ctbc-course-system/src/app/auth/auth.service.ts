
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { AuthRequest, AuthResponse, RegisterRequest } from './auth.models';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private readonly tokenKey = 'ctbc.jwt';
  private readonly apiBase = '';

  constructor(private readonly http: HttpClient) {}

  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  getRole(): string | null {
    const token = this.getToken();
    if (!token) return null;

    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      const roles: string[] = payload.roles || [];

      return roles.length > 0 ? roles[0].replace('ROLE_', '') : null;
    } catch {
      return null;
    }
  }

  isAdmin(): boolean {
    return this.getRole() === 'ADMIN';
  }

  isInstructor(): boolean {
    return this.getRole() === 'INSTRUCTOR';
  }

  isUser(): boolean {
    return this.getRole() === 'USER';
  }

  isTokenExpired(): boolean {
    const token = this.getToken();
    if (!token) return true;

    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      const now = Math.floor(Date.now() / 1000);
      return payload.exp < now;
    } catch {
      return true;
    }
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  logout(): void {
    localStorage.removeItem(this.tokenKey);
  }

  login(request: AuthRequest): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${this.apiBase}/api/auth/login`, request)
      .pipe(
        tap((resp) => {
          if (resp?.accessToken) {
            localStorage.setItem(this.tokenKey, resp.accessToken);
          }
        })
      );
  }

  // 註冊
  register(request: RegisterRequest): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${this.apiBase}/api/auth/register`, request)
      .pipe(
        tap((resp) => {
          if (resp?.accessToken) {
            localStorage.setItem(this.tokenKey, resp.accessToken);
          }
        })
      );
  }
}