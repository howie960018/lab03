import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Course } from '../models/course';

export interface InstructorStats {
  courseCount: number;
  totalEnrollments: number;
}

@Injectable({ providedIn: 'root' })
export class InstructorService {
  constructor(private readonly http: HttpClient) {}

  getStats(): Observable<InstructorStats> {
    return this.http.get<InstructorStats>('/api/instructor/stats');
  }

  getMyCourses(): Observable<Course[]> {
    return this.http.get<Course[]>('/api/instructor/courses');
  }

  saveCourse(course: Course): Observable<Course> {
    return this.http.post<Course>('/api/instructor/courses', course);
  }
}
