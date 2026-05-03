package com.ctbc.assignment2.controller.rest.dto;

public class OrderItemDto {
    private Long id;
    private Long courseId;
    private String courseName;
    private Double price;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
}
