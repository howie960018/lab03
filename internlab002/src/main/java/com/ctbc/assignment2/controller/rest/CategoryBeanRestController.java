package com.ctbc.assignment2.controller.rest;

import com.ctbc.assignment2.bean.CourseCategoryBean;
import com.ctbc.assignment2.service.CourseCategoryBeanService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/api/category")
@CrossOrigin
public class CategoryBeanRestController {

    @Autowired
    private CourseCategoryBeanService categoryService;

    // 所有人可查
    @GetMapping
    public List<CourseCategoryBean> getAll() {
        return categoryService.findAll();
    }

    // 所有人可查
    @GetMapping("/{id}")
    public CourseCategoryBean getById(@PathVariable Long id) {
        return categoryService.findById(id);
    }

    // 只有 ADMIN 可以刪
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteById(@PathVariable Long id) {
        categoryService.deleteById(id);
    }

    // 只有 ADMIN 可以改
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public CourseCategoryBean updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CourseCategoryBean category) {
        category.setId(id);
        return categoryService.save(category);
    }

    // 只有 ADMIN 可以新增
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public CourseCategoryBean save(@Valid @RequestBody CourseCategoryBean category) {
        return categoryService.save(category);
    }
}