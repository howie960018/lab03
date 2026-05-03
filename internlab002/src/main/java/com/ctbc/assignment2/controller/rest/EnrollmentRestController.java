package com.ctbc.assignment2.controller.rest;

import com.ctbc.assignment2.bean.Enrollment;
import com.ctbc.assignment2.controller.rest.dto.EnrollmentDto;
import com.ctbc.assignment2.service.EnrollmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentRestController {

    @Autowired
    private EnrollmentService enrollmentService;

    @GetMapping("/my")
    public List<EnrollmentDto> getMyEnrollments(Principal principal) {
        return enrollmentService.findByStudent(principal.getName()).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @PostMapping("/{courseId}")
    public EnrollmentDto enroll(@PathVariable Long courseId, Principal principal) {
        return toDto(enrollmentService.enroll(principal.getName(), courseId));
    }

    @DeleteMapping("/{courseId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancel(@PathVariable Long courseId, Principal principal) {
        enrollmentService.cancel(principal.getName(), courseId);
    }

    private EnrollmentDto toDto(Enrollment enrollment) {
        EnrollmentDto dto = new EnrollmentDto();
        dto.setId(enrollment.getId());
        dto.setCourseId(enrollment.getCourse().getId());
        dto.setCourseName(enrollment.getCourse().getCourseName());
        dto.setCoverImageUrl(enrollment.getCourse().getCoverImageUrl());
        dto.setPrice(enrollment.getCourse().getPrice());
        dto.setStatus(enrollment.getStatus());
        dto.setEnrolledAt(enrollment.getEnrolledAt());
        return dto;
    }
}
