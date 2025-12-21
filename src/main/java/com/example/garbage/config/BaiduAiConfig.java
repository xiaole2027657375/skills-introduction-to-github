package com.example.garbage.config;

import com.baidu.aip.imageclassify.AipImageClassify;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BaiduAiConfig {

    @Value("${baidu.ai.app-id}")
    private String appId;

    @Value("${baidu.ai.api-key}")
    private String apiKey;

    @Value("${baidu.ai.secret-key}")
    private String secretKey;

    @Bean
    public AipImageClassify aipImageClassify() {
        System.out.println("初始化百度AI客户端...");
        System.out.println("AppId: " + appId);
        System.out.println("ApiKey: " + apiKey);
        System.out.println("SecretKey: " + (secretKey != null ? "已设置" : "未设置"));

        AipImageClassify client = new AipImageClassify(appId, apiKey, secretKey);

        // 可选：设置网络参数
        client.setConnectionTimeoutInMillis(5000);
        client.setSocketTimeoutInMillis(60000);

        System.out.println("百度AI客户端初始化完成");
        return client;
    }
}