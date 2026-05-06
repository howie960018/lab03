export interface Page<T> {
  content: T[];           // 當頁資料
  number: number;        // 目前頁碼 (0-based)
  size: number;          // 每頁筆數
  totalElements: number; // 總筆數
  totalPages: number;    // 總頁數
  first: boolean;
  last: boolean;
}