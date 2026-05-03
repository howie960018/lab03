package com.ctbc.assignment2.controller.rest.dto;

public class ChapterDto {
    private Long id;
    private Long courseId;
    private String title;
    private String description;
    private Integer sortOrder;
    private String videoPlaceholderUrl;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }

    public String getVideoPlaceholderUrl() { return videoPlaceholderUrl; }
    public void setVideoPlaceholderUrl(String videoPlaceholderUrl) { this.videoPlaceholderUrl = videoPlaceholderUrl; }
}
