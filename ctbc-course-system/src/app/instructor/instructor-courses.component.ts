import { Component, OnInit } from '@angular/core';
import { InstructorService } from '../services/instructor.service';
import { CourseService } from '../services/course.service';
import { CategoryService } from '../services/category.service';
import { Course } from '../models/course';
import { Category } from '../models/category';
import { finalize } from 'rxjs';

@Component({
  selector: 'app-instructor-courses',
  templateUrl: './instructor-courses.component.html',
  styleUrls: ['./instructor-courses.component.css']
})
export class InstructorCoursesComponent implements OnInit {
  courses: Course[] = [];
  categories: Category[] = [];
  isLoading = false;
  errorMessage = '';

  newCourse: Course = { courseName: '', price: undefined };
  showForm = false;

  constructor(
    private readonly instructorService: InstructorService,
    private readonly courseService: CourseService,
    private readonly categoryService: CategoryService
  ) {}

  ngOnInit(): void {
    this.categoryService.getAll().subscribe({ next: (c) => (this.categories = c ?? []) });
    this.load();
  }

  load(): void {
    this.isLoading = true;
    this.instructorService.getMyCourses().subscribe({
      next: (c) => { this.courses = (c ?? []).map(x => ({ ...x, categoryId: x.category?.id })); this.isLoading = false; },
      error: () => { this.isLoading = false; }
    });
  }

  onSubmit(): void {
    if (!this.newCourse.courseName.trim()) { this.errorMessage = '請填寫課程名稱'; return; }
    this.errorMessage = '';
    this.instructorService.saveCourse(this.newCourse).pipe(finalize(() => {})).subscribe({
      next: () => { this.newCourse = { courseName: '', price: undefined }; this.showForm = false; this.load(); },
      error: (err) => { this.errorMessage = err?.error?.message ?? '儲存失敗'; }
    });
  }

  onUpdateStatus(course: Course, status: string): void {
    if (!course.id) return;
    this.courseService.updateStatus(course.id, status).subscribe({
      next: (updated) => { course.status = updated.status; },
      error: (err) => { this.errorMessage = err?.error?.message ?? '更新狀態失敗'; }
    });
  }

  statusLabel(status: string | undefined): string {
    switch (status) {
      case 'PUBLISHED': return '已發布';
      case 'ARCHIVED': return '已封存';
      default: return '草稿';
    }
  }

  getCategoryName(id: number | undefined): string {
    if (!id) return '—';
    return this.categories.find(c => c.id === id)?.categoryName ?? String(id);
  }
}
