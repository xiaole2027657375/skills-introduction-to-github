package com.example.garbage.dao;

import com.example.garbage.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserDao {

    // 根据用户名查询用户
    @Select("select * from users where username = #{username}")
    User findByUsername(String username);

    // 插入新用户（自动生成主键）
    @Insert("insert into users(username, password) values(#{username}, #{password})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(User user);
}