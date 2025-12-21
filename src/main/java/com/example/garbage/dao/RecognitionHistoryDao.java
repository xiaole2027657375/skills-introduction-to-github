package com.example.garbage.dao;

import com.example.garbage.entity.RecognitionHistory;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface RecognitionHistoryDao {

    @Insert("INSERT INTO recognition_history(image_path, original_filename, recognition_result, " +
            "recognition_time, ip_address, user_agent) " +
            "VALUES(#{imagePath}, #{originalFilename}, #{recognitionResult}, " +
            "#{recognitionTime}, #{ipAddress}, #{userAgent})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(RecognitionHistory history);

    @Select("SELECT * FROM recognition_history ORDER BY recognition_time DESC LIMIT #{limit}")
    List<RecognitionHistory> findRecentHistory(int limit);

    @Select("SELECT * FROM recognition_history ORDER BY recognition_time DESC")
    List<RecognitionHistory> findAll();

    @Select("SELECT COUNT(*) FROM recognition_history")
    int getTotalCount();

    @Delete("DELETE FROM recognition_history WHERE id = #{id}")
    int deleteById(Long id);

    @Select("SELECT * FROM recognition_history WHERE DATE(recognition_time) = CURDATE()")
    List<RecognitionHistory> findTodayHistory();
}