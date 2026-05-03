package com.ctbc.assignment2.controller.rest;

import com.ctbc.assignment2.bean.Chapter;
import com.ctbc.assignment2.bean.CourseBean;
import com.ctbc.assignment2.controller.rest.dto.ChapterDto;
import com.ctbc.assignment2.service.ChapterService;
import com.ctbc.assignment2.service.CourseBeanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chapters")
public class ChapterRestController {

    @Autowired
    private ChapterService chapterService;

    @Autowired
    private CourseBeanService courseService;

    @GetMapping("/course/{courseId}")
    public List<ChapterDto> getByCourse(@PathVariable Long courseId) {
        return chapterService.findByCourse(courseId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ChapterDto getById(@PathVariable Long id) {
        return toDto(chapterService.findById(id));
    }

    @PostMapping
    public ChapterDto save(@RequestBody ChapterDto dto) {
        return toDto(chapterService.save(fromDto(dto)));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        chapterService.delete(id);
    }

    private ChapterDto toDto(Chapter chapter) {
        ChapterDto dto = new ChapterDto();
        dto.setId(chapter.getId());
        dto.setCourseId(chapter.getCourse().getId());
        dto.setTitle(chapter.getTitle());
        dto.setDescription(chapter.getDescription());
        dto.setSortOrder(chapter.getSortOrder());
        dto.setVideoPlaceholderUrl(chapter.getVideoPlaceholderUrl());
        return dto;
    }

    private Chapter fromDto(ChapterDto dto) {
        Chapter chapter = new Chapter();
        if (dto.getId() != null) chapter.setId(dto.getId());
        chapter.setTitle(dto.getTitle());
        chapter.setDescription(dto.getDescription());
        chapter.setSortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0);
        chapter.setVideoPlaceholderUrl(dto.getVideoPlaceholderUrl());
        if (dto.getCourseId() != null) {
            CourseBean course = courseService.findById(dto.getCourseId());
            chapter.setCourse(course);
        }
        return chapter;
    }
}
