import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { OrderService } from '../services/order.service';
import { Order } from '../models/order';

@Component({
  selector: 'app-order-detail',
  templateUrl: './order-detail.component.html',
  styleUrls: ['./order-detail.component.css']
})
export class OrderDetailComponent implements OnInit {
  order?: Order;
  isLoading = false;
  errorMessage = '';
  successMessage = '';

  constructor(
    private readonly route: ActivatedRoute,
    private readonly orderService: OrderService
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.isLoading = true;
    this.orderService.getById(id).subscribe({
      next: (order) => { this.order = order; this.isLoading = false; },
      error: () => { this.isLoading = false; this.errorMessage = '載入訂單失敗'; }
    });
  }

  onPay(): void {
    if (!this.order?.id) return;
    this.orderService.pay(this.order.id).subscribe({
      next: (order) => { this.order = order; this.successMessage = '付款成功！課程已開通。'; },
      error: (err) => { this.errorMessage = err?.error?.message ?? '付款失敗'; }
    });
  }

  onCancel(): void {
    if (!this.order?.id) return;
    this.orderService.cancel(this.order.id).subscribe({
      next: () => { if (this.order) this.order.status = 'CANCELLED'; this.successMessage = '訂單已取消。'; },
      error: (err) => { this.errorMessage = err?.error?.message ?? '取消失敗'; }
    });
  }

  statusClass(status: string): string {
    switch (status) {
      case 'PAID': return 'bg-success';
      case 'CANCELLED': return 'bg-secondary';
      default: return 'bg-warning text-dark';
    }
  }

  statusLabel(status: string): string {
    switch (status) {
      case 'PAID': return '已付款';
      case 'CANCELLED': return '已取消';
      default: return '待付款';
    }
  }
}
