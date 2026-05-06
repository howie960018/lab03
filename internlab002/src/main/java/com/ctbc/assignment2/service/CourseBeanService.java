package com.ctbc.assignment2.service;

import com.ctbc.assignment2.bean.CourseBean;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface CourseBeanService {

    List<CourseBean> findAll();

    CourseBean findById(Long id);

    Page<CourseBean> findPage(
            String keyword,
            Long categoryId,
            Pageable pageable
    );

    CourseBean save(CourseBean course);

    void deleteById(Long id);

    List<CourseBean> findByCategoryId(Long categoryId);
}