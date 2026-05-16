package com.ctbc.assignment2.controller.rest;

import com.ctbc.assignment2.bean.CourseBean;
import com.ctbc.assignment2.bean.CourseCategoryBean;
import com.ctbc.assignment2.service.AppUserService;
import com.ctbc.assignment2.service.CourseBeanService;
import com.ctbc.assignment2.service.CourseCategoryBeanService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/api/course")
@CrossOrigin
public class CourseBeanRestController {

    @Autowired
    private CourseBeanService courseService;

    @Autowired
    private CourseCategoryBeanService categoryService;

    @Autowired
    private AppUserService appUserService;

    // =============================================
    // 查所有講師名單（公開）
    // 必須在 /{id} 之前宣告，避免路由衝突
    // =============================================
    @GetMapping("/instructors")
    public List<String> getInstructors() {
        return appUserService.findUsernamesByRole("INSTRUCTOR");
    }

    // =============================================
    // 查全部（後台用）ADMIN/INSTRUCTOR 才能用
    // =============================================
    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN','INSTRUCTOR')")
    public List<CourseBean> getAll() {
        return courseService.findAll();
    }

    // =============================================
    // 分頁查詢（前後台都會用）
    // GET 開放（SecurityConfig 已 allow）
    // =============================================
    @GetMapping
    public Page<CourseBean> getPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String instructor,
            @RequestParam(defaultValue = "id,asc") String sort
    ) {
        String[] sortArr = sort.split(",");
        Sort.Direction direction = Sort.Direction.fromString(sortArr[1]);
        Sort sortObj = Sort.by(direction, sortArr[0]);
        Pageable pageable = PageRequest.of(page, size, sortObj);
        return courseService.findPage(keyword, categoryId, instructor, pageable);
    }

    // =============================================
    // 單筆查詢（公開 or 由 service 控權限）
    // =============================================
    @GetMapping("/{id}")
    public CourseBean getById(@PathVariable Long id) {
        return courseService.findById(id);
    }

    // =============================================
    // 刪除（只能 ADMIN / INSTRUCTOR）
    // =============================================
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','INSTRUCTOR')")
    public void deleteById(@PathVariable Long id) {
        courseService.deleteById(id);
    }

    // =============================================
    // 完整更新
    // =============================================
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','INSTRUCTOR')")
    public CourseBean updateCourse(
            @PathVariable Long id,
            @Valid @RequestBody CourseBean course) {
        course.setId(id);
        return courseService.save(course);
    }

    // =============================================
    // 部分更新
    // =============================================
    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','INSTRUCTOR')")
    public CourseBean patchCourse(
            @PathVariable Long id,
            @RequestBody CourseBean patchData) {
        CourseBean existing = courseService.findById(id);
        if (patchData.getCourseName() != null) {
            existing.setCourseName(patchData.getCourseName());
        }
        if (patchData.getPrice() != null) {
            existing.setPrice(patchData.getPrice());
        }
        if (patchData.getCourseSummary() != null) {
            existing.setCourseSummary(patchData.getCourseSummary());
        }
        if (patchData.getImageUrl() != null) {
            existing.setImageUrl(patchData.getImageUrl());
        }
        return courseService.save(existing);
    }

    // =============================================
    // 新增課程
    // =============================================
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','INSTRUCTOR')")
    public CourseBean save(@Valid @RequestBody CourseBean course) {
        return courseService.save(course);
    }

    // =============================================
    // 更新分類
    // =============================================
    @PutMapping("/{id}/category/{categoryId}")
    @PreAuthorize("hasAnyRole('ADMIN','INSTRUCTOR')")
    public CourseBean updateCourseCategory(
            @PathVariable Long id,
            @PathVariable Long categoryId) {
        return courseService.updateCategory(id, categoryId);
    }

    // =============================================
    // 新增 + 指定分類
    // =============================================
    @PostMapping("/category/{categoryId}")
    @PreAuthorize("hasAnyRole('ADMIN','INSTRUCTOR')")
    public CourseBean saveWithCategory(
            @Valid @RequestBody CourseBean course,
            @PathVariable Long categoryId) {
        CourseCategoryBean category = categoryService.findById(categoryId);
        course.setCategory(category);
        return courseService.save(course);
    }

    // =============================================
    // 查分類課程（公開）
    // =============================================
    @GetMapping("/category/{categoryId}")
    public List<CourseBean> getCoursesByCategory(@PathVariable Long categoryId) {
        return courseService.findByCategoryId(categoryId);
    }
}