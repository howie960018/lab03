import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Category } from '../models/category';

@Injectable({
  providedIn: 'root'
})
export class CategoryService {

  private readonly apiBase = '';

  constructor(private readonly http: HttpClient) { }

  getAll(): Observable<Category[]> {
    return this.http.get<Category[]>(
      `${this.apiBase}/api/category/all`
    );
  }

  getById(id: number): Observable<Category> {
    return this.http.get<Category>(
      `${this.apiBase}/api/category/${id}`
    );
  }

  save(category: Category): Observable<Category> {
    return this.http.post<Category>(
      `${this.apiBase}/api/category`,
      category
    );
  }

  deleteById(id: number): Observable<void> {
    return this.http.delete<void>(
      `${this.apiBase}/api/category/${id}`
    );
  }
}