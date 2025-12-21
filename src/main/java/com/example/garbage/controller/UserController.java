package com.example.garbage.controller;

import com.example.garbage.entity.User;
import com.example.garbage.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    // 跳转到登录页
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // 跳转到注册页
    @GetMapping("/register")
    public String register() {
        return "register";
    }

    // 处理登录请求
    @PostMapping("/doLogin")
    public String doLogin(String username, String password, Model model, HttpSession session) {
        // 新增日志，确认方法被调用
        System.out.println("==== 接收到登录请求：username=" + username + "，password=" + password + " ====");

        User user = userService.findByUsername(username);
        if (user == null) {
            model.addAttribute("error", "用户名不存在");
            return "login";
        }
        if (!user.getPassword().equals(password)) {
            model.addAttribute("error", "密码错误");
            return "login";
        }
        session.setAttribute("user", user);
        return "redirect:/"; // 登录成功跳转到首页（/garbage/）
    }

    // 处理注册请求
    @PostMapping("/doRegister")
    public String doRegister(String username, String password, Model model) {
        // 1. 验证用户名是否已存在
        if (userService.findByUsername(username) != null) {
            model.addAttribute("error", "用户名已存在");
            return "register";
        }

        // 2. 保存新用户（明文存储密码）
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        userService.save(user);

        // 3. 注册成功，跳转到登录页
        return "redirect:/login";
    }

    // 退出登录
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // 清除session
        return "redirect:/login";
    }
}