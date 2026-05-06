package com.ctbc.assignment2.service.impl;

import com.ctbc.assignment2.bean.CourseBean;
import com.ctbc.assignment2.exception.DuplicateCourseNameException;
import com.ctbc.assignment2.exception.ResourceNotFoundException;
import com.ctbc.assignment2.repository.CourseBeanRepository;
import com.ctbc.assignment2.service.CourseCategoryBeanService;
import com.ctbc.assignment2.service.CourseBeanService;
import com.ctbc.assignment2.utils.DefaultImageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service 實作層 (Implementation)
 * 
 * - @Service: 這是 Spring 提供的一種標註，告訴 Spring 這個類別是服務層元件。
 *   在系統啟動時，Spring 會將他實例化 (Bean) 並放入容器中統一管理，我們之後便可透過 @Autowired 注入此元件。
 * - 介面繼承 (implements): 確保該類別實作了 CourseBeanService 介面定義的所有方法，落實規格和實作分離。
 * - @Transactional (常放在更新/刪除方法前): 用於控制資料庫事務 (Database Transaction)。
 *   確保「同一個方法內的資料操作要嘛全部成功，要嘛全部失敗退回原狀」，避免發生意外狀況導致一半資料變更的情況。
 */

@Service
public class CourseBeanServiceJPAImplement implements CourseBeanService {

    @Autowired
    private CourseBeanRepository repo;

    @Autowired
    private CourseCategoryBeanService categoryService;

    @Override
    public List<CourseBean> findAll() {
        return repo.findAll();
    }

    @Override
    public CourseBean findById(Long id) {
        return repo.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Course not found: " + id));
    }

    @Override
    public CourseBean save(CourseBean course) {

        // ===== 名稱重複檢查 =====
        if (course.getId() != null) {
            // 更新
            if (repo.existsByCourseNameAndIdNot(course.getCourseName(),
                    course.getId())) {
                throw new DuplicateCourseNameException("課程名稱已存在： " +
                        course.getCourseName());
            }
        } else {
            // 新增
            if (repo.existsByCourseName(course.getCourseName())) {
                throw new DuplicateCourseNameException("課程名稱已存在： " +
                        course.getCourseName());
            }
        }

        // ===== 第一次存（為了取得 ID）=====
        CourseBean saved = repo.save(course);

        // ===== 補預設圖片（只在沒給 imageUrl 時）=====
        if (saved.getImageUrl() == null ||
                saved.getImageUrl().isBlank()) {
            saved.setImageUrl(
                    DefaultImageUtil.defaultCourseImage(saved.getId())
            );

            // 再存一次，把 imageUrl 更新進 DB
            saved = repo.save(saved);
        }

        return saved;
    }

    @Override
    public void deleteById(Long id) {
        CourseBean course = findById(id); // 驗證存在
        repo.delete(course);
    }

    @Override
    public List<CourseBean> findByCategoryId(Long categoryId) {
        categoryService.findById(categoryId); // 驗證分類存在
        return repo.findByCategoryId(categoryId);
    }

    @Override
    public Page<CourseBean> findPage(String keyword, Long categoryId,
                                     Pageable pageable) {
        boolean hasKeyword = keyword != null && !keyword.isBlank();
        boolean hasCategory = categoryId != null;

        if (hasKeyword && hasCategory) {
            return repo
                    .findByCategoryIdAndCourseNameContainingIgnoreCaseOrCategoryIdAndCourseSummaryContainingIgnoreCase(
                            categoryId, keyword, categoryId, keyword, pageable);
        }

        if (hasKeyword) {
            return repo.findByCourseNameContainingIgnoreCaseOrCourseSummaryContainingIgnoreCase(
                    keyword, keyword, pageable);
        }

        if (hasCategory) {
            return repo.findAll((root, query, cb) ->
                    cb.equal(root.get("category").get("id"), categoryId),
                    pageable);
        }

        return repo.findAll(pageable);
    }
}