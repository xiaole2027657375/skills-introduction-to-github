// 上传页面交互逻辑
document.addEventListener('DOMContentLoaded', function() {
    const uploadArea = document.getElementById('uploadArea');
    const fileInput = document.getElementById('imageFile');
    const previewImage = document.getElementById('previewImage');
    const submitBtn = document.getElementById('submitBtn');
    const uploadForm = document.getElementById('uploadForm');

    // 点击上传区域触发文件选择
    uploadArea.addEventListener('click', function() {
        fileInput.click();
    });

    // 文件选择变化
    fileInput.addEventListener('change', function(e) {
        handleFileSelect(e.target.files[0]);
    });

    // 拖拽功能
    uploadArea.addEventListener('dragover', function(e) {
        e.preventDefault();
        uploadArea.classList.add('dragover');
    });

    uploadArea.addEventListener('dragleave', function() {
        uploadArea.classList.remove('dragover');
    });

    uploadArea.addEventListener('drop', function(e) {
        e.preventDefault();
        uploadArea.classList.remove('dragover');
        const files = e.dataTransfer.files;
        if (files.length > 0) {
            handleFileSelect(files[0]);
        }
    });

    function handleFileSelect(file) {
        if (file) {
            // 验证文件类型
            if (!file.type.match('image.*')) {
                alert('请选择图片文件！');
                return;
            }

            // 验证文件大小 (10MB)
            if (file.size > 10 * 1024 * 1024) {
                alert('文件大小不能超过10MB！');
                return;
            }

            // 显示预览
            const reader = new FileReader();
            reader.onload = function(e) {
                previewImage.src = e.target.result;
                previewImage.style.display = 'block';
                uploadArea.querySelector('.upload-placeholder').style.display = 'none';
                submitBtn.disabled = false;
            };
            reader.readAsDataURL(file);
        }
    }

    // 表单提交
    uploadForm.addEventListener('submit', function(e) {
        if (!fileInput.files[0]) {
            e.preventDefault();
            alert('请选择要识别的图片！');
            return;
        }

        // 显示加载状态
        submitBtn.disabled = true;
        submitBtn.textContent = '识别中...';
    });
});

function resetForm() {
    const fileInput = document.getElementById('imageFile');
    const previewImage = document.getElementById('previewImage');
    const submitBtn = document.getElementById('submitBtn');
    const uploadArea = document.getElementById('uploadArea');

    fileInput.value = '';
    previewImage.style.display = 'none';
    uploadArea.querySelector('.upload-placeholder').style.display = 'block';
    submitBtn.disabled = true;
    submitBtn.textContent = '开始识别';
}