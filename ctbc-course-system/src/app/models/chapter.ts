export interface Chapter {
  id?: number;
  courseId: number;
  title: string;
  description?: string;
  sortOrder?: number;
  videoPlaceholderUrl?: string;
  isEditing?: boolean;
}
