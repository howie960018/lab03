import { Category } from './category';

export interface Course {
  id?: number;
  courseName: string;
  courseSummary?: string;
  courseDescription?: string;
  price?: number;
  category?: Category;

  // UI helper field (backend accepts category object, and also provides a dedicated endpoint to save with categoryId)
  categoryId?: number;
  isEditing?: boolean;
}
