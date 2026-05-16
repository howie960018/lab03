import { Category } from './category';

export interface Course {
  id?: number;
  courseName: string;
  courseSummary?: string;
  courseDescription?: string;
  price?: number;
  imageUrl?: string;
  categoryId?: number;
  category?: Category;
  instructor?: {
    id: number;
    username: string;
  };
  isEditing?: boolean;
}