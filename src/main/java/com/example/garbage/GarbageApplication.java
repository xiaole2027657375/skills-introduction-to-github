package com.example.garbage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GarbageApplication {

    public static void main(String[] args) {
        SpringApplication.run(GarbageApplication.class, args);
        System.out.println("=========================================");
        System.out.println("🚀 垃圾分类图像识别系统启动成功!");
        System.out.println("📍 访问地址: http://localhost:8080/garbage/");
        System.out.println("=========================================");
    }
}