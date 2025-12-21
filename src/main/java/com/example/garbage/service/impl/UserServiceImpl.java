package com.example.garbage.service.impl;

import com.example.garbage.dao.UserDao;
import com.example.garbage.entity.User;
import com.example.garbage.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Override
    public User findByUsername(String username) {
        return userDao.findByUsername(username);
    }

    @Override
    public void save(User user) {
        userDao.insert(user);
    }
}