import { Component, OnInit } from '@angular/core';
import { finalize } from 'rxjs';

import { Category } from '../models/category';
import { CategoryService } from '../services/category.service';

@Component({
  selector: 'app-category',
  templateUrl: './category.component.html',
  styleUrls: ['./category.component.css']
})
export class CategoryComponent implements OnInit {
  categories: Category[] = [];

  newCategory: Category = {
    categoryName: ''
  };

  isLoading = false;
  errorMessage = '';

  private readonly originalSnapshot = new WeakMap<Category, Pick<Category, 'categoryName'>>();

  constructor(private readonly categoryService: CategoryService) {}

  ngOnInit(): void {
    this.reloadData();
  }

  reloadData(): void {
    this.isLoading = true;
    this.errorMessage = '';

    this.categoryService
      .getAll()
      .pipe(finalize(() => (this.isLoading = false)))
      .subscribe({
        next: (data) => {
          console.log('categories:', data);
          this.categories = (data ?? []).map((c) => ({
            ...c,
            isEditing: false
          }));
        },
        error: (error) => {
          console.error(error);
          this.errorMessage = '載入類別資料失敗，請稍後再試。';
          this.categories = [];
        }
      });
  }

  onAddCategory(): void {
    this.isLoading = true;
    this.errorMessage = '';

    const payload: Category = {
      categoryName: (this.newCategory.categoryName ?? '').trim()
    };

    if (!payload.categoryName) {
      this.isLoading = false;
      this.errorMessage = '類別名稱為必填。';
      return;
    }

    this.categoryService
      .save(payload)
      .pipe(finalize(() => (this.isLoading = false)))
      .subscribe({
        next: () => {
          this.newCategory = { categoryName: '' };
          this.reloadData();
        },
        error: (error) => {
          console.error(error);
          this.errorMessage = '新增類別失敗，請稍後再試。';
        }
      });
  }

  onDeleteCategory(id: number | undefined): void {
    if (id === undefined) {
      this.errorMessage = '刪除失敗：id 不可為空。';
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';

    this.categoryService
      .deleteById(id)
      .pipe(finalize(() => (this.isLoading = false)))
      .subscribe({
        next: () => this.reloadData(),
        error: (error) => {
          console.error(error);
          this.errorMessage = '刪除類別失敗，請稍後再試。';
        }
      });
  }

  onToggleModify(category: Category): void {
    if (!category.isEditing) {
      this.originalSnapshot.set(category, {
        categoryName: category.categoryName
      });
    }

    category.isEditing = true;
    this.errorMessage = '';
  }

  onCancelModify(category: Category): void {
    const snapshot = this.originalSnapshot.get(category);
    if (snapshot) {
      category.categoryName = snapshot.categoryName;
    }

    category.isEditing = false;
    this.errorMessage = '';
  }

  onUpdateCategory(category: Category): void {
    if (category.id === undefined) {
      this.errorMessage = '更新失敗：id 不可為空。';
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';

    const payload: Category = {
      id: category.id,
      categoryName: (category.categoryName ?? '').trim()
    };

    if (!payload.categoryName) {
      this.isLoading = false;
      this.errorMessage = '類別名稱為必填。';
      return;
    }

    this.categoryService
      .save(payload)
      .pipe(finalize(() => (this.isLoading = false)))
      .subscribe({
        next: () => {
          category.isEditing = false;
          this.originalSnapshot.delete(category);
          this.reloadData();
        },
        error: (error) => {
          console.error(error);
          this.errorMessage = '更新類別失敗，請稍後再試。';
        }
      });
  }
}
