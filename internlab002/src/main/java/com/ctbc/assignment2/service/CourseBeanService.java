package com.ctbc.assignment2.service;

import com.ctbc.assignment2.bean.CourseBean;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 課程服務介面
 * 定義「課程管理」需要提供哪些功能（如：尋找所有、依 ID 尋找、儲存、刪除）。
 */
public interface CourseBeanService {

    List<CourseBean> findAll();

    CourseBean findById(Long id);

    Page<CourseBean> findPage(String keyword, Long categoryId, String instructor, Pageable pageable);

    CourseBean save(CourseBean course);

    void deleteById(Long id);

    List<CourseBean> findByCategoryId(Long categoryId);

    CourseBean updateCategory(Long courseId, Long categoryId);
}