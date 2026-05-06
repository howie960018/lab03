
package com.ctbc.assignment2.utils;

/**
 * DefaultImageUtil
 *
 * 統一管理「系統預設圖片規則」
 * - 課程沒有上傳圖片時使用
 * - 不依賴 DB、不依賴 Spring、純工具類
 */
public final class DefaultImageUtil {

    // 私有建構子，避免被 new
    private DefaultImageUtil() {}

    /**
     * 產生課程的預設圖片（固定、不亂跳）
     *
     * @param courseId 課程 ID
     * @return 圖片 URL
     */
    public static String defaultCourseImage(Long courseId) {

        if (courseId == null) {
            // 端端保護（理論上不會發生）
            return fallbackImage();
        }

        // Picsum：seed 固定 → 同一課程永遠同一張
        return "https://picsum.photos/seed/course-" + courseId + "/400/250";
    }

    /**
     * 當 courseId 不存在時的保底圖片
     */
    private static String fallbackImage() {
        return "https://picsum.photos/400/250";
    }
}