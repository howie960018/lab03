package com.ctbc.assignment2.runner;

import com.ctbc.assignment2.bean.AppUser;
import com.ctbc.assignment2.bean.CourseBean;
import com.ctbc.assignment2.bean.CourseCategoryBean;
import com.ctbc.assignment2.repository.AppUserRepository;
import com.ctbc.assignment2.repository.CourseBeanRepository;
import com.ctbc.assignment2.repository.CourseCategoryBeanRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Component
@Profile("!test")
@Order(1)
public class SampleDataRunnable implements CommandLineRunner {

    @Autowired
    private CourseBeanRepository courseRepo;

    @Autowired
    private CourseCategoryBeanRepository categoryRepo;

    @Autowired
    private AppUserRepository userRepo;

    @Override
    public void run(String... args) {

        System.out.println("========== 建立 100 筆課程（含 instructor）==========");

        List<CourseCategoryBean> categories = categoryRepo.findAll();
        List<AppUser> instructors = userRepo.findAll()
                .stream()
                .filter(u -> u.getRole().equals("INSTRUCTOR"))
                .toList();

        Random random = new Random();

        if (categories.isEmpty()) {
            System.out.println("⚠️ 尚無分類，略過課程建立");
            return;
        }

        if (instructors.isEmpty()) {
            System.out.println("⚠️ 尚無 instructor，略過課程建立");
            return;
        }

        int totalCourses = 100;
        int index = 0;

        for (int i = 0; i < totalCourses; i++) {
            index++;

            CourseBean course = new CourseBean();

            // ✅ 隨機分類
            CourseCategoryBean category =
                    categories.get(random.nextInt(categories.size()));

            // ✅ 隨機 instructor（核心）
            AppUser instructor =
                    instructors.get(random.nextInt(instructors.size()));

            course.setCategory(category);
            course.setInstructor(instructor);
            course.setCourseName("課程 #" + index + " - " + category.getCategoryName());
            course.setPrice(1000.0 + random.nextInt(5000));
            course.setCourseSummary("這是一門關於 " + category.getCategoryName() + " 的課程");
            course.setImageUrl("https://picsum.photos/seed/course-" + index + "/600/300");

            courseRepo.save(course);
        }

        System.out.println("✅ 課程建立完成，共 100 筆（已分配 instructor）");
    }
}