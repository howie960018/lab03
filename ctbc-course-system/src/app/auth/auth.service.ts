import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

import { AuthRequest, AuthResponse, RegisterRequest } from './auth.models';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly tokenKey = 'ctbc.jwt';
  private readonly expiresAtKey = 'ctbc.jwt.expiresAt';
  private readonly apiBase = '';

  constructor(private readonly http: HttpClient) {}

  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  private getExpiresAt(): number | null {
    const raw = localStorage.getItem(this.expiresAtKey);
    if (!raw) return null;

    const value = Number(raw);
    return Number.isFinite(value) ? value : null;
  }

  isLoggedIn(): boolean {
    const token = this.getToken();
    if (!token) return false;

    const expiresAt = this.getExpiresAt();
    if (expiresAt !== null && Date.now() >= expiresAt) {
      this.logout();
      return false;
    }

    return true;
  }

  logout(): void {
    localStorage.removeItem(this.tokenKey);
    localStorage.removeItem(this.expiresAtKey);
  }

  login(request: AuthRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiBase}/api/auth/login`, request).pipe(
      tap((resp) => {
        if (resp?.accessToken) {
          localStorage.setItem(this.tokenKey, resp.accessToken);

          if (typeof resp.expiresInMs === 'number' && Number.isFinite(resp.expiresInMs)) {
            const expiresAt = Date.now() + resp.expiresInMs;
            localStorage.setItem(this.expiresAtKey, String(expiresAt));
          } else {
            localStorage.removeItem(this.expiresAtKey);
          }
        }
      })
    );
  }

  register(request: RegisterRequest): Observable<void> {
    return this.http.post<void>(`${this.apiBase}/api/auth/register`, request);
  }
}
