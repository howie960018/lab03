export interface OrderItem {
  id?: number;
  courseId: number;
  courseName: string;
  price: number;
}

export interface Order {
  id?: number;
  status: string;
  totalAmount: number;
  createdAt?: string;
  updatedAt?: string;
  items: OrderItem[];
}
