package com.ctbc.assignment2.controller.rest;

import com.ctbc.assignment2.bean.CourseBean;
import com.ctbc.assignment2.service.CourseFavoriteService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorite")
@PreAuthorize("hasRole('USER')")
public class CourseFavoriteController {

    @Autowired
    private CourseFavoriteService service;

    @PostMapping("/{courseId}")
    public void add(@PathVariable Long courseId) {
        service.addFavorite(courseId);
    }

    @GetMapping
    public List<CourseBean> myFavorites() {
        return service.myFavorites();
    }

    @DeleteMapping("/{courseId}")
    public void remove(@PathVariable Long courseId) {
        service.removeFavorite(courseId);
    }
}