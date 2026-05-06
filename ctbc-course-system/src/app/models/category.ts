export interface Category {
  id?: number;
  categoryName: string;
  isEditing?: boolean; // 用於後台管理介面的編輯狀態切換
}