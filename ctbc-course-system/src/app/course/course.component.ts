import { Component, OnInit } from '@angular/core';
import { finalize } from 'rxjs';

import { AuthService } from '../auth/auth.service';
import { Category } from '../models/category';
import { Course } from '../models/course';
import { CategoryService } from '../services/category.service';
import { CourseService } from '../services/course.service';

@Component({
  selector: 'app-course',
  templateUrl: './course.component.html',
  styleUrls: ['./course.component.css']
})
export class CourseComponent implements OnInit {
  courses: Course[] = [];
  categories: Category[] = [];

  newCourse: Course = {
    courseName: '',
    courseSummary: '',
    price: undefined,
    categoryId: undefined
  };

  isLoading = false;
  errorMessage = '';

  private readonly originalSnapshot = new WeakMap<Course, Pick<Course, 'courseName' | 'courseSummary' | 'price' | 'categoryId'>>();

  constructor(
    private readonly courseService: CourseService,
    private readonly categoryService: CategoryService,
    private readonly authService: AuthService
  ) {}

  get isLoggedIn(): boolean {
    return this.authService.isLoggedIn();
  }

  ngOnInit(): void {
    this.categoryService.getAll().subscribe({
      next: (data) => (this.categories = data ?? []),
      error: () => (this.categories = [])
    });
    this.reloadData();
  }

  getCategoryName(id: number | undefined): string {
    if (id === undefined || id === null) return '—';
    const cat = this.categories.find((c) => c.id === id);
    return cat ? cat.categoryName : String(id);
  }

  private validateCourse(course: Course, currentId?: number): string | null {
    const name = (course.courseName ?? '').trim();
    if (!name) return '課程名稱為必填。';

    const price = course.price ?? 0;
    if (price <= 0) return '價格必須為正數（大於 0）。';

    const duplicate = this.courses.some(
      (c) => c.courseName.trim() === name && c.id !== currentId
    );
    if (duplicate) return `課程名稱「${name}」已存在，請使用其他名稱。`;

    return null;
  }

  reloadData(): void {
    this.isLoading = true;
    this.errorMessage = '';

    this.courseService
      .getAll()
      .pipe(finalize(() => (this.isLoading = false)))
      .subscribe({
        next: (data) => {
          console.log('courses:', data);
          this.courses = (data ?? []).map((c) => ({
            ...c,
            isEditing: false
          }));
        },
        error: (error) => {
          console.error(error);
          this.errorMessage = '載入課程資料失敗，請稍後再試。';
          this.courses = [];
        }
      });
  }

  onAddCourse(): void {
    this.errorMessage = '';

    const payload: Course = {
      courseName: (this.newCourse.courseName ?? '').trim(),
      courseSummary: (this.newCourse.courseSummary ?? '').trim(),
      price: this.newCourse.price ?? 0,
      categoryId: this.newCourse.categoryId
    };

    const validationError = this.validateCourse(payload);
    if (validationError) {
      this.errorMessage = validationError;
      return;
    }

    this.isLoading = true;

    this.courseService
      .save(payload)
      .pipe(finalize(() => (this.isLoading = false)))
      .subscribe({
        next: () => {
          this.newCourse = { courseName: '', courseSummary: '', price: undefined, categoryId: undefined };
          this.reloadData();
        },
        error: (error) => {
          console.error(error);
          this.errorMessage = error?.error?.message ?? '新增課程失敗，請稍後再試。';
        }
      });
  }

  onDeleteCourse(id: number | undefined): void {
    if (id === undefined) {
      this.errorMessage = '刪除失敗：id 不可為空。';
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';

    this.courseService
      .deleteById(id)
      .pipe(finalize(() => (this.isLoading = false)))
      .subscribe({
        next: () => this.reloadData(),
        error: (error) => {
          console.error(error);
          this.errorMessage = error?.error?.message ?? '刪除課程失敗，請稍後再試。';
        }
      });
  }

  onToggleModify(course: Course): void {
    if (!course.isEditing) {
      this.originalSnapshot.set(course, {
        courseName: course.courseName,
        courseSummary: course.courseSummary,
        price: course.price,
        categoryId: course.categoryId
      });
    }

    course.isEditing = true;
    this.errorMessage = '';
  }

  onCancelModify(course: Course): void {
    const snapshot = this.originalSnapshot.get(course);
    if (snapshot) {
      course.courseName = snapshot.courseName;
      course.courseSummary = snapshot.courseSummary;
      course.price = snapshot.price;
      course.categoryId = snapshot.categoryId;
    }

    course.isEditing = false;
    this.errorMessage = '';
  }

  onUpdateCourse(course: Course): void {
    if (course.id === undefined) {
      this.errorMessage = '更新失敗：找不到對應的課程 ID。';
      return;
    }

    this.errorMessage = '';

    const payload: Course = {
      id: course.id,
      courseName: (course.courseName ?? '').trim(),
      courseSummary: (course.courseSummary ?? '').trim(),
      price: course.price ?? 0,
      categoryId: course.categoryId
    };

    const validationError = this.validateCourse(payload, course.id);
    if (validationError) {
      this.errorMessage = validationError;
      return;
    }

    this.isLoading = true;

    this.courseService
      .save(payload)
      .pipe(finalize(() => (this.isLoading = false)))
      .subscribe({
        next: () => {
          course.isEditing = false;
          this.originalSnapshot.delete(course);
          this.reloadData();
        },
        error: (error) => {
          console.error(error);
          this.errorMessage = error?.error?.message ?? '更新課程失敗，請稍後再試。';
        }
      });
  }
}
