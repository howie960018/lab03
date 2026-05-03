import { Component, OnInit } from '@angular/core';
import { OrderService } from '../services/order.service';
import { Order } from '../models/order';

@Component({
  selector: 'app-order-list',
  templateUrl: './order-list.component.html',
  styleUrls: ['./order-list.component.css']
})
export class OrderListComponent implements OnInit {
  orders: Order[] = [];
  isLoading = false;

  constructor(private readonly orderService: OrderService) {}

  ngOnInit(): void {
    this.isLoading = true;
    this.orderService.getMyOrders().subscribe({
      next: (orders) => { this.orders = orders ?? []; this.isLoading = false; },
      error: () => { this.isLoading = false; }
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
