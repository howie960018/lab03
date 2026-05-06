import { Injectable } from '@angular/core';
import {
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest
} from '@angular/common/http';
import { Router } from '@angular/router';
import { catchError, Observable, throwError } from 'rxjs';

import { AuthService } from './auth.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(
    private readonly authService: AuthService,
    private readonly router: Router
  ) {}

  intercept(req: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    const isApiCall = req.url.startsWith('/api/') || req.url.includes('://') && req.url.includes('/api/');
    const isAuthCall = req.url.includes('/api/auth/');

    if (!isApiCall || isAuthCall) {
      return next.handle(req);
    }

    // ✅ if token expired, isLoggedIn() will clear it.
    const shouldAttachToken = this.authService.isLoggedIn();
    const token = shouldAttachToken ? this.authService.getToken() : null;

    const requestToSend = token
      ? req.clone({
          setHeaders: {
            Authorization: `Bearer ${token}`
          }
        })
      : req;

    return next.handle(requestToSend).pipe(
      catchError((err) => {
        const status = err?.status as number | undefined;

        // 401/403: token invalid/expired/not authorized -> force logout
        if (status === 401 || status === 403) {
          this.authService.logout();
          this.router.navigate(['/login']);
        }

        return throwError(() => err);
      })
    );
  }
}
