import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Order } from '../models/order';

@Injectable({ providedIn: 'root' })
export class OrderService {
  constructor(private readonly http: HttpClient) {}

  getMyOrders(): Observable<Order[]> {
    return this.http.get<Order[]>('/api/orders');
  }

  getById(id: number): Observable<Order> {
    return this.http.get<Order>(`/api/orders/${id}`);
  }

  checkout(courseIds: number[]): Observable<Order> {
    return this.http.post<Order>('/api/orders/checkout', { courseIds });
  }

  pay(id: number): Observable<Order> {
    return this.http.post<Order>(`/api/orders/${id}/pay`, {});
  }

  cancel(id: number): Observable<void> {
    return this.http.post<void>(`/api/orders/${id}/cancel`, {});
  }
}
