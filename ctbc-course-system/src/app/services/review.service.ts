import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Review {
  id?: number;
  courseId: number;
  courseName?: string;
  reviewerUsername?: string;
  rating: number;
  comment?: string;
  createdAt?: string;
}

@Injectable({ providedIn: 'root' })
export class ReviewService {
  constructor(private readonly http: HttpClient) {}

  getByCourse(courseId: number): Observable<Review[]> {
    return this.http.get<Review[]>(`/api/reviews/course/${courseId}`);
  }

  getAll(): Observable<Review[]> {
    return this.http.get<Review[]>('/api/reviews/all');
  }

  submit(courseId: number, rating: number, comment: string): Observable<Review> {
    return this.http.post<Review>(`/api/reviews/course/${courseId}`, { rating, comment });
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`/api/reviews/${id}`);
  }
}
