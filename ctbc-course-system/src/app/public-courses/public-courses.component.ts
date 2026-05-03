import { Component, OnInit } from '@angular/core';
import { CourseService } from '../services/course.service';
import { CategoryService } from '../services/category.service';
import { Course } from '../models/course';
import { Category } from '../models/category';

@Component({
  selector: 'app-public-courses',
  templateUrl: './public-courses.component.html',
  styleUrls: ['./public-courses.component.css']
})
export class PublicCoursesComponent implements OnInit {
  courses: Course[] = [];
  categories: Category[] = [];
  keyword = '';
  selectedCategoryId?: number;
  currentPage = 0;
  totalPages = 0;
  totalElements = 0;
  pageSize = 12;
  isLoading = false;

  constructor(
    private readonly courseService: CourseService,
    private readonly categoryService: CategoryService
  ) {}

  ngOnInit(): void {
    this.categoryService.getAll().subscribe({ next: (cats) => (this.categories = cats ?? []) });
    this.loadCourses();
  }

  loadCourses(): void {
    this.isLoading = true;
    this.courseService.getPublished(this.currentPage, this.pageSize, this.keyword || undefined, this.selectedCategoryId).subscribe({
      next: (page) => {
        this.courses = page.content;
        this.totalPages = page.totalPages;
        this.totalElements = page.totalElements;
        this.isLoading = false;
      },
      error: () => { this.isLoading = false; }
    });
  }

  onSearch(): void {
    this.currentPage = 0;
    this.loadCourses();
  }

  onCategoryChange(): void {
    this.currentPage = 0;
    this.loadCourses();
  }

  goToPage(page: number): void {
    if (page < 0 || page >= this.totalPages) return;
    this.currentPage = page;
    this.loadCourses();
  }

  get pages(): number[] {
    return Array.from({ length: this.totalPages }, (_, i) => i);
  }
}
