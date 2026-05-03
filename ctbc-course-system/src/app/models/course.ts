import { Category } from './category';

export interface Course {
  id?: number;
  courseName: string;
  courseSummary?: string;
  courseDescription?: string;
  price?: number;
  coverImageUrl?: string;
  instructorName?: string;
  durationHours?: number;
  level?: string;
  status?: string;
  category?: Category;
  categoryId?: number;
  isEditing?: boolean;
}

export interface PageResult<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}
