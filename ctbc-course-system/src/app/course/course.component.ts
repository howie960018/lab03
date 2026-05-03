import { Component, OnInit } from '@angular/core';
import { finalize } from 'rxjs';

import { Course } from '../models/course';
import { CourseService } from '../services/course.service';

@Component({
  selector: 'app-course',
  templateUrl: './course.component.html',
  styleUrls: ['./course.component.css']
})
export class CourseComponent implements OnInit {
  courses: Course[] = [];

  newCourse: Course = {
    courseName: '',
    courseSummary: '',
    price: 0,
    categoryId: undefined
  };

  isLoading = false;
  errorMessage = '';

  private readonly originalSnapshot = new WeakMap<Course, Pick<Course, 'courseName' | 'courseSummary' | 'price' | 'categoryId'>>();

  constructor(private readonly courseService: CourseService) {}

  ngOnInit(): void {
    this.reloadData();
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
    this.isLoading = true;
    this.errorMessage = '';

    const payload: Course = {
      courseName: (this.newCourse.courseName ?? '').trim(),
      courseSummary: (this.newCourse.courseSummary ?? '').trim(),
      price: this.newCourse.price ?? 0,
      categoryId: this.newCourse.categoryId
    };

    if (!payload.courseName) {
      this.isLoading = false;
      this.errorMessage = '課程名稱為必填。';
      return;
    }

    this.courseService
      .save(payload)
      .pipe(finalize(() => (this.isLoading = false)))
      .subscribe({
        next: () => {
          this.newCourse = { courseName: '', courseSummary: '', price: 0, categoryId: undefined };
          this.reloadData();
        },
        error: (error) => {
          console.error(error);
          this.errorMessage = '新增課程失敗，請稍後再試。';
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
          this.errorMessage = '刪除課程失敗，請稍後再試。';
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
      this.errorMessage = '更新失敗：id 不可為空。';
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';

    const payload: Course = {
      id: course.id,
      courseName: (course.courseName ?? '').trim(),
      courseSummary: (course.courseSummary ?? '').trim(),
      price: course.price ?? 0,
      categoryId: course.categoryId
    };

    if (!payload.courseName) {
      this.isLoading = false;
      this.errorMessage = '課程名稱為必填。';
      return;
    }

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
          this.errorMessage = '更新課程失敗，請稍後再試。';
        }
      });
  }
}
