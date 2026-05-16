package com.ctbc.assignment2.service;

import com.ctbc.assignment2.bean.AppUser;
import com.ctbc.assignment2.bean.CourseBean;
import com.ctbc.assignment2.bean.CourseFavorite;
import com.ctbc.assignment2.exception.ResourceNotFoundException;
import com.ctbc.assignment2.repository.CourseBeanRepository;
import com.ctbc.assignment2.repository.CourseFavoriteRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CourseFavoriteService {

    @Autowired
    private CourseFavoriteRepository repo;

    @Autowired
    private CourseBeanRepository courseRepo; // ✅✅ 新增這個

    @Autowired
    private AppUserService userService;

    public void addFavorite(Long courseId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        AppUser user = userService.findByUsername(auth.getName());

        // ✅ 防重複
        if (repo.existsByUserIdAndCourseId(user.getId(), courseId)) {
            return;
        }

        CourseFavorite fav = new CourseFavorite();
        fav.setUser(user);

        // ✅✅ 正確：用 courseRepo 查
        CourseBean course = courseRepo.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        fav.setCourse(course);
        repo.save(fav);
    }

    @Transactional
    public void removeFavorite(Long courseId) {
        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
        repo.deleteByUserUsernameAndCourseId(username, courseId);
    }

    public List<CourseBean> myFavorites() {
        return repo.findByUserUsername(
                SecurityContextHolder.getContext().getAuthentication().getName()
        ).stream().map(CourseFavorite::getCourse).toList();
    }
}