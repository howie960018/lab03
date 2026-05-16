package com.ctbc.assignment2.service.impl;

import com.ctbc.assignment2.bean.CourseBean;
import com.ctbc.assignment2.bean.CourseCategoryBean;
import com.ctbc.assignment2.exception.DuplicateCourseNameException;
import com.ctbc.assignment2.exception.ForbiddenException;
import com.ctbc.assignment2.exception.ResourceNotFoundException;
import com.ctbc.assignment2.repository.CourseBeanRepository;
import com.ctbc.assignment2.repository.CourseFavoriteRepository;
import com.ctbc.assignment2.service.AppUserService;
import com.ctbc.assignment2.service.CourseBeanService;
import com.ctbc.assignment2.service.CourseCategoryBeanService;
import com.ctbc.assignment2.utils.DefaultImageUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CourseBeanServiceJPAImplement implements CourseBeanService {

    @Autowired
    private CourseBeanRepository repo;

    @Autowired
    private AppUserService userService;

    @Autowired
    private CourseCategoryBeanService categoryService;

    @Autowired
    private CourseFavoriteRepository favoriteRepository;

    // =============================================
    // 查詢全部
    // =============================================
    @Override
    public List<CourseBean> findAll() {
        if (isAdmin()) {
            return repo.findAll();
        }
        // instructor 只看自己的
        return repo.findByInstructorUsername(getCurrentUsername());
    }

    // =============================================
    // 查單筆（要做 ownership 檢查）
    // =============================================
    @Override
    public CourseBean findById(Long id) {
        CourseBean course = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found: " + id));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // ✅✅1 未登入（匿名）
        if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
            return course;
        }

        // ✅✅2 ADMIN → OK
        if (isAdmin()) {
            return course;
        }

        // ✅✅3 USER → 可以看（前台）
        boolean isUser = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER"));
        if (isUser) {
            return course;
        }

        // ✅✅4 INSTRUCTOR → 限制只能看自己
        if (!course.getInstructor().getUsername().equals(getCurrentUsername())) {
            throw new ForbiddenException("不可存取他人課程");
        }
        return course;
    }

    // =============================================
    // 新增 / 更新
    // =============================================
    @Override
    public CourseBean save(CourseBean course) {
        String username = getCurrentUsername();

        // ✅ 新增
        if (course.getId() == null) {
            course.setInstructor(userService.findByUsername(username));
            if (repo.existsByCourseName(course.getCourseName())) {
                throw new DuplicateCourseNameException("課程名稱已存在：" + course.getCourseName());
            }
        } else {
            // ✅ 更新
            CourseBean existing = repo.findById(course.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

            // ✅ 權限檢查
            if (!isAdmin() && !existing.getInstructor().getUsername().equals(username)) {
                throw new ForbiddenException("不能修改他人課程");
            }

            // ✅ 保留 instructor
            course.setInstructor(existing.getInstructor());

            if (repo.existsByCourseNameAndIdNot(course.getCourseName(), course.getId())) {
                throw new DuplicateCourseNameException("課程名稱已存在：" + course.getCourseName());
            }
        }

        CourseBean saved = repo.save(course);

        // ✅ 預設圖片
        if (saved.getImageUrl() == null || saved.getImageUrl().isBlank()) {
            saved.setImageUrl(DefaultImageUtil.defaultCourseImage(saved.getId()));
            saved = repo.save(saved);
        }

        return saved;
    }

    // =============================================
    // 刪除（owner check）
    // =============================================
    @Transactional
    @Override
    public void deleteById(Long id) {
        CourseBean course = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        if (!isAdmin() && !course.getInstructor().getUsername().equals(getCurrentUsername())) {
            throw new ForbiddenException("不能刪除他人課程");
        }

        // ✅ 先刪收藏
        favoriteRepository.deleteByCourseId(id);
        // ✅ 再刪課程
        repo.delete(course);
    }

    // =============================================
    // 分類查詢（要隔離）
    // =============================================
    @Override
    public List<CourseBean> findByCategoryId(Long categoryId) {
        categoryService.findById(categoryId);

        if (isAdmin()) {
            return repo.findByCategoryId(categoryId);
        }

        return repo.findByInstructorUsername(getCurrentUsername()).stream()
                .filter(c -> c.getCategory() != null
                        && c.getCategory().getId().equals(categoryId))
                .toList();
    }

    // =============================================
    // 分頁（保留搜尋條件）
    // =============================================
    @Override
    public Page<CourseBean> findPage(String keyword, Long categoryId,
                                     String instructor, Pageable pageable) {
        boolean hasKeyword = keyword != null && !keyword.isBlank();
        boolean hasCategory = categoryId != null;
        boolean hasInstructor = instructor != null && !instructor.isBlank();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        boolean isAnonymous = auth == null || !auth.isAuthenticated()
                || auth.getName().equals("anonymousUser");

        boolean isUser = !isAnonymous && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER"));

        boolean isInstructor = !isAnonymous && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_INSTRUCTOR"));

        // =============================================
        // ✅ 公開 / USER / ADMIN / INSTRUCTOR（統一邏輯）
        // =============================================
        if (isAnonymous || isUser || isAdmin() || isInstructor) {
            return repo.findAll((root, query, cb) -> {
                var predicates = cb.conjunction(); // AND 條件集合

                // ✅ instructor
                if (hasInstructor) {
                    predicates = cb.and(predicates,
                            cb.equal(root.get("instructor").get("username"), instructor));
                }

                // ✅ category
                if (hasCategory) {
                    predicates = cb.and(predicates,
                            cb.equal(root.get("category").get("id"), categoryId));
                }

                // ✅ keyword（名稱 or 描述）
                if (hasKeyword) {
                    String like = "%" + keyword.toLowerCase() + "%";
                    predicates = cb.and(predicates,
                            cb.or(
                                    cb.like(cb.lower(root.get("courseName")), like),
                                    cb.like(cb.lower(root.get("courseSummary")), like)
                            ));
                }

                return predicates;
            }, pageable);
        }

        // =============================================
        // ✅ INSTRUCTOR（只能看自己）
        // =============================================
        String username = getCurrentUsername();
        return repo.findAll((root, query, cb) -> {
            var predicates = cb.conjunction();

            // ✅ 強制只看自己（關鍵）
            predicates = cb.and(predicates,
                    cb.equal(root.get("instructor").get("username"), username));

            // ❗ 沒有 instructor filter（完全忽略前端傳的）

            if (hasCategory) {
                predicates = cb.and(predicates,
                        cb.equal(root.get("category").get("id"), categoryId));
            }

            if (hasKeyword) {
                String like = "%" + keyword.toLowerCase() + "%";
                predicates = cb.and(predicates,
                        cb.or(
                                cb.like(cb.lower(root.get("courseName")), like),
                                cb.like(cb.lower(root.get("courseSummary")), like)
                        ));
            }

            return predicates;
        }, pageable);
    }

    // =============================================
    // 修改分類（要檢查 owner）
    // =============================================
    @Override
    public CourseBean updateCategory(Long courseId, Long categoryId) {
        CourseBean course = repo.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        if (!isAdmin() && !course.getInstructor().getUsername().equals(getCurrentUsername())) {
            throw new ForbiddenException("不能修改他人課程分類");
        }

        CourseCategoryBean category = categoryService.findById(categoryId);
        course.setCategory(category);
        return repo.save(course);
    }

    // =============================================
    // 工具方法
    // =============================================
    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }

    private boolean isAdmin() {
        return SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}