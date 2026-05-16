
import { Component, OnInit } from '@angular/core';
import { Course } from '../models/course';
import { FavoriteService } from './favorite.service';

@Component({
  selector: 'app-favorite',
  templateUrl: './favorite.component.html',
  styleUrls: ['./favorite.component.css']
})
export class FavoriteComponent implements OnInit {

  courses: Course[] = [];
  isLoading = false;

  constructor(private readonly favoriteService: FavoriteService) {}

  ngOnInit(): void {
    this.loadFavorites();
  }

  loadFavorites(): void {
    this.isLoading = true;
    this.favoriteService.getMyFavorites().subscribe({
      next: data => {
        this.courses = data ?? [];
        this.isLoading = false;
      },
      error: err => {
        console.error(err);
        this.courses = [];
        this.isLoading = false;
      }
    });
  }

  onRemove(courseId?: number): void {
    if (!courseId) return;

    this.favoriteService.remove(courseId).subscribe({
      next: () => this.loadFavorites(),
      error: err => {
        console.error(err);
        alert('取消收藏失敗');
      }
    });
  }
}