package com.ctbc.assignment2.controller.rest.dto;

import java.util.List;

public class CheckoutRequest {
    private List<Long> courseIds;

    public List<Long> getCourseIds() { return courseIds; }
    public void setCourseIds(List<Long> courseIds) { this.courseIds = courseIds; }
}
