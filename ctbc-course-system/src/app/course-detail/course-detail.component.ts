import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CourseService } from '../services/course.service';
import { ChapterService } from '../services/chapter.service';
import { CartService } from '../services/cart.service';
import { ReviewService, Review } from '../services/review.service';
import { AuthService } from '../auth/auth.service';
import { Course } from '../models/course';
import { Chapter } from '../models/chapter';

@Component({
  selector: 'app-course-detail',
  templateUrl: './course-detail.component.html',
  styleUrls: ['./course-detail.component.css']
})
export class CourseDetailComponent implements OnInit {
  course?: Course;
  chapters: Chapter[] = [];
  reviews: Review[] = [];
  newRating = 5;
  newComment = '';
  isLoading = false;
  errorMessage = '';
  successMessage = '';

  constructor(
    private readonly route: ActivatedRoute,
    private readonly courseService: CourseService,
    private readonly chapterService: ChapterService,
    readonly cartService: CartService,
    private readonly reviewService: ReviewService,
    readonly authService: AuthService
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.isLoading = true;
    this.courseService.getById(id).subscribe({
      next: (course) => {
        this.course = course;
        this.isLoading = false;
        this.chapterService.getByCourse(id).subscribe({ next: (ch) => (this.chapters = ch ?? []) });
        this.reviewService.getByCourse(id).subscribe({ next: (rv) => (this.reviews = rv ?? []) });
      },
      error: () => { this.isLoading = false; this.errorMessage = '載入課程失敗'; }
    });
  }

  onAddToCart(): void {
    if (!this.course) return;
    this.cartService.addItem({
      courseId: this.course.id!,
      courseName: this.course.courseName,
      price: this.course.price ?? 0,
      coverImageUrl: this.course.coverImageUrl
    });
    this.successMessage = '已加入購物車！';
  }

  onSubmitReview(): void {
    if (!this.course?.id) return;
    this.reviewService.submit(this.course.id, this.newRating, this.newComment).subscribe({
      next: (review) => {
        this.reviews = [review, ...this.reviews];
        this.newRating = 5;
        this.newComment = '';
        this.successMessage = '評論已送出！';
      },
      error: (err) => {
        this.errorMessage = err?.error?.message ?? '送出評論失敗';
      }
    });
  }

  starsArray(n: number): number[] {
    return Array(n).fill(0);
  }
}
