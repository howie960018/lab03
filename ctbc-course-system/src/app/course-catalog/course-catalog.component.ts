import { Component, OnInit } from '@angular/core';

import { Course } from '../models/course';
import { Category } from '../models/category';

import { CourseService } from '../services/course.service';
import { CategoryService } from '../services/category.service';

import { BehaviorSubject, Subject } from 'rxjs';

import {
  debounceTime,
  distinctUntilChanged,
  switchMap
} from 'rxjs/operators';

import {
  ActivatedRoute,
  Router
} from '@angular/router';

@Component({
  selector: 'app-course-catalog',
  templateUrl: './course-catalog.component.html',
  styleUrls: ['./course-catalog.component.css']
})
export class CourseCatalogComponent implements OnInit {

  // =====================================
  // 資料
  // =====================================

  courses: Course[] = [];
  categories: Category[] = [];

  // =====================================
  // 查詢狀態（✅ 會同步到 URL）
  // =====================================

  page = 0;
  size = 9;

  totalPages = 0;
  totalElements = 0;

  keyword = '';
  selectedCategoryId?: number;

  sort = 'id,asc';

  isLoading = false;

  // =====================================
  // RxJS
  // =====================================

  keyword$ = new Subject<string>();

  private queryTrigger$ =
    new BehaviorSubject<void>(undefined);

  constructor(
    private readonly courseService: CourseService,
    private readonly categoryService: CategoryService,
    private readonly route: ActivatedRoute,
    private readonly router: Router
  ) {}

  // =====================================
  // 初始化
  // =====================================

  ngOnInit(): void {

    // 1️⃣ 載入類別
    this.categoryService.getAll().subscribe({
      next: data => (this.categories = data ?? []),
      error: () => (this.categories = [])
    });

    // 2️⃣ ✅ 從 URL Query Params 還原搜尋狀態
    //    （重整不會清）

    this.route.queryParamMap.subscribe(params => {

      this.keyword =
        params.get('keyword') ?? '';

      this.page =
        Number(params.get('page') ?? 0);

      this.sort =
        params.get('sort') ?? 'id,asc';

      const cid = params.get('category');

      this.selectedCategoryId =
        cid ? Number(cid) : undefined;

      this.queryTrigger$.next();
    });

    // 3️⃣ keyword debounce
    this.keyword$
      .pipe(
        debounceTime(300),
        distinctUntilChanged()
      )
      .subscribe(keyword => {

        this.keyword = keyword;

        this.page = 0;

        this.syncQueryParams();
      });

    // 4️⃣ 單一 HTTP 出口
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

          this.courses =
            res.content ?? [];

          this.totalPages =
            res.totalPages;

          this.totalElements =
            res.totalElements;

          this.isLoading = false;
        },

        error: () => {

          this.courses = [];

          this.isLoading = false;
        }
      });
  }

  // =====================================
  // ✅ 同步搜尋狀態到 URL
  // =====================================

  private syncQueryParams(): void {

    this.router.navigate([], {

      relativeTo: this.route,

      queryParams: {
        keyword: this.keyword || null,
        page: this.page,
        sort: this.sort,
        category:
          this.selectedCategoryId ?? null
      },

      queryParamsHandling: 'merge'
    });
  }

  // =====================================
  // 搜尋 / 篩選 / 排序
  // =====================================

  onSearch(): void {

    this.page = 0;

    this.syncQueryParams();
  }

  onSortChange(value: string): void {

    this.sort = value;

    this.page = 0;

    this.syncQueryParams();
  }

  onCategoryChange(): void {

    this.page = 0;

    this.syncQueryParams();
  }

  // =====================================
  // 分頁
  // =====================================

  goFirst(): void {

    if (this.page !== 0) {

      this.page = 0;

      this.syncQueryParams();
    }
  }

  goPrev(): void {

    if (this.page > 0) {

      this.page--;

      this.syncQueryParams();
    }
  }

  goNext(): void {

    if (this.page + 1 < this.totalPages) {

      this.page++;

      this.syncQueryParams();
    }
  }

  goLast(): void {

    const lastPage =
      this.totalPages - 1;

    if (
      lastPage >= 0 &&
      this.page !== lastPage
    ) {

      this.page = lastPage;

      this.syncQueryParams();
    }
  }
}