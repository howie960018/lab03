package com.ctbc.assignment2.controller.rest;

import com.ctbc.assignment2.bean.CourseReview;
import com.ctbc.assignment2.controller.rest.dto.ReviewDto;
import com.ctbc.assignment2.repository.CourseReviewRepository;
import com.ctbc.assignment2.service.CourseReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reviews")
public class ReviewRestController {

    @Autowired
    private CourseReviewService reviewService;

    @Autowired
    private CourseReviewRepository reviewRepository;

    @GetMapping("/course/{courseId}")
    public List<ReviewDto> getByCourse(@PathVariable Long courseId) {
        return reviewService.findByCourse(courseId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/all")
    public List<ReviewDto> getAll() {
        return reviewRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @PostMapping("/course/{courseId}")
    public ReviewDto submit(@PathVariable Long courseId,
                            @RequestBody Map<String, Object> body,
                            Principal principal) {
        Integer rating = (Integer) body.get("rating");
        String comment = (String) body.get("comment");
        return toDto(reviewService.submit(principal.getName(), courseId, rating, comment));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        reviewService.delete(id);
    }

    private ReviewDto toDto(CourseReview review) {
        ReviewDto dto = new ReviewDto();
        dto.setId(review.getId());
        dto.setCourseId(review.getCourse().getId());
        dto.setCourseName(review.getCourse().getCourseName());
        dto.setReviewerUsername(review.getReviewer().getUsername());
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        dto.setCreatedAt(review.getCreatedAt());
        return dto;
    }
}
