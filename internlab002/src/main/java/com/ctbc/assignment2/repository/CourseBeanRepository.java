package com.ctbc.assignment2.repository;

import com.ctbc.assignment2.bean.CourseBean;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CourseBeanRepository extends JpaRepository<CourseBean, Long>, JpaSpecificationExecutor<CourseBean> {
    
    boolean existsByCourseName(String courseName);
    
    boolean existsByCourseNameAndIdNot(String courseName, Long id);
    
    List<CourseBean> findByCategoryId(Long categoryId);
    
    boolean existsByCategoryId(Long categoryId);
    
    Page<CourseBean> findByCourseNameContainingIgnoreCase(String keyword, Pageable pageable);

    // 查名稱 OR 描述
    Page<CourseBean> findByCourseNameContainingIgnoreCaseOrCourseSummaryContainingIgnoreCase(
            String nameKeyword, String summaryKeyword, Pageable pageable);

    // 關鍵字 + 類別
    Page<CourseBean> findByCategoryIdAndCourseNameContainingIgnoreCaseOrCategoryIdAndCourseSummaryContainingIgnoreCase(
            Long categoryId1, String nameKeyword, Long categoryId2, String summaryKeyword, Pageable pageable);
}