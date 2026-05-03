import { Component, OnInit } from '@angular/core';
import { CourseService } from '../services/course.service';
import { Course } from '../models/course';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
  featuredCourses: Course[] = [];
  isLoading = false;

  constructor(private readonly courseService: CourseService) {}

  ngOnInit(): void {
    this.isLoading = true;
    this.courseService.getPublished(0, 6).subscribe({
      next: (page) => {
        this.featuredCourses = page.content;
        this.isLoading = false;
      },
      error: () => { this.isLoading = false; }
    });
  }
}
