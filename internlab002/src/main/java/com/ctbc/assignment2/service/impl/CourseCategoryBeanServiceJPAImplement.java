package com.ctbc.assignment2.service.impl;

import com.ctbc.assignment2.bean.CourseCategoryBean;
import com.ctbc.assignment2.exception.DuplicateCourseNameException;
import com.ctbc.assignment2.exception.ResourceNotFoundException;
import com.ctbc.assignment2.repository.CourseBeanRepository;
import com.ctbc.assignment2.repository.CourseCategoryBeanRepository;
import com.ctbc.assignment2.service.CourseCategoryBeanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import com.ctbc.assignment2.exception.CategoryHasCoursesException;

/**
 * 課程類別 Service 實作層
 * 
 * - 設計概念：這裡是系統架構的「業務邏輯層」。接收 Controller 傳來的需求，並操作 Repository (資料訪問層) 來取得/修改資料。
 * - @Service: 類別層級的標註。讓 Spring 在啟動時能掃描到這個類別，產生可以被 @Autowired 注入的單例 (Singleton) Bean。
 */


@Service
public class CourseCategoryBeanServiceJPAImplement implements CourseCategoryBeanService {

    @Autowired
    private CourseCategoryBeanRepository repo;

    @Autowired
    private CourseBeanRepository courseRepo;

    @Override
    public List<CourseCategoryBean> findAll() {
        return repo.findAll();
    }

    @Override
    public CourseCategoryBean findById(Long id) {
        return repo.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Category not found: " + id));
    }

    @Override
    public CourseCategoryBean save(CourseCategoryBean category) {

        if (category.getId() != null) {
            CourseCategoryBean existing = repo.findById(category.getId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Category not found: " +
                                    category.getId()));

            if (repo.existsByCategoryNameAndIdNot(category.getCategoryName(),
                    category.getId())) {
                throw new DuplicateCourseNameException("類別名稱已存在： " +
                        category.getCategoryName());
            }

            existing.setCategoryName(category.getCategoryName());
            return repo.save(existing);
        }

        if (repo.existsByCategoryName(category.getCategoryName())) {
            throw new DuplicateCourseNameException("類別名稱已存在： " +
                    category.getCategoryName());
        }

        return repo.save(category);
    }

    @Override
    public void deleteById(Long id) {
        // 1 先確認分類存在（不存在會直接丟 404）
        CourseCategoryBean category = findById(id);

        // 2 檢查該分類底下是否還有課程
        if (courseRepo.existsByCategoryId(id)) {
            throw new CategoryHasCoursesException("此分類底下仍有課程，無法刪除");
        }

        // 3 沒有課程，才真的刪除
        repo.delete(category);
    }
}