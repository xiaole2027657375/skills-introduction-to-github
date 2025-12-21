<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>注册</title>
    <!-- 引入公共样式 -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css">
    <style>
        /* 注册页面专属样式 */
        body {
            background-color: #f0f2f5;
            display: flex;
            flex-direction: column;
            min-height: 100vh;
        }

        .register-container {
            width: 100%;
            max-width: 400px;
            margin: auto; /* 垂直水平居中 */
            padding: 30px;
            background: white;
            border-radius: 12px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
        }

        .register-container h2 {
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
            border-color: #3498db;
            box-shadow: 0 0 0 2px rgba(52, 152, 219, 0.2);
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

        .tips {
            background: #e8f4fd;
            padding: 12px;
            border-radius: 6px;
            margin-bottom: 20px;
            font-size: 13px;
            color: #34495e;
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
<div class="register-container">
    <h2>用户注册</h2>

    <div class="tips">
        请设置您的账号和密码，注册后即可使用所有功能
    </div>

    <!-- 错误提示 -->
    <c:if test="${not empty error}">
        <div class="error-message">${error}</div>
    </c:if>

    <form action="${pageContext.request.contextPath}/doRegister" method="post">
        <div class="form-group">
            <label for="username">用户名</label>
            <input type="text" id="username" name="username" required
                   placeholder="请设置用户名（不少于3个字符）">
        </div>
        <div class="form-group">
            <label for="password">密码</label>
            <input type="password" id="password" name="password" required
                   placeholder="请设置密码（不少于6个字符）">
        </div>

        <div class="form-actions">
            <button type="submit" class="btn btn-primary">注册</button>
            <a href="${pageContext.request.contextPath}/login" class="btn btn-secondary">已有账号？登录</a>
        </div>
    </form>

    <div class="footer-text">
        注册即表示同意我们的使用条款和隐私政策
    </div>
</div>
</body>
</html>