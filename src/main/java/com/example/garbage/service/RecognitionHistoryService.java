package com.example.garbage.service;

import com.example.garbage.entity.RecognitionHistory;
import java.util.List;

public interface RecognitionHistoryService {
    void saveRecognitionHistory(RecognitionHistory history);
    List<RecognitionHistory> getRecentHistory(int limit);
    List<RecognitionHistory> getAllHistory();
    int getTotalRecognitionCount();
    List<RecognitionHistory> getTodayHistory();
    void deleteHistory(Long id);
}