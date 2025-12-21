package com.example.garbage.entity;

import java.util.Date;

public class RecognitionHistory {
    private Long id;
    private String imagePath;
    private String originalFilename;
    private String recognitionResult; // JSON格式的识别结果
    private Date recognitionTime;
    private String ipAddress;
    private String userAgent;

    // 构造方法
    public RecognitionHistory() {}

    public RecognitionHistory(String imagePath, String originalFilename, String recognitionResult) {
        this.imagePath = imagePath;
        this.originalFilename = originalFilename;
        this.recognitionResult = recognitionResult;
        this.recognitionTime = new Date();
    }

    // getter和setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public String getOriginalFilename() { return originalFilename; }
    public void setOriginalFilename(String originalFilename) { this.originalFilename = originalFilename; }

    public String getRecognitionResult() { return recognitionResult; }
    public void setRecognitionResult(String recognitionResult) { this.recognitionResult = recognitionResult; }

    public Date getRecognitionTime() { return recognitionTime; }
    public void setRecognitionTime(Date recognitionTime) { this.recognitionTime = recognitionTime; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }
}