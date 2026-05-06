package com.ctbc.assignment2.runner;
import com.ctbc.assignment2.bean.CourseBean;
import com.ctbc.assignment2.bean.CourseCategoryBean;
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

    @Override
    public void run(String... args) throws Exception {
        System.out.println("========== 建立 100 筆課程（每類別至少一筆） ==========");

        List<CourseCategoryBean> categories = categoryRepo.findAll();
        Random random = new Random();

        int targetCount = 100;
        int categoryCount = categories.size();

        if (categoryCount == 0) {
            System.out.println("⚠️ 尚無任何類別，略過課程建立");
            return;
        }

        // 計算每個類別最少分配幾門課
        int basePerCategory = targetCount / categoryCount;
        int remainder = targetCount % categoryCount;

        int courseIndex = 0;

        for (int i = 0; i < categories.size(); i++) {
            CourseCategoryBean category = categories.get(i);

            // 前 remainder 個類別多拿一門課程
            int coursePerThisCategory = basePerCategory + (i < remainder ? 1 : 0);

            for (int j = 1; j <= coursePerThisCategory; j++) {
                courseIndex++;

                CourseBean course = new CourseBean();
                course.setCategory(category);

                // ✅ 保證名稱唯一
                course.setCourseName(
                        category.getCategoryName() + " 精選課程 #" + courseIndex
                );

                // 隨機價格 2000 ~ 7000
                course.setPrice(
                        2000.0 + random.nextInt(5000)
                );

                // ✅ 所有課程都有 imageUrl (網址)
                course.setImageUrl(
                        "https://picsum.photos/seed/course-" + courseIndex + "/600/300"
                );

                courseRepo.save(course);
            }
        }

        System.out.println("✅ 課程建立完成，共：" + courseIndex + " 筆");
    }
}