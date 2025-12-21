package com.example.garbage.service.impl;

import com.baidu.aip.imageclassify.AipImageClassify;
import com.example.garbage.dao.GarbageInfoDao;
import com.example.garbage.entity.GarbageInfo;
import com.example.garbage.entity.RecognitionResult;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class BaiduAiService {

    @Autowired
    private AipImageClassify client;

    @Autowired
    private GarbageInfoDao garbageInfoDao;

    /**
     * 调用百度AI通用物体识别进行垃圾分类
     */
    public RecognitionResult recognizeGarbage(MultipartFile file) {
        RecognitionResult result = new RecognitionResult();

        try {
            System.out.println("=== 开始百度AI识别 ===");
            System.out.println("文件名称: " + file.getOriginalFilename());
            System.out.println("文件大小: " + file.getSize() + " bytes");

            // 检查客户端是否正常
            if (client == null) {
                System.err.println("错误: AipImageClassify client 为 null");
                result.setSuccess(false);
                result.setMessage("AI服务未正确初始化");
                return result;
            }
            System.out.println("百度AI客户端状态: 正常");

            // 使用兼容 Java 8 的方式读取文件
            byte[] imageData;
            try (InputStream inputStream = file.getInputStream();
                 ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {

                byte[] data = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, bytesRead);
                }
                imageData = buffer.toByteArray();
            }

            System.out.println("图片数据读取成功，大小: " + imageData.length + " bytes");

            // 调用百度AI通用物体识别接口
            System.out.println("调用百度AI advancedGeneral 接口...");
            JSONObject response = client.advancedGeneral(imageData, new HashMap<>());

            System.out.println("百度AI响应: " + response.toString());

            // 解析响应结果
            if (response.has("error_code")) {
                int errorCode = response.getInt("error_code");
                String errorMsg = response.getString("error_msg");
                System.err.println("百度AI识别失败 - 错误码: " + errorCode + ", 错误信息: " + errorMsg);

                result.setSuccess(false);
                result.setMessage("百度AI识别失败: " + errorMsg + " (错误码: " + errorCode + ")");
                return result;
            }

            if (response.has("result")) {
                JSONArray items = response.getJSONArray("result");
                System.out.println("识别到 " + items.length() + " 个物品");

                List<RecognitionResult.GarbageItem> garbageItems = new ArrayList<>();

                for (int i = 0; i < items.length(); i++) {
                    JSONObject item = items.getJSONObject(i);
                    String keyword = item.getString("keyword");
                    double score = item.getDouble("score");
                    String root = item.optString("root", "");

                    System.out.println("物品 " + (i+1) + ": " + keyword + " (置信度: " + score + ")");

                    // 只处理置信度高于0.1的结果
                    if (score < 0.1) {
                        System.out.println("跳过低置信度物品: " + keyword);
                        continue;
                    }

                    RecognitionResult.GarbageItem garbageItem = new RecognitionResult.GarbageItem();
                    garbageItem.setKeyword(keyword);
                    garbageItem.setScore(score);

                    // 从数据库查询垃圾分类信息
                    GarbageInfo garbageInfo = garbageInfoDao.findExactMatch(keyword);
                    if (garbageInfo != null) {
                        System.out.println("数据库匹配: " + keyword + " -> " + garbageInfo.getCategory());
                        garbageItem.setCategory(garbageInfo.getCategory());
                        garbageItem.setSuggestion(garbageInfo.getDescription());
                    } else {
                        // 如果没有精确匹配，使用智能分类
                        String category = classifyGarbage(keyword, root);
                        System.out.println("智能分类: " + keyword + " -> " + category);
                        garbageItem.setCategory(category);
                        garbageItem.setSuggestion(getSuggestion(category));
                    }

                    garbageItems.add(garbageItem);
                }

                if (garbageItems.isEmpty()) {
                    System.out.println("没有有效的识别结果");
                    result.setSuccess(false);
                    result.setMessage("未识别到有效的物品，请尝试上传更清晰的图片");
                } else {
                    System.out.println("识别成功，有效物品数量: " + garbageItems.size());
                    result.setSuccess(true);
                    result.setMessage("识别成功，共识别到 " + garbageItems.size() + " 个物品");
                    result.setItems(garbageItems);
                }
            } else {
                System.out.println("API响应中没有 result 字段");
                result.setSuccess(false);
                result.setMessage("未识别到任何物品");
            }

        } catch (IOException e) {
            System.err.println("文件处理异常: " + e.getMessage());
            e.printStackTrace();
            result.setSuccess(false);
            result.setMessage("文件处理失败: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("识别服务异常: " + e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace();
            result.setSuccess(false);
            result.setMessage("识别服务异常: " + e.getMessage());
        }

        System.out.println("=== 百度AI识别结束 ===");
        return result;
    }



    /**
     * 使用 byte[] 数据进行识别（避免 MultipartFile 临时文件问题）
     */
    public RecognitionResult recognizeGarbage(byte[] imageData, String filename) {
        RecognitionResult result = new RecognitionResult();

        try {
            System.out.println("=== 开始百度AI识别 ===");
            System.out.println("文件名称: " + filename);
            System.out.println("图片数据大小: " + imageData.length + " bytes");

            // 检查客户端是否正常
            if (client == null) {
                System.err.println("错误: AipImageClassify client 为 null");
                result.setSuccess(false);
                result.setMessage("AI服务未正确初始化");
                return result;
            }
            System.out.println("百度AI客户端状态: 正常");

            // 调用百度AI通用物体识别接口
            System.out.println("调用百度AI advancedGeneral 接口...");
            JSONObject response = client.advancedGeneral(imageData, new HashMap<>());

            System.out.println("百度AI响应: " + response.toString());

            // 解析响应结果
            if (response.has("error_code")) {
                int errorCode = response.getInt("error_code");
                String errorMsg = response.getString("error_msg");
                System.err.println("百度AI识别失败 - 错误码: " + errorCode + ", 错误信息: " + errorMsg);

                result.setSuccess(false);
                result.setMessage("百度AI识别失败: " + errorMsg + " (错误码: " + errorCode + ")");
                return result;
            }

            if (response.has("result")) {
                JSONArray items = response.getJSONArray("result");
                System.out.println("识别到 " + items.length() + " 个物品");

                List<RecognitionResult.GarbageItem> garbageItems = new ArrayList<>();

                for (int i = 0; i < items.length(); i++) {
                    JSONObject item = items.getJSONObject(i);
                    String keyword = item.getString("keyword");
                    double score = item.getDouble("score");
                    String root = item.optString("root", "");

                    System.out.println("物品 " + (i+1) + ": " + keyword + " (置信度: " + score + ")");

                    // 只处理置信度高于0.1的结果
                    if (score < 0.1) {
                        System.out.println("跳过低置信度物品: " + keyword);
                        continue;
                    }

                    RecognitionResult.GarbageItem garbageItem = new RecognitionResult.GarbageItem();
                    garbageItem.setKeyword(keyword);
                    garbageItem.setScore(score);

                    // 从数据库查询垃圾分类信息
                    GarbageInfo garbageInfo = garbageInfoDao.findExactMatch(keyword);
                    if (garbageInfo != null) {
                        System.out.println("数据库匹配: " + keyword + " -> " + garbageInfo.getCategory());
                        garbageItem.setCategory(garbageInfo.getCategory());
                        garbageItem.setSuggestion(garbageInfo.getDescription());
                    } else {
                        // 如果没有精确匹配，使用智能分类
                        String category = classifyGarbage(keyword, root);
                        System.out.println("智能分类: " + keyword + " -> " + category);
                        garbageItem.setCategory(category);
                        garbageItem.setSuggestion(getSuggestion(category));
                    }

                    garbageItems.add(garbageItem);
                }

                if (garbageItems.isEmpty()) {
                    System.out.println("没有有效的识别结果");
                    result.setSuccess(false);
                    result.setMessage("未识别到有效的物品，请尝试上传更清晰的图片");
                } else {
                    System.out.println("识别成功，有效物品数量: " + garbageItems.size());
                    result.setSuccess(true);
                    result.setMessage("识别成功，共识别到 " + garbageItems.size() + " 个物品");
                    result.setItems(garbageItems);
                }
            } else {
                System.out.println("API响应中没有 result 字段");
                result.setSuccess(false);
                result.setMessage("未识别到任何物品");
            }

        } catch (Exception e) {
            System.err.println("识别服务异常: " + e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace();
            result.setSuccess(false);
            result.setMessage("识别服务异常: " + e.getMessage());
        }

        System.out.println("=== 百度AI识别结束 ===");
        return result;
    }
    /**
     * 检查文件是否为图片
     */
    private boolean isImageFile(String filename) {
        if (filename == null) {
            return false;
        }
        String lowerCaseFilename = filename.toLowerCase();
        return lowerCaseFilename.endsWith(".jpg") ||
                lowerCaseFilename.endsWith(".jpeg") ||
                lowerCaseFilename.endsWith(".png") ||
                lowerCaseFilename.endsWith(".gif") ||
                lowerCaseFilename.endsWith(".bmp");
    }

    /**
     * 智能垃圾分类逻辑
     */
    private String classifyGarbage(String keyword, String root) {
        // 转换为小写进行匹配
        String lowerKeyword = keyword.toLowerCase();
        String lowerRoot = root.toLowerCase();

        // 可回收垃圾关键词
        String[] recyclableKeywords = {
                "塑料", "瓶", "纸", "玻璃", "金属", "布料", "纸张", "纸板",
                "易拉罐", "饮料瓶", "书本", "报纸", "杂志", "包装盒",
                "泡沫", "橡胶", "皮革", "电脑", "手机", "电视机"
        };

        // 有害垃圾关键词
        String[] hazardousKeywords = {
                "电池", "药品", "灯管", "油漆", "化学品", "杀虫剂",
                "温度计", "血压计", "荧光灯", "消毒剂", "农药",
                "蓄电池", "纽扣电池", "油漆桶"
        };

        // 厨余垃圾关键词
        String[] kitchenKeywords = {
                "食物", "果皮", "蔬菜", "剩饭", "骨头", "茶叶",
                "咖啡渣", "蛋壳", "果核", "米饭", "面包", "蛋糕",
                "肉类", "鱼", "虾", "水果", "瓜子", "花生"
        };

        // 其他垃圾关键词
        String[] otherKeywords = {
                "卫生纸", "塑料袋", "餐巾纸", "尿不湿", "陶瓷",
                "烟头", "尘土", "一次性餐具", "污损塑料",
                "尼龙", "编织袋", "大骨头", "硬贝壳"
        };

        // 优先检查有害垃圾（安全性最重要）
        for (String k : hazardousKeywords) {
            if (lowerKeyword.contains(k) || lowerRoot.contains(k)) {
                return "有害垃圾";
            }
        }

        // 检查厨余垃圾
        for (String k : kitchenKeywords) {
            if (lowerKeyword.contains(k) || lowerRoot.contains(k)) {
                return "厨余垃圾";
            }
        }

        // 检查可回收垃圾
        for (String k : recyclableKeywords) {
            if (lowerKeyword.contains(k) || lowerRoot.contains(k)) {
                return "可回收垃圾";
            }
        }

        // 检查其他垃圾
        for (String k : otherKeywords) {
            if (lowerKeyword.contains(k) || lowerRoot.contains(k)) {
                return "其他垃圾";
            }
        }

        // 根据常见物品分类
        if (lowerKeyword.contains("包装") || lowerKeyword.contains("容器")) {
            return "可回收垃圾";
        } else if (lowerKeyword.contains("电子") || lowerKeyword.contains("电器")) {
            return "可回收垃圾";
        } else if (lowerKeyword.contains("植物") || lowerKeyword.contains("花卉")) {
            return "厨余垃圾";
        } else if (lowerKeyword.contains("建筑") || lowerKeyword.contains("装修")) {
            return "其他垃圾";
        }

        // 默认分类
        return "其他垃圾";
    }

    /**
     * 根据分类返回处理建议
     */
    private String getSuggestion(String category) {
        switch(category) {
            case "可回收垃圾":
                return "清洗干净、压扁后投入可回收物桶，保持干燥清洁，避免污染";
            case "有害垃圾":
                return "轻拿轻放，如有破损请妥善包裹，投入有害垃圾专用回收箱";
            case "厨余垃圾":
                return "沥干水分，去除包装物后投入厨余垃圾桶，提倡使用可降解垃圾袋";
            case "其他垃圾":
                return "投入其他垃圾桶，难以辨识类别的生活垃圾投入此桶";
            default:
                return "请咨询当地垃圾分类标准，或使用垃圾分类小程序查询";
        }
    }

    /**
     * 测试百度AI连接（可选方法）
     */
    public boolean testConnection() {
        try {
            // 发送一个简单的测试请求
            JSONObject response = client.advancedGeneral(new byte[0], new HashMap<>());
            return response.has("error_code") || response.has("result");
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取识别统计信息（可选方法）
     */
    public JSONObject getRecognitionStats() {
        try {
            HashMap<String, String> options = new HashMap<>();
            options.put("top_num", "10"); // 返回前10个结果
            options.put("baike_num", "0"); // 不返回百科信息

            JSONObject response = client.advancedGeneral(new byte[0], options);
            return response;
        } catch (Exception e) {
            return new JSONObject().put("error", e.getMessage());
        }
    }
}