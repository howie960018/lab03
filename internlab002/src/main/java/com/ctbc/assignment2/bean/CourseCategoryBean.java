package com.ctbc.assignment2.bean;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "course_category")
public class CourseCategoryBean {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "類別名稱不可以為空")
    @Column(nullable = false, unique = true, length = 100)
    private String categoryName;

    @JsonIgnore
    @OneToMany(mappedBy = "category")
    private List<CourseBean> courses;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = false)
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    // ── Lifecycle ─────────────────────────────────
    @PrePersist
    public void onCreate() {
        Date now = new Date();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = new Date();
    }

    // ── Constructors ──────────────────────────────
    public CourseCategoryBean() {}

    public CourseCategoryBean(Long id, String categoryName, List<CourseBean> courses,
                               Date createdAt, Date updatedAt) {
        this.id = id;
        this.categoryName = categoryName;
        this.courses = courses;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // ── Getters & Setters ─────────────────────────
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public List<CourseBean> getCourses() { return courses; }
    public void setCourses(List<CourseBean> courses) { this.courses = courses; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }

    // ── toString（排除 courses 避免循環）────────────
    @Override
    public String toString() {
        return "CourseCategoryBean{id=" + id + ", categoryName='" + categoryName + "'}";
    }

    // ── equals & hashCode（只用 id）──────────────────
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CourseCategoryBean)) return false;
        CourseCategoryBean that = (CourseCategoryBean) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}