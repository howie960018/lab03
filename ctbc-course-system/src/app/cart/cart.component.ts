import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CartService } from '../services/cart.service';
import { OrderService } from '../services/order.service';
import { CartItem } from '../models/cart-item';
import { AuthService } from '../auth/auth.service';

@Component({
  selector: 'app-cart',
  templateUrl: './cart.component.html',
  styleUrls: ['./cart.component.css']
})
export class CartComponent implements OnInit {
  items: CartItem[] = [];
  isLoading = false;
  errorMessage = '';

  constructor(
    readonly cartService: CartService,
    private readonly orderService: OrderService,
    private readonly authService: AuthService,
    private readonly router: Router
  ) {}

  ngOnInit(): void {
    this.cartService.items$.subscribe(items => (this.items = items));
  }

  onRemove(courseId: number): void {
    this.cartService.removeItem(courseId);
  }

  onClear(): void {
    this.cartService.clear();
  }

  onCheckout(): void {
    if (!this.authService.isLoggedIn()) {
      this.router.navigate(['/login']);
      return;
    }
    if (this.items.length === 0) return;

    this.isLoading = true;
    this.errorMessage = '';
    const courseIds = this.items.map(i => i.courseId);

    this.orderService.checkout(courseIds).subscribe({
      next: (order) => {
        this.cartService.clear();
        this.router.navigate(['/orders', order.id]);
      },
      error: (err) => {
        this.isLoading = false;
        this.errorMessage = err?.error?.message ?? '結帳失敗，請稍後再試';
      }
    });
  }
}
