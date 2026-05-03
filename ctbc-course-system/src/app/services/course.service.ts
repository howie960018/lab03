import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { Course, PageResult } from '../models/course';

@Injectable({ providedIn: 'root' })
export class CourseService {
  constructor(private readonly http: HttpClient) {}

  getAll(): Observable<Course[]> {
    return this.http.get<Course[]>('/api/course/all').pipe(
      map((courses) => (courses ?? []).map((c) => ({ ...c, categoryId: c.category?.id })))
    );
  }

  getById(id: number): Observable<Course> {
    return this.http.get<Course>(`/api/course/${id}`).pipe(
      map((c) => ({ ...c, categoryId: c.category?.id }))
    );
  }

  getPublished(page = 0, size = 12, keyword?: string, categoryId?: number): Observable<PageResult<Course>> {
    let params = new HttpParams().set('page', page).set('size', size);
    if (keyword) params = params.set('keyword', keyword);
    if (categoryId != null) params = params.set('categoryId', categoryId);
    return this.http.get<PageResult<Course>>('/api/course/published', { params }).pipe(
      map((result) => ({
        ...result,
        content: result.content.map((c) => ({ ...c, categoryId: c.category?.id }))
      }))
    );
  }

  save(course: Course): Observable<Course> {
    const payload: any = {
      id: course.id,
      courseName: course.courseName,
      courseSummary: course.courseSummary,
      courseDescription: course.courseDescription,
      price: course.price,
      coverImageUrl: course.coverImageUrl,
      instructorName: course.instructorName,
      durationHours: course.durationHours,
      level: course.level,
      status: course.status
    };

    if (course.categoryId != null) {
      return this.http
        .post<Course>(`/api/course/category/${course.categoryId}`, payload)
        .pipe(map((c) => ({ ...c, categoryId: c.category?.id })));
    }
    return this.http
      .post<Course>('/api/course', payload)
      .pipe(map((c) => ({ ...c, categoryId: c.category?.id })));
  }

  updateStatus(id: number, status: string): Observable<Course> {
    return this.http.post<Course>(`/api/course/${id}/status`, { status });
  }

  deleteById(id: number): Observable<void> {
    return this.http.delete<void>(`/api/course/${id}`);
  }
}
