package com.ctbc.assignment2.service;

import com.ctbc.assignment2.bean.CourseCategoryBean;

import java.util.List;

/**
 * 課程分類服務介面
 * 定義了課程分類管理提供哪些功能（如：尋找所有、依 ID 尋找、儲存、刪除）
 */
public interface CourseCategoryBeanService {

    List<CourseCategoryBean> findAll();

    CourseCategoryBean findById(Long id);

    CourseCategoryBean save(CourseCategoryBean category);

    void deleteById(Long id);
}