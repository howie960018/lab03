import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { CartItem } from '../models/cart-item';

@Injectable({ providedIn: 'root' })
export class CartService {
  private readonly storageKey = 'ctbc.cart';
  private readonly itemsSubject = new BehaviorSubject<CartItem[]>(this.load());

  readonly items$ = this.itemsSubject.asObservable();

  get items(): CartItem[] {
    return this.itemsSubject.value;
  }

  get total(): number {
    return this.items.reduce((sum, item) => sum + item.price, 0);
  }

  get count(): number {
    return this.items.length;
  }

  addItem(item: CartItem): void {
    const current = this.items;
    if (current.some(i => i.courseId === item.courseId)) return;
    const updated = [...current, item];
    this.save(updated);
    this.itemsSubject.next(updated);
  }

  removeItem(courseId: number): void {
    const updated = this.items.filter(i => i.courseId !== courseId);
    this.save(updated);
    this.itemsSubject.next(updated);
  }

  clear(): void {
    this.save([]);
    this.itemsSubject.next([]);
  }

  inCart(courseId: number): boolean {
    return this.items.some(i => i.courseId === courseId);
  }

  private load(): CartItem[] {
    try {
      const raw = localStorage.getItem(this.storageKey);
      return raw ? JSON.parse(raw) : [];
    } catch {
      return [];
    }
  }

  private save(items: CartItem[]): void {
    localStorage.setItem(this.storageKey, JSON.stringify(items));
  }
}
