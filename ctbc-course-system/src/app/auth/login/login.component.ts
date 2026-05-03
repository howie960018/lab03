import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { finalize } from 'rxjs';

import { AuthService } from '../auth.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  username = '';
  password = '';

  isLoading = false;
  errorMessage = '';

  constructor(private readonly authService: AuthService, private readonly router: Router) {}

  onSubmit(): void {
    this.isLoading = true;
    this.errorMessage = '';

    this.authService
      .login({ username: this.username.trim(), password: this.password })
      .pipe(finalize(() => (this.isLoading = false)))
      .subscribe({
        next: () => this.router.navigate(['/categories']),
        error: (err) => {
          console.error(err);
          this.errorMessage = '登入失敗：帳號或密碼錯誤。';
        }
      });
  }
}
