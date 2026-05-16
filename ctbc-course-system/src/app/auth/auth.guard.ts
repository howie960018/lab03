
import { Injectable } from '@angular/core';
import { CanActivate, Router, UrlTree } from '@angular/router';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {

  constructor(
    private readonly authService: AuthService,
    private readonly router: Router
  ) {}

  canActivate(): boolean | UrlTree {

    // 第一層：token 是否存在 / 是否過期
    if (this.authService.isTokenExpired()) {
      this.authService.logout();
      return this.router.parseUrl('/login');
    }

    // 第二層：是否登入
    if (this.authService.isLoggedIn()) {
      return true;
    }

    return this.router.parseUrl('/login');
  }
}