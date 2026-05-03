import { Component, OnInit } from '@angular/core';
import { ReviewService, Review } from '../services/review.service';

@Component({
  selector: 'app-admin-reviews',
  templateUrl: './admin-reviews.component.html',
  styleUrls: ['./admin-reviews.component.css']
})
export class AdminReviewsComponent implements OnInit {
  reviews: Review[] = [];
  isLoading = false;
  errorMessage = '';

  constructor(private readonly reviewService: ReviewService) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.isLoading = true;
    this.reviewService.getAll().subscribe({
      next: (r) => { this.reviews = r ?? []; this.isLoading = false; },
      error: () => { this.isLoading = false; }
    });
  }

  onDelete(id: number | undefined): void {
    if (!id) return;
    this.reviewService.delete(id).subscribe({
      next: () => this.load(),
      error: (err) => { this.errorMessage = err?.error?.message ?? '刪除失敗'; }
    });
  }

  stars(n: number): number[] {
    return Array(n).fill(0);
  }
}
