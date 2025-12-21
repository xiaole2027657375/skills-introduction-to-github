// 主页面交互逻辑
document.addEventListener('DOMContentLoaded', function() {
    // 可以添加一些动画效果或交互逻辑
    console.log('垃圾分类识别系统已加载');

    // 示例：为功能卡片添加点击效果
    const featureCards = document.querySelectorAll('.feature-card');
    featureCards.forEach(card => {
        card.addEventListener('click', function() {
            this.style.transform = 'scale(0.95)';
            setTimeout(() => {
                this.style.transform = '';
            }, 150);
        });
    });
});