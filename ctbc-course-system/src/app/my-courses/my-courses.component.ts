import { Component, OnInit } from '@angular/core';
import { EnrollmentService } from '../services/enrollment.service';
import { Enrollment } from '../models/enrollment';

@Component({
  selector: 'app-my-courses',
  templateUrl: './my-courses.component.html',
  styleUrls: ['./my-courses.component.css']
})
export class MyCoursesComponent implements OnInit {
  enrollments: Enrollment[] = [];
  isLoading = false;

  constructor(private readonly enrollmentService: EnrollmentService) {}

  ngOnInit(): void {
    this.isLoading = true;
    this.enrollmentService.getMyEnrollments().subscribe({
      next: (data) => { this.enrollments = data ?? []; this.isLoading = false; },
      error: () => { this.isLoading = false; }
    });
  }
}
