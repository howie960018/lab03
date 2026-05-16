package com.ctbc.assignment2.bean;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

@Entity
@Table(name = "course")
public class CourseBean {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "課程名稱不可以為空")
    private String courseName;

    @NotNull(message = "價格不可以為空")
    @PositiveOrZero(message = "價格不可以負數")
    private Double price;

    @ManyToOne
    @JoinColumn(name = "instructor_id", nullable = false)
    private AppUser instructor;

    @Column(length = 1000)
    private String courseSummary;

    @Column(length = 500)
    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = true)
    private CourseCategoryBean category;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = false)
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    // ── Lifecycle ─────────────────────────────────
    @PrePersist
    public void onCreate() {
        createdAt = new Date();
        updatedAt = new Date();
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = new Date();
    }

    // ── Constructors ──────────────────────────────
    public CourseBean() {}

    public CourseBean(Long id, String courseName, Double price, AppUser instructor,
                      String courseSummary, String imageUrl, CourseCategoryBean category,
                      Date createdAt, Date updatedAt) {
        this.id = id;
        this.courseName = courseName;
        this.price = price;
        this.instructor = instructor;
        this.courseSummary = courseSummary;
        this.imageUrl = imageUrl;
        this.category = category;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // ── Getters & Setters ─────────────────────────
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public AppUser getInstructor() { return instructor; }
    public void setInstructor(AppUser instructor) { this.instructor = instructor; }

    public String getCourseSummary() { return courseSummary; }
    public void setCourseSummary(String courseSummary) { this.courseSummary = courseSummary; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public CourseCategoryBean getCategory() { return category; }
    public void setCategory(CourseCategoryBean category) { this.category = category; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }

    // ── toString（排除 category 避免循環）────────────
    @Override
    public String toString() {
        return "CourseBean{id=" + id + ", courseName='" + courseName + "', price=" + price + "}";
    }

    // ── equals & hashCode（只用 id）──────────────────
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CourseBean)) return false;
        CourseBean that = (CourseBean) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}