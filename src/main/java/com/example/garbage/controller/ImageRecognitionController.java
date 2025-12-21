package com.example.garbage.controller;

import com.example.garbage.entity.RecognitionHistory;
import com.example.garbage.entity.RecognitionResult;
import com.example.garbage.service.RecognitionHistoryService;
import com.example.garbage.service.impl.BaiduAiService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/")
public class ImageRecognitionController {

    @Autowired
    private BaiduAiService baiduAiService;

    @Autowired
    private RecognitionHistoryService historyService;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${file.upload-dir:uploads/}")
    private String uploadDir;

    @GetMapping("/")
    public String index(Model model) {
        int todayCount = 0;
        int totalCount = 0;
        try {
            // 获取所有历史记录
            List<RecognitionHistory> allHistory = historyService.getAllHistory();
            totalCount = allHistory.size();

            // 计算今日识别次数
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String today = sdf.format(new Date());

            for (RecognitionHistory history : allHistory) {
                if (history.getRecognitionTime() != null) {
                    String historyDate = sdf.format(history.getRecognitionTime());
                    if (today.equals(historyDate)) {
                        todayCount++;
                    }
                }
            }

            System.out.println("今日识别次数: " + todayCount + ", 总次数: " + totalCount);

            // 调试信息
            if (allHistory.isEmpty()) {
                System.out.println("没有历史记录");
            } else {
                System.out.println("第一条记录时间: " + allHistory.get(0).getRecognitionTime());
                System.out.println("最后一条记录时间: " + allHistory.get(allHistory.size()-1).getRecognitionTime());
            }

        } catch (Exception e) {
            System.err.println("加载统计数据失败: " + e.getMessage());
            e.printStackTrace();
        }
        model.addAttribute("todayCount", todayCount);
        model.addAttribute("totalCount", totalCount);
        return "index";
    }


    @PostMapping("/recognize")
    public String recognize(@RequestParam("file") MultipartFile file,
                            HttpServletRequest request,
                            Model model) {
        if (file.isEmpty()) {
            model.addAttribute("error", "请选择要上传的图片");
            loadStatistics(model); // 错误时也要加载统计
            return "index";
        }

        try {
            // 在 Controller 层立即读取文件数据
            byte[] fileData;
            try (InputStream inputStream = file.getInputStream();
                 ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {

                byte[] data = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, bytesRead);
                }
                fileData = buffer.toByteArray();
            }

            System.out.println("Controller层读取文件成功，大小: " + fileData.length + " bytes");

            // 保存上传的文件
            String fileName = saveFile(file);
            String imageUrl = "/garbage/uploads/" + fileName;

            System.out.println("开始调用百度AI识别...");

            // 调用百度AI服务进行识别，传递 byte[] 数据
            RecognitionResult result = baiduAiService.recognizeGarbage(fileData, file.getOriginalFilename());
            result.setImagePath(imageUrl);

            System.out.println("识别结果: " + (result.isSuccess() ? "成功" : "失败"));
            if (result.hasResults()) {
                System.out.println("识别到物品数量: " + result.getItems().size());
            }

            // 保存识别历史记录
            saveRecognitionHistory(file, fileName, result, request);

            model.addAttribute("result", result);
            model.addAttribute("imageUrl", imageUrl);

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "识别失败: " + e.getMessage());
        }

        // 识别完成后重新加载统计数据
        loadStatistics(model);
        return "index";
    }

    // 提取统计加载逻辑到单独方法
    private void loadStatistics(Model model) {
        int todayCount = 0;
        int totalCount = 0;
        try {
            List<RecognitionHistory> allHistory = historyService.getAllHistory();
            totalCount = allHistory.size();

            // 计算今日识别次数
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String today = sdf.format(new Date());

            for (RecognitionHistory history : allHistory) {
                if (history.getRecognitionTime() != null) {
                    String historyDate = sdf.format(history.getRecognitionTime());
                    if (today.equals(historyDate)) {
                        todayCount++;
                    }
                }
            }

            System.out.println("统计更新 - 今日识别: " + todayCount + ", 总次数: " + totalCount);

        } catch (Exception e) {
            System.err.println("加载统计数据失败: " + e.getMessage());
        }
        model.addAttribute("todayCount", todayCount);
        model.addAttribute("totalCount", totalCount);
    }
    private String saveFile(MultipartFile file) throws IOException {
        // 获取项目根目录的绝对路径
        String projectRoot = System.getProperty("user.dir");
        File uploadPath = new File(projectRoot, uploadDir);

        System.out.println("上传路径: " + uploadPath.getAbsolutePath());

        // 确保上传目录存在
        if (!uploadPath.exists()) {
            boolean created = uploadPath.mkdirs();
            if (!created) {
                throw new IOException("无法创建上传目录: " + uploadPath.getAbsolutePath());
            }
            System.out.println("创建上传目录: " + uploadPath.getAbsolutePath());
        }

        String originalFileName = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFileName != null && originalFileName.contains(".")) {
            fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }

        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String fileName = timeStamp + fileExtension;

        // 保存文件
        File dest = new File(uploadPath, fileName);
        file.transferTo(dest);

        System.out.println("文件保存成功: " + dest.getAbsolutePath());
        return fileName;
    }

    private void saveRecognitionHistory(MultipartFile file, String fileName,
                                        RecognitionResult result, HttpServletRequest request) {
        try {
            RecognitionHistory history = new RecognitionHistory();
            history.setImagePath("/uploads/" + fileName);
            history.setOriginalFilename(file.getOriginalFilename());
            history.setRecognitionResult(objectMapper.writeValueAsString(result));
            history.setRecognitionTime(new Date()); // 确保设置时间
            history.setIpAddress(getClientIpAddress(request));
            history.setUserAgent(request.getHeader("User-Agent"));

            historyService.saveRecognitionHistory(history);
            System.out.println("历史记录保存成功，时间: " + history.getRecognitionTime());
        } catch (Exception e) {
            System.err.println("保存历史记录失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0];
        }
        return request.getRemoteAddr();
    }


}