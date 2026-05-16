
import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { finalize } from 'rxjs';
import { AuthService } from '../auth.service';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent {

  username = '';
  password = '';
  confirmPassword = '';
  isLoading = false;
  errorMessage = '';
  successMessage = '';

  constructor(
    private readonly authService: AuthService,
    private readonly router: Router
  ) {}

  onSubmit(): void {
    this.isLoading = true;
    this.errorMessage = '';
    this.successMessage = '';

    this.authService
      .register({
        username: this.username.trim(),
        password: this.password,
        confirmPassword: this.confirmPassword
      })
      .pipe(
        finalize(() => (this.isLoading = false))
      )
      .subscribe({
        next: () => {
          this.successMessage = '註冊成功';
          setTimeout(() => this.router.navigate(['/courses']), 600);
        },
        error: (err) => {
          console.error(err);
          const status = err?.status as number | undefined;

          if (status === 409) {
            this.errorMessage = '註冊失敗：帳號已存在。';
            return;
          }

          if (status === 400) {
            this.errorMessage = '註冊失敗：請確認密碼與確認密碼一致，且格式符合要求。';
            return;
          }

          this.errorMessage = '註冊失敗，請稍後再試。';
        }
      });
  }
}