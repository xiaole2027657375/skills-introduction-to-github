<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<head>
    <title>识别历史记录</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container mt-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h1>识别历史记录</h1>
        <a href="${pageContext.request.contextPath}/" class="btn btn-primary">返回识别</a>
    </div>

    <!-- 统计信息 -->
    <div class="row mb-4">
        <div class="col-md-4">
            <div class="card text-white bg-primary">
                <div class="card-body">
                    <h5 class="card-title">总识别次数</h5>
                    <h2 class="card-text">${totalCount}</h2>
                </div>
            </div>
        </div>
        <div class="col-md-4">
            <div class="card text-white bg-success">
                <div class="card-body">
                    <h5 class="card-title">今日识别</h5>
                    <h2 class="card-text">${todayCount}</h2>
                </div>
            </div>
        </div>
        <div class="col-md-4">
            <div class="card text-white bg-info">
                <div class="card-body">
                    <h5 class="card-title">历史记录数</h5>
                    <h2 class="card-text">${historyList.size()}</h2>
                </div>
            </div>
        </div>
    </div>

    <!-- 历史记录列表 -->
    <div class="card">
        <div class="card-header">
            <h5 class="card-title mb-0">识别记录详情</h5>
        </div>
        <div class="card-body">
            <c:if test="${empty historyList}">
                <div class="text-center text-muted py-4">
                    <h5>暂无识别记录</h5>
                    <p>快去上传图片进行识别吧！</p>
                </div>
            </c:if>

            <c:forEach var="history" items="${historyList}">
                <div class="card mb-3">
                    <div class="card-body">
                        <div class="row">
                            <div class="col-md-2">
                                <img src="${pageContext.request.contextPath}${history.imagePath}"
                                     class="img-fluid rounded" alt="识别图片">
                            </div>
                            <div class="col-md-10">
                                <h6>原始文件: ${history.originalFilename}</h6>
                                <p class="text-muted">
                                    识别时间: <fmt:formatDate value="${history.recognitionTime}" pattern="yyyy-MM-dd HH:mm:ss"/> |
                                    IP: ${history.ipAddress}
                                </p>
                                <!-- 这里可以解析recognitionResult显示具体识别结果 -->
                            </div>
                            <!-- 添加删除按钮表单 -->
                            <div class="mt-2">
                                <form action="${pageContext.request.contextPath}/history/delete/${history.id}" method="post"
                                      onsubmit="return confirm('确定要删除这条记录吗？')">
                                    <button type="submit" class="btn btn-danger btn-sm">删除记录</button>
                                </form>
                            </div>

                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>
    </div>
</div>
</body>
</html>