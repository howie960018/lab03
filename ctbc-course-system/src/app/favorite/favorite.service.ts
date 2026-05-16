

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Course } from '../models/course';

@Injectable({
  providedIn: 'root'
})
export class FavoriteService {

  private readonly apiBase = '';

  constructor(private readonly http: HttpClient) {}

  getMyFavorites(): Observable<Course[]> {
    return this.http.get<Course[]>(`${this.apiBase}/api/favorite`);
  }

  add(courseId: number): Observable<void> {
    return this.http.post<void>(`${this.apiBase}/api/favorite/${courseId}`, {});
  }

  remove(courseId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiBase}/api/favorite/${courseId}`);
  }
}