import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Enrollment } from '../models/enrollment';

@Injectable({ providedIn: 'root' })
export class EnrollmentService {
  constructor(private readonly http: HttpClient) {}

  getMyEnrollments(): Observable<Enrollment[]> {
    return this.http.get<Enrollment[]>('/api/enrollments/my');
  }

  enroll(courseId: number): Observable<Enrollment> {
    return this.http.post<Enrollment>(`/api/enrollments/${courseId}`, {});
  }

  cancel(courseId: number): Observable<void> {
    return this.http.delete<void>(`/api/enrollments/${courseId}`);
  }
}
