import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

import { Category } from '../models/category';
import { CategoryService } from '../services/category.service';

@Component({
  selector: 'app-category-catalog',
  templateUrl: './category-catalog.component.html',
  styleUrls: ['./category-catalog.component.css']
})
export class CategoryCatalogComponent implements OnInit {

  categories: Category[] = [];

  constructor(
    private categoryService: CategoryService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadCategories(); // ✅ 現在存在了
  }

  /** ✅ 載入所有類別（前台用） */
  loadCategories(): void {
    this.categoryService.getAll().subscribe({
      next: data => this.categories = data ?? [],
      error: err => {
        console.error(err);
        this.categories = [];
      }
    });
  }

  goToCategory(category: Category): void {
    if (!category.id) return;

    this.router.navigate(['/courses'], {
      queryParams: {
        category: category.id,
        page: 0
      }
    });
  }
}