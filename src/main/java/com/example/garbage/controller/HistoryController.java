package com.example.garbage.controller;

import com.example.garbage.entity.RecognitionHistory;
import com.example.garbage.service.RecognitionHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/history")
public class HistoryController {

    @Autowired
    private RecognitionHistoryService historyService;

    @GetMapping("/list")
    public String list(Model model) {
        List<RecognitionHistory> historyList = historyService.getAllHistory();
        int totalCount = historyService.getTotalRecognitionCount();
        int todayCount = historyService.getTodayHistory().size();

        model.addAttribute("historyList", historyList);
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("todayCount", todayCount);

        return "history";
    }
    @PostMapping("/delete/{id}")
    public String deleteHistory(@PathVariable Long id) {
        historyService.deleteHistory(id);
        return "redirect:/history/list"; // 删除后重定向到历史记录列表
    }



}