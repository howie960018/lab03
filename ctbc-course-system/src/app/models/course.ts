import { Category } from './category';

export interface Course {
  id?: number;
  courseName: string;
  courseSummary?: string;
  courseDescription?: string;
  price?: number;
  category?: Category;
  categoryId?: number;
  imageUrl?: string; // ✅ 新增
  isEditing?: boolean;
}