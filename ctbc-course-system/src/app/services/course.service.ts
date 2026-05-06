import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { Course } from '../models/course';
import { Page } from '../models/page';

@Injectable({
  providedIn: 'root'
})
export class CourseService {

  private readonly apiBase = '';

  constructor(private readonly http: HttpClient) { }

  getAll(): Observable<Course[]> {
    return this.http.get<Course[]>(
      `${this.apiBase}/api/course/all`
    ).pipe(
      map((courses) =>
        (courses ?? []).map((c) => ({
          ...c,
          categoryId: c.category?.id
        }))
      )
    );
  }

  getPage(
    page: number,
    size: number,
    keyword?: string,
    categoryId?: number,
    sort = 'id,asc'
  ) {

    const params = new URLSearchParams({
      page: page.toString(),
      size: size.toString(),
      sort
    });

    if (keyword) {
      params.append('keyword', keyword);
    }

    if (categoryId !== undefined) {
      params.append('categoryId', String(categoryId));
    }

    return this.http
      .get<Page<Course>>(
        `/api/course?${params.toString()}`
      )
      .pipe(
        map((p) => ({
          ...p,
          content: p.content.map((c) => ({
            ...c,
            categoryId: c.category?.id
          }))
        }))
      );
  }

  getById(id: number): Observable<Course> {
    return this.http.get<Course>(
      `${this.apiBase}/api/course/${id}`
    ).pipe(
      map((c) => ({
        ...c,
        categoryId: c.category?.id
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
      imageUrl: course.imageUrl
    };

    if (
      course.categoryId !== undefined &&
      course.categoryId !== null
    ) {

      return this.http.post<Course>(
        `${this.apiBase}/api/course/category/${course.categoryId}`,
        payload
      ).pipe(
        map((c) => ({
          ...c,
          categoryId: c.category?.id
        }))
      );
    }

    return this.http.post<Course>(
      `${this.apiBase}/api/course`,
      payload
    ).pipe(
      map((c) => ({
        ...c,
        categoryId: c.category?.id
      }))
    );
  }

  deleteById(id: number): Observable<void> {
    return this.http.delete<void>(
      `${this.apiBase}/api/course/${id}`
    );
  }
}