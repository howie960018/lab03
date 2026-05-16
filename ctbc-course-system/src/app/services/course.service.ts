
import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Course } from '../models/course';
import { Page } from '../models/page';

@Injectable({
  providedIn: 'root'
})
export class CourseService {

  private readonly apiBase = '';

  constructor(private readonly http: HttpClient) {}

  /**
   * 取得分頁、搜尋與過濾後的課程列表
   */
  getPage(
    page: number,
    size: number,
    keyword?: string,
    categoryId?: number,
    sort?: string,
    instructor?: string
  ): Observable<Page<Course>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    if (keyword) {
      params = params.set('keyword', keyword);
    }
    if (categoryId !== undefined && categoryId !== null) {
      params = params.set('categoryId', categoryId.toString());
    }
    if (sort) {
      params = params.set('sort', sort);
    }
    if (instructor) {
      params = params.set('instructor', instructor);
    }

    return this.http.get<Page<Course>>(`${this.apiBase}/api/course`, { params });
  }

  /**
   * 取得所有講師的名稱列表
   */
  getInstructors(): Observable<string[]> {
    return this.http.get<string[]>(`${this.apiBase}/api/course/instructors`);
  }

  /**
   * 根據 ID 取得單一課程
   */
  getById(id: number): Observable<Course> {
    return this.http.get<Course>(`${this.apiBase}/api/course/${id}`).pipe(
      map((c) => ({ ...c, categoryId: c.category?.id }))
    );
  }

  /**
   * 儲存 (新增/修改) 課程
   */
  save(course: Course): Observable<Course> {
    const payload: any = {
      id: course.id,
      courseName: course.courseName,
      courseSummary: course.courseSummary,
      courseDescription: course.courseDescription,
      price: course.price,
      imageUrl: course.imageUrl
    };

    // 若有選擇類別，則打包含類別 ID 的 API 路徑
    if (course.categoryId !== undefined && course.categoryId !== null) {
      return this.http
        .post<Course>(`${this.apiBase}/api/course/category/${course.categoryId}`, payload)
        .pipe(map(c => ({ ...c, categoryId: c.category?.id })));
    }

    // 若未設定類別，則打一般新增路徑
    return this.http
      .post<Course>(`${this.apiBase}/api/course`, payload)
      .pipe(map(c => ({ ...c, categoryId: c.category?.id })));
  }

  /**
   * 刪除課程
   */
  deleteById(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiBase}/api/course/${id}`);
  }

  /**
   * 更新課程的關聯類別
   */
  updateCategory(courseId: number, categoryId: number): Observable<Course> {
    return this.http
      .put<Course>(`/api/course/${courseId}/category/${categoryId}`, {})
      .pipe(
        map(c => ({ ...c, categoryId: c.category?.id }))
      );
  }


}