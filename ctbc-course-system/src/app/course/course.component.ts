import { Component, OnInit } from '@angular/core';

import { AuthService } from '../auth/auth.service';
import { Category } from '../models/category';
import { Course } from '../models/course';

import { CategoryService } from '../services/category.service';
import { CourseService } from '../services/course.service';

import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject, Subject } from 'rxjs';

import {
  debounceTime,
  distinctUntilChanged,
  switchMap
} from 'rxjs/operators';

@Component({
  selector: 'app-course',
  templateUrl: './course.component.html',
  styleUrls: ['./course.component.css']
})
export class CourseComponent implements OnInit {

  page = 0;
  size = 10;
  totalPages = 0;
  totalElements = 0;

  keyword = '';
  sort = 'id,asc';
  selectedCategoryId: number | undefined;

  courses: Course[] = [];
  categories: Category[] = [];

  newCourse: Course = {
    courseName: '',
    courseSummary: '',
    price: undefined,
    categoryId: undefined,
    imageUrl: undefined
  };

  isLoading = false;
  errorMessage = '';

  search$ = new Subject<string>();

  private queryTrigger$ =
    new BehaviorSubject<void>(undefined);

  private readonly originalSnapshot =
    new WeakMap<Course, Pick<
      Course,
      'courseName' |
      'courseSummary' |
      'price' |
      'categoryId' |
      'imageUrl'
    >>();

  constructor(
    private readonly courseService: CourseService,
    private readonly categoryService: CategoryService,
    private readonly authService: AuthService,
    private readonly route: ActivatedRoute,
    private readonly router: Router
  ) {}

  get isLoggedIn(): boolean {
    return this.authService.isLoggedIn();
  }

  ngOnInit(): void {

    this.categoryService.getAll().subscribe({
      next: data => this.categories = data ?? [],
      error: () => this.categories = []
    });

    this.search$
      .pipe(
        debounceTime(300),
        distinctUntilChanged()
      )
      .subscribe(keyword => {
        this.keyword = keyword;
        this.page = 0;
        this.updateUrl();
      });

    this.route.queryParamMap.subscribe(params => {
      this.page = +(params.get('page') ?? 0);
      this.size = +(params.get('size') ?? 10);
      this.keyword = params.get('keyword') ?? '';
      this.sort = params.get('sort') ?? 'id,asc';

      const cat = params.get('categoryId');

      this.selectedCategoryId =
        cat !== null ? Number(cat) : undefined;

      this.reloadData();
    });

    this.queryTrigger$
      .pipe(
        switchMap(() => {
          this.isLoading = true;

          return this.courseService.getPage(
            this.page,
            this.size,
            this.keyword,
            this.selectedCategoryId,
            this.sort
          );
        })
      )
      .subscribe({
        next: res => {
          this.courses = res.content.map(c => ({
            ...c,
            isEditing: false
          }));

          this.totalPages = res.totalPages;
          this.totalElements = res.totalElements;
          this.isLoading = false;
        },

        error: err => {
          console.error(err);
          this.errorMessage = '載入課程失敗';
          this.isLoading = false;
        }
      });
  }

  reloadData(): void {
    this.queryTrigger$.next();
  }

  private updateUrl(): void {
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: {
        page: this.page,
        size: this.size,
        keyword: this.keyword || null,
        categoryId: this.selectedCategoryId ?? null,
        sort: this.sort
      },
      queryParamsHandling: 'merge'
    });
  }

  onSearch(): void {
    this.page = 0;
    this.updateUrl();
  }

  onSortChange(value: string): void {
    this.sort = value;
    this.page = 0;
    this.updateUrl();
  }

  goPrev(): void {
    if (this.page > 0) {
      this.page--;
      this.updateUrl();
    }
  }

  goNext(): void {
    if (this.page + 1 < this.totalPages) {
      this.page++;
      this.updateUrl();
    }
  }

  getCategoryName(id: number | undefined): string {
    if (id === undefined || id === null) {
      return '—';
    }

    const cat = this.categories.find(c => c.id === id);

    return cat ? cat.categoryName : String(id);
  }

  private validateCourse(
    course: Course,
    currentId?: number
  ): string | null {

    const name = (course.courseName ?? '').trim();

    if (!name) {
      return '課程名稱為必填';
    }

    const price = course.price ?? 0;

    if (price <= 0) {
      return '價格必須為正數';
    }

    const duplicate = this.courses.some(
      c =>
        c.courseName.trim() === name &&
        c.id !== currentId
    );

    if (duplicate) {
      return `課程名稱「${name}」已存在`;
    }

    return null;
  }

  onAddCourse(): void {

    const payload: Course = {
      courseName: this.newCourse.courseName.trim(),
      courseSummary: this.newCourse.courseSummary?.trim(),
      price: this.newCourse.price,
      categoryId: this.newCourse.categoryId,
      imageUrl: this.newCourse.imageUrl?.trim() || undefined
    };

    const error = this.validateCourse(payload);

    if (error) {
      this.errorMessage = error;
      return;
    }

    this.courseService.save(payload)
      .subscribe({
        next: () => {
          this.newCourse = {
            courseName: '',
            courseSummary: '',
            price: undefined,
            categoryId: undefined,
            imageUrl: undefined
          };

          this.reloadData();
        },

        error: err => {
          console.error(err);
          this.errorMessage = '新增課程失敗';
        }
      });
  }

  onDeleteCourse(id: number | undefined): void {

    if (id === undefined) {
      return;
    }

    this.courseService.deleteById(id)
      .subscribe({
        next: () => this.reloadData(),

        error: err => {
          console.error(err);
          this.errorMessage = '刪除課程失敗';
        }
      });
  }

  onToggleModify(course: Course): void {

    if (!course.isEditing) {
      this.originalSnapshot.set(course, {
        courseName: course.courseName,
        courseSummary: course.courseSummary,
        price: course.price,
        categoryId: course.categoryId,
        imageUrl: course.imageUrl
      });
    }

    course.isEditing = true;
  }

  onCancelModify(course: Course): void {

    const snapshot = this.originalSnapshot.get(course);

    if (snapshot) {
      course.courseName = snapshot.courseName;
      course.courseSummary = snapshot.courseSummary;
      course.price = snapshot.price;
      course.categoryId = snapshot.categoryId;
      course.imageUrl = snapshot.imageUrl;
    }

    course.isEditing = false;
  }

  onUpdateCourse(course: Course): void {

    if (course.id === undefined) {
      return;
    }

    const payload: Course = {
      id: course.id,
      courseName: course.courseName.trim(),
      courseSummary: course.courseSummary?.trim(),
      price: course.price,
      categoryId: course.categoryId,
      imageUrl: course.imageUrl?.trim() || undefined
    };

    const error = this.validateCourse(
      payload,
      course.id
    );

    if (error) {
      this.errorMessage = error;
      return;
    }

    this.courseService.save(payload)
      .subscribe({
        next: () => {
          course.isEditing = false;
          this.reloadData();
        },

        error: err => {
          console.error(err);
          this.errorMessage = '更新課程失敗';
        }
      });
  }
}