import { Component, OnInit } from '@angular/core';
import { CourseService } from '../services/course.service';
import { ChapterService } from '../services/chapter.service';
import { Course } from '../models/course';
import { Chapter } from '../models/chapter';
import { finalize } from 'rxjs';

@Component({
  selector: 'app-admin-chapters',
  templateUrl: './admin-chapters.component.html',
  styleUrls: ['./admin-chapters.component.css']
})
export class AdminChaptersComponent implements OnInit {
  courses: Course[] = [];
  chapters: Chapter[] = [];
  selectedCourseId?: number;
  isLoading = false;
  errorMessage = '';

  newChapter: Chapter = { courseId: 0, title: '', sortOrder: 0 };
  private readonly snapshots = new WeakMap<Chapter, Chapter>();

  constructor(
    private readonly courseService: CourseService,
    private readonly chapterService: ChapterService
  ) {}

  ngOnInit(): void {
    this.courseService.getAll().subscribe({ next: (c) => (this.courses = c ?? []) });
  }

  onCourseSelect(): void {
    if (!this.selectedCourseId) { this.chapters = []; return; }
    this.newChapter.courseId = this.selectedCourseId;
    this.loadChapters();
  }

  loadChapters(): void {
    if (!this.selectedCourseId) return;
    this.isLoading = true;
    this.chapterService.getByCourse(this.selectedCourseId).subscribe({
      next: (ch) => { this.chapters = (ch ?? []).map(c => ({ ...c, isEditing: false })); this.isLoading = false; },
      error: () => { this.isLoading = false; }
    });
  }

  onAdd(): void {
    if (!this.selectedCourseId || !this.newChapter.title.trim()) { this.errorMessage = '請填寫章節標題'; return; }
    this.errorMessage = '';
    this.chapterService.save({ ...this.newChapter, courseId: this.selectedCourseId }).subscribe({
      next: () => { this.newChapter = { courseId: this.selectedCourseId!, title: '', sortOrder: 0 }; this.loadChapters(); },
      error: (err) => { this.errorMessage = err?.error?.message ?? '新增失敗'; }
    });
  }

  onEdit(ch: Chapter): void {
    this.snapshots.set(ch, { ...ch });
    ch.isEditing = true;
  }

  onCancel(ch: Chapter): void {
    const snap = this.snapshots.get(ch);
    if (snap) { Object.assign(ch, snap); }
    ch.isEditing = false;
  }

  onSave(ch: Chapter): void {
    this.chapterService.save(ch).pipe(finalize(() => (ch.isEditing = false))).subscribe({
      next: () => this.loadChapters(),
      error: (err) => { this.errorMessage = err?.error?.message ?? '儲存失敗'; ch.isEditing = true; }
    });
  }

  onDelete(id: number | undefined): void {
    if (!id) return;
    this.chapterService.deleteById(id).subscribe({
      next: () => this.loadChapters(),
      error: (err) => { this.errorMessage = err?.error?.message ?? '刪除失敗'; }
    });
  }

  courseName(id: number): string {
    return this.courses.find(c => c.id === id)?.courseName ?? String(id);
  }
}
