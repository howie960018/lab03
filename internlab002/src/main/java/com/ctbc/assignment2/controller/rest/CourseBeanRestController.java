package com.ctbc.assignment2.controller.rest;

import com.ctbc.assignment2.bean.CourseBean;
import com.ctbc.assignment2.bean.CourseCategoryBean;
import com.ctbc.assignment2.service.CourseBeanService;
import com.ctbc.assignment2.service.CourseCategoryBeanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;


@RestController
@RequestMapping("/api/course")
@CrossOrigin

public class CourseBeanRestController {

    @Autowired
    private CourseBeanService courseService;

    @Autowired
    private CourseCategoryBeanService categoryService;

    @GetMapping("/all")
    public List<CourseBean> getAll() {
        return courseService.findAll();
    }

    @GetMapping
    public Page<CourseBean> getPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "id,asc") String sort
    ) {
        String[] sortArr = sort.split(",");
        Sort.Direction direction = Sort.Direction.fromString(sortArr[1]);
        Sort sortObj = Sort.by(direction, sortArr[0]);

        Pageable pageable = PageRequest.of(page, size, sortObj);

        return courseService.findPage(keyword, categoryId, pageable);
    }


    @GetMapping("/{id}")
    public CourseBean getById(
           
            @PathVariable Long id) {
        return courseService.findById(id);
    }


    @DeleteMapping("/{id}")
    public void deleteById(
          
            @PathVariable Long id) {
        courseService.deleteById(id);
    }

    @PutMapping("/{id}")
    public CourseBean updateCourse(
            @PathVariable Long id,
            @Valid @RequestBody CourseBean course) {
        course.setId(id);
        return courseService.save(course);
    }

  
    @PatchMapping("/{id}")
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

        return courseService.save(existing);
    }

  
    @PostMapping
    public CourseBean save(
            @Valid @RequestBody 
          
            CourseBean course) {
        return courseService.save(course);
    }

  
    @PostMapping("/category/{categoryId}")
    public CourseBean saveWithCategory(
            @Valid @RequestBody CourseBean course,
          
            @PathVariable Long categoryId) {
        
        CourseCategoryBean category = categoryService.findById(categoryId);
        course.setCategory(category);
        return courseService.save(course);
    }

    @GetMapping("/category/{categoryId}")
    public List<CourseBean> getCoursesByCategory(
            
            @PathVariable Long categoryId) {
        return courseService.findByCategoryId(categoryId);
    }
}