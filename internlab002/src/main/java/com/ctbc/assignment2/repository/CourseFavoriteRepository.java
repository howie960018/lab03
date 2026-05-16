package com.ctbc.assignment2.repository;

import com.ctbc.assignment2.bean.CourseFavorite;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface CourseFavoriteRepository
        extends JpaRepository<CourseFavorite, Long> {

    boolean existsByUserIdAndCourseId(Long userId, Long courseId);

    List<CourseFavorite> findByUserUsername(String username);

    @Modifying
    @Transactional
    @Query("DELETE FROM CourseFavorite f WHERE f.course.id = :courseId")
    void deleteByCourseId(@Param("courseId") Long courseId);

    void deleteByUserUsernameAndCourseId(String username, Long courseId);
}