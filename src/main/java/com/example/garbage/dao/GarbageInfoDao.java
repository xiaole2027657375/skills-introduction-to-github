package com.example.garbage.dao;

import com.example.garbage.entity.GarbageInfo;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface GarbageInfoDao {


    @Select("SELECT * FROM garbage_knowledge WHERE keyword LIKE CONCAT('%', #{keyword}, '%')")
    List<GarbageInfo> findByKeyword(String keyword);

    @Select("SELECT * FROM garbage_knowledge WHERE keyword = #{keyword}")
    GarbageInfo findExactMatch(String keyword);

    @Select("SELECT * FROM garbage_knowledge WHERE category = #{category}")
    List<GarbageInfo> findByCategory(String category);

    @Insert("INSERT INTO garbage_knowledge(keyword, category, description) " +
            "VALUES(#{keyword}, #{category}, #{description})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(GarbageInfo garbageInfo);
}