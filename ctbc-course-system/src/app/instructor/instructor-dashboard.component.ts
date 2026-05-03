import { Component, OnInit } from '@angular/core';
import { InstructorService, InstructorStats } from '../services/instructor.service';

@Component({
  selector: 'app-instructor-dashboard',
  templateUrl: './instructor-dashboard.component.html',
  styleUrls: ['./instructor-dashboard.component.css']
})
export class InstructorDashboardComponent implements OnInit {
  stats?: InstructorStats;
  isLoading = false;

  constructor(private readonly instructorService: InstructorService) {}

  ngOnInit(): void {
    this.isLoading = true;
    this.instructorService.getStats().subscribe({
      next: (s) => { this.stats = s; this.isLoading = false; },
      error: () => { this.isLoading = false; }
    });
  }
}
