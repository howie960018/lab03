import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Chapter } from '../models/chapter';

@Injectable({ providedIn: 'root' })
export class ChapterService {
  constructor(private readonly http: HttpClient) {}

  getByCourse(courseId: number): Observable<Chapter[]> {
    return this.http.get<Chapter[]>(`/api/chapters/course/${courseId}`);
  }

  getById(id: number): Observable<Chapter> {
    return this.http.get<Chapter>(`/api/chapters/${id}`);
  }

  save(chapter: Chapter): Observable<Chapter> {
    return this.http.post<Chapter>('/api/chapters', chapter);
  }

  deleteById(id: number): Observable<void> {
    return this.http.delete<void>(`/api/chapters/${id}`);
  }
}
