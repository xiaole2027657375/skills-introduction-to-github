package com.example.garbage.service.impl;

import com.example.garbage.dao.RecognitionHistoryDao;
import com.example.garbage.entity.RecognitionHistory;
import com.example.garbage.service.RecognitionHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecognitionHistoryServiceImpl implements RecognitionHistoryService {

    @Autowired
    private RecognitionHistoryDao recognitionHistoryDao;

    @Override
    public void saveRecognitionHistory(RecognitionHistory history) {
        recognitionHistoryDao.insert(history);
    }

    @Override
    public List<RecognitionHistory> getRecentHistory(int limit) {
        return recognitionHistoryDao.findRecentHistory(limit);
    }

    @Override
    public List<RecognitionHistory> getAllHistory() {
        return recognitionHistoryDao.findAll();
    }

    @Override
    public int getTotalRecognitionCount() {
        return recognitionHistoryDao.getTotalCount();
    }

    @Override
    public List<RecognitionHistory> getTodayHistory() {
        return recognitionHistoryDao.findTodayHistory();
    }

    @Override
    public void deleteHistory(Long id) {
        recognitionHistoryDao.deleteById(id);
    }
}