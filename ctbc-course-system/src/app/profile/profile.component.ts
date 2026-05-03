import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../auth/auth.service';

interface Profile {
  id: number;
  username: string;
  role: string;
}

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit {
  profile?: Profile;
  isLoading = false;

  constructor(private readonly http: HttpClient, readonly authService: AuthService) {}

  ngOnInit(): void {
    this.isLoading = true;
    this.http.get<Profile>('/api/profile').subscribe({
      next: (p) => { this.profile = p; this.isLoading = false; },
      error: () => { this.isLoading = false; }
    });
  }

  roleLabel(role: string): string {
    switch (role) {
      case 'ADMIN': return '管理員';
      case 'INSTRUCTOR': return '講師';
      default: return '學員';
    }
  }
}
