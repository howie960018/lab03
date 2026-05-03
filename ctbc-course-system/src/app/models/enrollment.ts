export interface Enrollment {
  id?: number;
  courseId: number;
  courseName: string;
  coverImageUrl?: string;
  price: number;
  status: string;
  enrolledAt?: string;
}
