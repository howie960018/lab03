package com.ctbc.assignment2.service.impl;

import com.ctbc.assignment2.bean.CourseCategoryBean;
import com.ctbc.assignment2.exception.CategoryHasCoursesException;
import com.ctbc.assignment2.exception.DuplicateCourseNameException;
import com.ctbc.assignment2.exception.ResourceNotFoundException;
import com.ctbc.assignment2.repository.CourseBeanRepository;
import com.ctbc.assignment2.repository.CourseCategoryBeanRepository;
import com.ctbc.assignment2.service.CourseCategoryBeanService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
        return repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + id));
    }

    @Override
    public CourseCategoryBean save(CourseCategoryBean category) {

        if (category.getId() != null) {
            // ✅ 更新
            CourseCategoryBean existing = repo.findById(category.getId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Category not found: " + category.getId()));

            if (repo.existsByCategoryNameAndIdNot(
                    category.getCategoryName(), category.getId())) {
                throw new DuplicateCourseNameException(
                        "類別名稱已存在：" + category.getCategoryName());
            }

            existing.setCategoryName(category.getCategoryName());
            return repo.save(existing);
        }

        // ✅ 新增
        if (repo.existsByCategoryName(category.getCategoryName())) {
            throw new DuplicateCourseNameException(
                    "類別名稱已存在：" + category.getCategoryName());
        }

        return repo.save(category);
    }

    @Override
    public void deleteById(Long id) {
        // 先確認分類存在
        CourseCategoryBean category = findById(id);

        // 檢查該分類底下是否還有課程
        if (courseRepo.existsByCategoryId(id)) {
            throw new CategoryHasCoursesException("此分類底下仍有課程，無法刪除");
        }

        // 確認該類別沒有課程才刪除
        repo.delete(category);
    }
}