
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Category } from '../models/category';

@Injectable({
  providedIn: 'root'
})
export class CategoryService {

  // 若後端 API 有特定的 Context Path，可設定在此（目前為空字串，代表同網域或由 Proxy 代理）
  private readonly apiBase = '';

  constructor(private readonly http: HttpClient) {}

  /**
   * 取得所有類別列表
   */
  getAll(): Observable<Category[]> {
    return this.http.get<Category[]>(`${this.apiBase}/api/category`);
  }

  /**
   * 新增或更新類別
   * 若 category 帶有 id，則視為「更新」並發送 PUT 請求；
   * 若無 id，則視為「新增」並發送 POST 請求。
   */
  save(category: Category): Observable<Category> {
    if (category.id) {
      return this.http.put<Category>(`${this.apiBase}/api/category/${category.id}`, category);
    }
    return this.http.post<Category>(`${this.apiBase}/api/category`, category);
  }

  /**
   * 根據 ID 刪除特定類別
   */
  deleteById(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiBase}/api/category/${id}`);
  }
}