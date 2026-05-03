import { Component, OnInit } from '@angular/core';
import { AdminService, AdminStats } from '../services/admin.service';

@Component({
  selector: 'app-admin-dashboard',
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.css']
})
export class AdminDashboardComponent implements OnInit {
  stats?: AdminStats;
  isLoading = false;

  constructor(private readonly adminService: AdminService) {}

  ngOnInit(): void {
    this.isLoading = true;
    this.adminService.getStats().subscribe({
      next: (s) => { this.stats = s; this.isLoading = false; },
      error: () => { this.isLoading = false; }
    });
  }
}
