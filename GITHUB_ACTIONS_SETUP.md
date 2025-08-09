# GitHub Actions 自动构建指南

## 概述
本项目已配置GitHub Actions来自动构建Android APK。当您推送代码到GitHub时，系统会自动构建APK并创建发布版本。

## 设置步骤

### 1. 创建GitHub仓库
1. 访问 [GitHub](https://github.com)
2. 点击 "New repository"
3. 输入仓库名称，例如：`autolike-android`
4. 选择 "Public" 或 "Private"
5. 不要初始化README文件（因为我们已经有了）

### 2. 上传代码到GitHub
在您的项目目录中运行以下命令：

```bash
# 初始化Git仓库
git init

# 添加所有文件
git add .

# 提交更改
git commit -m "Initial commit: AutoLike Android app"

# 添加远程仓库（替换YOUR_USERNAME和YOUR_REPO_NAME）
git remote add origin https://github.com/YOUR_USERNAME/YOUR_REPO_NAME.git

# 推送到GitHub
git push -u origin main
```

### 3. 查看构建结果
1. 访问您的GitHub仓库页面
2. 点击 "Actions" 标签页
3. 您会看到构建工作流正在运行
4. 构建完成后，点击最新的构建记录查看详情

### 4. 下载APK
构建成功后，您可以通过以下方式获取APK：

#### 方式1：从Artifacts下载
1. 在Actions页面点击最新的构建记录
2. 滚动到底部，找到 "Artifacts" 部分
3. 点击 "app-debug" 下载APK文件

#### 方式2：从Releases下载
1. 在仓库页面点击 "Releases"
2. 找到最新版本
3. 下载 `app-debug.apk` 文件

## 工作流配置说明

### 触发条件
- 推送到 `main` 或 `master` 分支
- 创建Pull Request到 `main` 或 `master` 分支
- 手动触发（在Actions页面点击 "Run workflow"）

### 构建环境
- 操作系统：Ubuntu Latest
- Java版本：JDK 17
- Gradle版本：8.4

### 构建步骤
1. 检出代码
2. 设置Java环境
3. 缓存Gradle依赖
4. 构建项目
5. 生成APK
6. 上传Artifacts
7. 创建Release（仅限main/master分支）

## 故障排除

### 常见问题

#### 1. 构建失败
- 检查代码是否有语法错误
- 确保所有依赖都正确配置
- 查看构建日志获取详细错误信息

#### 2. APK无法下载
- 确保构建成功完成
- 检查网络连接
- 尝试从Releases页面下载

#### 3. 权限问题
- 确保仓库有足够的权限创建Releases
- 检查GitHub Actions权限设置

### 获取帮助
如果遇到问题，请：
1. 查看构建日志中的错误信息
2. 检查GitHub Actions的权限设置
3. 确保所有必需文件都已上传到仓库

## 本地测试
在推送到GitHub之前，您可以在本地测试构建：

```bash
# 使用Gradle Wrapper构建
./gradlew assembleDebug

# 构建完成后，APK将位于：
# app/build/outputs/apk/debug/app-debug.apk
```

## 注意事项
- 确保不要上传敏感信息（如API密钥）到公开仓库
- 定期更新依赖版本以保持安全性
- 监控构建时间和资源使用情况 