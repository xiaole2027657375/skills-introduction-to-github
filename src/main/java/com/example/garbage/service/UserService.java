package com.example.garbage.service;

import com.example.garbage.entity.User;

public interface UserService {
    // 根据用户名查询用户
    User findByUsername(String username);

    // 保存用户（注册）
    void save(User user);
}