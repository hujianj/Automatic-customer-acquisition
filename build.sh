#!/bin/bash

echo "开始构建自动点赞应用..."

# 检查是否安装了Android SDK
if ! command -v adb &> /dev/null; then
    echo "错误: 未找到Android SDK，请确保已安装Android Studio并配置环境变量"
    exit 1
fi

# 清理项目
echo "清理项目..."
./gradlew clean

# 构建Debug版本
echo "构建Debug版本..."
./gradlew assembleDebug

if [ $? -eq 0 ]; then
    echo "构建成功！"
    echo "APK文件位置: app/build/outputs/apk/debug/app-debug.apk"
    echo ""
    echo "安装说明:"
    echo "1. 将APK文件传输到Android设备"
    echo "2. 在设备上安装APK文件"
    echo "3. 打开应用并授予必要权限"
    echo "4. 在设置中启用无障碍服务"
    echo "5. 配置任务参数并开始使用"
else
    echo "构建失败，请检查错误信息"
    exit 1
fi 