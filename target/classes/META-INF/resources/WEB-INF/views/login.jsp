<!-- 完整的登录页面代码 -->
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>登录</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css">
    <style>
        /* 登录页面专属样式 */
        body {
            background-color: #f0f2f5;
            display: flex;
            flex-direction: column;
            min-height: 100vh;
        }

        .login-container {
            width: 100%;
            max-width: 400px;
            margin: auto; /* 垂直水平居中 */
            padding: 30px;
            background: white;
            border-radius: 12px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
        }

        .login-container h2 {
            text-align: center;
            color: #2c3e50;
            margin-bottom: 25px;
            font-size: 1.8em;
        }

        .form-group {
            margin-bottom: 20px;
        }

        .form-group label {
            display: block;
            margin-bottom: 8px;
            color: #555;
            font-weight: 500;
        }

        .form-group input {
            width: 100%;
            padding: 12px 15px;
            border: 1px solid #ddd;
            border-radius: 6px;
            font-size: 16px;
            transition: border-color 0.3s;
        }

        .form-group input:focus {
            outline: none;
            border-color: #4CAF50;
            box-shadow: 0 0 0 2px rgba(76, 175, 80, 0.2);
        }

        .form-actions {
            margin-top: 30px;
            display: flex;
            flex-direction: column;
            gap: 12px;
        }

        .btn-primary, .btn-secondary {
            width: 100%;
            padding: 14px;
            font-size: 16px;
        }

        .error-message {
            background: #ffebee;
            color: #c62828;
            padding: 12px;
            border-radius: 6px;
            margin-bottom: 20px;
            text-align: center;
            font-size: 14px;
        }

        .footer-text {
            text-align: center;
            margin-top: 20px;
            color: #666;
            font-size: 14px;
        }
    </style>
</head>
<body>
<div class="login-container">
    <h2>用户登录</h2>

    <!-- 错误提示 -->
    <c:if test="${not empty error}">
        <div class="error-message">${error}</div>
    </c:if>

    <!-- 登录表单（带完整样式结构） -->
    <form action="${pageContext.request.contextPath}/doLogin" method="post">
        <div class="form-group">
            <label for="username">用户名</label>
            <input type="text" id="username" name="username" value="${param.username}" required>
        </div>
        <div class="form-group">
            <label for="password">密码</label>
            <input type="password" id="password" name="password" required>
        </div>
        <div class="form-actions">
            <button type="submit" class="btn btn-primary">登录</button>
            <a href="${pageContext.request.contextPath}/register" class="btn btn-secondary">去注册</a>
        </div>
    </form>

    <div class="footer-text">
        登录即表示同意我们的使用条款
    </div>
</div>
</body>
</html>