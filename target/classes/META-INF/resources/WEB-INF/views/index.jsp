<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<html>
<head>
    <title>图像识别垃圾分类</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        .container { max-width: 800px; margin-top: 30px; }
        .upload-area {
            border: 2px dashed #ccc;
            padding: 40px;
            text-align: center;
            margin: 20px 0;
            border-radius: 10px;
            background-color: #f8f9fa;
        }
        .result-area { margin-top: 30px; }
        .garbage-item {
            margin-bottom: 15px;
            padding: 15px;
            border-radius: 8px;
            border-left: 4px solid;
            background-color: #fff;
            box-shadow: 0 1px 3px rgba(0,0,0,0.1);
        }
        .recyclable { border-left-color: #007bff; }
        .hazardous { border-left-color: #dc3545; }
        .kitchen { border-left-color: #28a745; }
        .other { border-left-color: #6c757d; }
        .stats-card { text-align: center; padding: 20px; }
        .preview-img { max-height: 200px; border-radius: 8px; }
    </style>
</head>
<body>
<div class="container">
    <!-- 页面标题 -->
    <div class="text-center mb-4">
        <h1 class="text-primary">🗑️ 图像识别垃圾分类</h1>
        <p class="text-muted">上传垃圾图片，自动识别并分类</p>
    </div>

    <!-- 统计信息 -->
    <div class="row mb-4">
        <div class="col-md-6">
            <div class="card text-white bg-primary">
                <div class="card-body stats-card">
                    <h5 class="card-title">今日识别</h5>
                    <h2 class="card-text">${todayCount}</h2>
                </div>
            </div>
        </div>
        <div class="col-md-6">
            <div class="card text-white bg-success">
                <div class="card-body stats-card">
                    <h5 class="card-title">总识别次数</h5>
                    <h2 class="card-text">${totalCount}</h2>
                </div>
            </div>
        </div>
    </div>

    <!-- 错误提示 -->
    <c:if test="${not empty error}">
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            <strong>错误:</strong> ${error}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>

    <!-- 上传区域 -->
    <div class="upload-area">
        <form action="${pageContext.request.contextPath}/recognize"
              method="post" enctype="multipart/form-data" id="uploadForm">
            <div class="mb-3">
                <label for="fileInput" class="form-label">选择垃圾图片</label>
                <input type="file" class="form-control" id="fileInput" name="file"
                       accept="image/*" required onchange="previewImage(this)">
                <div class="form-text">支持 JPG、PNG、JPEG 格式，最大 10MB</div>
            </div>

            <!-- 图片预览 -->
            <div id="imagePreview" class="mt-3" style="display: none;">
                <img id="previewImg" class="img-fluid rounded preview-img">
            </div>

            <button type="submit" class="btn btn-primary btn-lg mt-3" id="submitBtn">
                <span class="spinner-border spinner-border-sm" id="loadingSpinner" style="display: none;"></span>
                开始识别
            </button>
        </form>
    </div>

    <!-- 识别结果 -->
    <c:if test="${not empty result && result.success}">
        <div class="result-area">
            <div class="card">
                <div class="card-header bg-primary text-white">
                    <h5 class="card-title mb-0">
                        📷 识别结果
                        <c:if test="${result.hasResults()}">
                                <span class="badge bg-light text-dark">
                                    共识别到 <c:out value="${result.items.size()}"/> 个物品
                                </span>
                        </c:if>
                    </h5>
                </div>
                <div class="card-body">
                    <!-- 图片显示 -->
                    <div class="text-center mb-4">
                        <img src="${imageUrl}" class="img-fluid rounded shadow preview-img" alt="识别图片">
                        <p class="text-muted mt-2">${result.message}</p>
                    </div>

                    <!-- 识别结果列表 -->
                    <c:if test="${result.hasResults()}">
                        <div class="mt-3">
                            <c:forEach var="item" items="${result.items}" varStatus="status">
                                <div class="garbage-item
                                        <c:choose>
                                            <c:when test="${item.category == '可回收垃圾'}">recyclable</c:when>
                                            <c:when test="${item.category == '有害垃圾'}">hazardous</c:when>
                                            <c:when test="${item.category == '厨余垃圾'}">kitchen</c:when>
                                            <c:when test="${item.category == '其他垃圾'}">other</c:when>
                                        </c:choose>">
                                    <div class="row align-items-center">
                                        <div class="col-md-8">
                                            <h5 class="mb-1">
                                                <span class="badge bg-secondary">${status.index + 1}</span>
                                                    ${item.keyword}
                                            </h5>
                                            <p class="mb-1 text-muted">
                                                置信度: <strong>${(item.score * 100)}%</strong>
                                            </p>
                                            <p class="mb-0">💡 ${item.suggestion}</p>
                                        </div>
                                        <div class="col-md-4 text-end">
                                                <span class="badge fs-6
                                                    <c:choose>
                                                        <c:when test="${item.category == '可回收垃圾'}">bg-primary</c:when>
                                                        <c:when test="${item.category == '有害垃圾'}">bg-danger</c:when>
                                                        <c:when test="${item.category == '厨余垃圾'}">bg-success</c:when>
                                                        <c:when test="${item.category == '其他垃圾'}">bg-secondary</c:when>
                                                        <c:otherwise>bg-warning</c:otherwise>
                                                    </c:choose>">
                                                        ${item.category}
                                                </span>
                                        </div>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                    </c:if>

                    <c:if test="${!result.hasResults()}">
                        <div class="alert alert-warning text-center">
                            <h5>⚠️ 未识别到有效物品</h5>
                            <p class="mb-0">请尝试上传更清晰的图片或调整拍摄角度</p>
                        </div>
                    </c:if>
                </div>
            </div>

            <!-- 操作按钮 -->
            <div class="text-center mt-4">
                <button onclick="resetForm()" class="btn btn-outline-primary">重新识别</button>
                <a href="${pageContext.request.contextPath}/history/list" class="btn btn-info">查看历史记录</a>
            </div>
        </div>
    </c:if>

    <!-- 功能说明 -->
    <div class="card mt-4">
        <div class="card-body">
            <h5>💡 使用说明</h5>
            <ul>
                <li>上传清晰的垃圾图片，系统会自动识别物品并分类</li>
                <li>支持识别多种常见垃圾物品</li>
                <li>系统会根据识别结果提供正确的投放建议</li>
                <li>所有识别记录都会保存，可在历史记录中查看</li>
            </ul>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
    // 图片预览
    function previewImage(input) {
        if (input.files && input.files[0]) {
            const reader = new FileReader();
            reader.onload = function(e) {
                document.getElementById('previewImg').src = e.target.result;
                document.getElementById('imagePreview').style.display = 'block';
            }
            reader.readAsDataURL(input.files[0]);
        }
    }

    // 重置表单
    function resetForm() {
        document.getElementById('uploadForm').reset();
        document.getElementById('imagePreview').style.display = 'none';
        // 清除结果区域
        window.location.href = '${pageContext.request.contextPath}/';
    }

    // 提交时显示加载动画
    document.getElementById('uploadForm').addEventListener('submit', function() {
        const submitBtn = document.getElementById('submitBtn');
        const loadingSpinner = document.getElementById('loadingSpinner');

        submitBtn.disabled = true;
        loadingSpinner.style.display = 'inline-block';
        submitBtn.innerHTML = ' 识别中...';
    });

    // 页面加载完成后初始化
    document.addEventListener('DOMContentLoaded', function() {
        console.log('垃圾分类系统页面加载完成');

        // 如果有结果，滚动到结果区域
        <c:if test="${not empty result && result.success}">
        document.querySelector('.result-area').scrollIntoView({
            behavior: 'smooth'
        });
        </c:if>
    });
</script>
</body>
</html>