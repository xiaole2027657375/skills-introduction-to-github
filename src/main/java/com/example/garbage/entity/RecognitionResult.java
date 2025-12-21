package com.example.garbage.entity;

import java.util.List;

public class RecognitionResult {
    private boolean success;
    private String message;
    private List<GarbageItem> items;
    private String imagePath;

    // 构造方法
    public RecognitionResult() {}

    public RecognitionResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    // 内部类 - 垃圾项
    public static class GarbageItem {
        private String keyword;
        private double score;
        private String category;
        private String suggestion;

        // 默认构造方法
        public GarbageItem() {}

        // 带参数构造方法
        public GarbageItem(String keyword, double score, String category, String suggestion) {
            this.keyword = keyword;
            this.score = score;
            this.category = category;
            this.suggestion = suggestion;
        }

        // getter和setter
        public String getKeyword() {
            return keyword;
        }
        public void setKeyword(String keyword) {
            this.keyword = keyword;
        }

        public double getScore() {
            return score;
        }
        public void setScore(double score) {
            this.score = score;
        }

        public String getCategory() {
            return category;
        }
        public void setCategory(String category) {
            this.category = category;
        }

        public String getSuggestion() {
            return suggestion;
        }
        public void setSuggestion(String suggestion) {
            this.suggestion = suggestion;
        }

        @Override
        public String toString() {
            return "GarbageItem{" +
                    "keyword='" + keyword + '\'' +
                    ", score=" + score +
                    ", category='" + category + '\'' +
                    ", suggestion='" + suggestion + '\'' +
                    '}';
        }
    }

    // 主类的getter和setter
    public boolean isSuccess() {
        return success;
    }
    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    public List<GarbageItem> getItems() {
        return items;
    }
    public void setItems(List<GarbageItem> items) {
        this.items = items;
    }

    public String getImagePath() {
        return imagePath;
    }
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    // 添加项目的方法
    public void addItem(GarbageItem item) {
        this.items.add(item);
    }

    // 判断是否有识别结果
    public boolean hasResults() {
        return items != null && !items.isEmpty();
    }

    // 获取最高置信度的结果
    public GarbageItem getTopResult() {
        if (!hasResults()) {
            return null;
        }
        return items.stream()
                .max((a, b) -> Double.compare(a.getScore(), b.getScore()))
                .orElse(null);
    }

    @Override
    public String toString() {
        return "RecognitionResult{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", items=" + items +
                ", imagePath='" + imagePath + '\'' +
                '}';
    }
}