package com.ctbc.assignment2.controller.rest;

import com.ctbc.assignment2.controller.rest.dto.AdminStatsDto;
import com.ctbc.assignment2.repository.AppUserRepository;
import com.ctbc.assignment2.repository.EnrollmentRepository;
import com.ctbc.assignment2.service.CourseBeanService;
import com.ctbc.assignment2.service.CourseCategoryBeanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminStatsRestController {

    @Autowired
    private CourseBeanService courseService;

    @Autowired
    private CourseCategoryBeanService categoryService;

    @Autowired(required = false)
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private AppUserRepository userRepository;

    @GetMapping("/stats")
    public AdminStatsDto getStats() {
        AdminStatsDto dto = new AdminStatsDto();
        dto.setCourseCount(courseService.count());
        dto.setCategoryCount(categoryService.count());
        dto.setUserCount(userRepository.count());
        if (enrollmentRepository != null) {
            dto.setTotalEnrollments(enrollmentRepository.count());
        }
        return dto;
    }
}
