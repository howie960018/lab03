package com.ctbc.assignment2.controller.rest.dto;

public class AdminStatsDto {
    private long courseCount;
    private long categoryCount;
    private long totalEnrollments;
    private long userCount;

    public long getCourseCount() { return courseCount; }
    public void setCourseCount(long courseCount) { this.courseCount = courseCount; }

    public long getCategoryCount() { return categoryCount; }
    public void setCategoryCount(long categoryCount) { this.categoryCount = categoryCount; }

    public long getTotalEnrollments() { return totalEnrollments; }
    public void setTotalEnrollments(long totalEnrollments) { this.totalEnrollments = totalEnrollments; }

    public long getUserCount() { return userCount; }
    public void setUserCount(long userCount) { this.userCount = userCount; }
}
