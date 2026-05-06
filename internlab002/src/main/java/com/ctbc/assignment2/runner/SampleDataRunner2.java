package com.ctbc.assignment2.runner;


import com.ctbc.assignment2.bean.CourseCategoryBean;
import com.ctbc.assignment2.repository.CourseCategoryBeanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@Profile("!test")
@Order(0) // 確保先建立類別，再建立課程
public class SampleDataRunner2 implements CommandLineRunner {

    @Autowired
    private CourseCategoryBeanRepository categoryRepo;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("========== 建立課程類別 ==========");

        List<String> categories = Arrays.asList(
                "程式設計", "資料庫", "網頁開發", "行動開發", "雲端運算",
                "DevOps", "資安", "人工智慧", "大數據", "機器學習",
                "系統設計", "軟體工程", "測試工程", "UI/UX",
                "遊戲開發", "嵌入式系統", "區塊鏈", "物聯網",
                "金融科技", "數據分析"
        );

        for (String name : categories) {
            CourseCategoryBean cat = new CourseCategoryBean();
            cat.setCategoryName(name);
            categoryRepo.save(cat);
        }

        System.out.println("類別總數：" + categoryRepo.count());
    }
}