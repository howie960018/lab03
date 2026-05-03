package com.ctbc.assignment2.controller.rest;

import com.ctbc.assignment2.bean.CourseBean;
import com.ctbc.assignment2.service.CourseBeanService;
import com.ctbc.assignment2.service.EnrollmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/instructor")
public class InstructorRestController {

    @Autowired
    private CourseBeanService courseService;

    @Autowired(required = false)
    private EnrollmentService enrollmentService;

    @GetMapping("/courses")
    public List<CourseBean> getMyCourses(Principal principal) {
        return courseService.findByInstructorName(principal.getName());
    }

    @GetMapping("/stats")
    public java.util.Map<String, Object> getStats(Principal principal) {
        List<CourseBean> courses = courseService.findByInstructorName(principal.getName());
        long totalEnrollments = 0;
        if (enrollmentService != null) {
            for (CourseBean course : courses) {
                totalEnrollments += enrollmentService.countByCourse(course.getId());
            }
        }
        java.util.Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("courseCount", courses.size());
        stats.put("totalEnrollments", totalEnrollments);
        return stats;
    }

    @PostMapping("/courses")
    public CourseBean saveCourse(@RequestBody CourseBean course, Principal principal) {
        course.setInstructorName(principal.getName());
        return courseService.save(course);
    }
}
