package com.dianzan.autolike.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.content.Intent
import android.graphics.Path
import android.graphics.Rect
import android.os.Build
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import androidx.annotation.RequiresApi
import com.dianzan.autolike.data.model.Platform
import com.dianzan.autolike.data.model.Action
import com.dianzan.autolike.data.model.TaskHistory
import kotlinx.coroutines.*

class AutoLikeAccessibilityService : AccessibilityService() {
    
    companion object {
        private const val TAG = "AutoLikeAccessibilityService"
        var instance: AutoLikeAccessibilityService? = null
    }
    
    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var currentTaskConfig: com.dianzan.autolike.data.model.TaskConfig? = null
    private var isRunning = false
    private var currentPlatform: Platform? = null
    
    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        Log.d(TAG, "无障碍服务已连接")
    }
    
    override fun onDestroy() {
        super.onDestroy()
        instance = null
        serviceScope.cancel()
        Log.d(TAG, "无障碍服务已销毁")
    }
    
    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        if (!isRunning || currentTaskConfig == null) return
        
        when (event.eventType) {
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                handleWindowStateChanged(event)
            }
            AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED -> {
                handleWindowContentChanged(event)
            }
        }
    }
    
    override fun onInterrupt() {
        Log.d(TAG, "无障碍服务被中断")
    }
    
    private fun handleWindowStateChanged(event: AccessibilityEvent) {
        val packageName = event.packageName?.toString() ?: return
        
        when (packageName) {
            "com.ss.android.ugc.aweme" -> { // 抖音
                currentPlatform = Platform.DOUYIN
                handleDouyinWindow(event)
            }
            "com.xingin.discover" -> { // 小红书
                currentPlatform = Platform.XIAOHONGSHU
                handleXiaohongshuWindow(event)
            }
        }
    }
    
    private fun handleWindowContentChanged(event: AccessibilityEvent) {
        // 处理窗口内容变化
        if (currentPlatform != null) {
            serviceScope.launch {
                delay(1000) // 等待界面稳定
                performCurrentTask()
            }
        }
    }
    
    private fun handleDouyinWindow(event: AccessibilityEvent) {
        serviceScope.launch {
            delay(2000) // 等待界面加载
            when (currentTaskConfig?.runMode) {
                com.dianzan.autolike.data.model.RunMode.SEARCH -> performDouyinSearch()
                com.dianzan.autolike.data.model.RunMode.RECOMMEND -> performDouyinRecommend()
                else -> {}
            }
        }
    }
    
    private fun handleXiaohongshuWindow(event: AccessibilityEvent) {
        serviceScope.launch {
            delay(2000) // 等待界面加载
            when (currentTaskConfig?.runMode) {
                com.dianzan.autolike.data.model.RunMode.SEARCH -> performXiaohongshuSearch()
                com.dianzan.autolike.data.model.RunMode.RECOMMEND -> performXiaohongshuRecommend()
                else -> {}
            }
        }
    }
    
    private suspend fun performDouyinSearch() {
        // 执行抖音搜索模式
        Log.d(TAG, "执行抖音搜索模式")
        
        // 1. 点击搜索按钮
        clickSearchButton()
        delay(currentTaskConfig?.timeIntervals?.searchStayTime?.toLong() ?: 3000)
        
        // 2. 输入搜索关键词
        inputSearchKeywords()
        delay(2000)
        
        // 3. 点击搜索
        clickSearchButton()
        delay(currentTaskConfig?.timeIntervals?.searchStayTime?.toLong() ?: 3000)
        
        // 4. 浏览搜索结果
        browseSearchResults()
    }
    
    private suspend fun performDouyinRecommend() {
        // 执行抖音推荐模式
        Log.d(TAG, "执行抖音推荐模式")
        
        // 1. 浏览推荐视频
        browseRecommendVideos()
    }
    
    private suspend fun performXiaohongshuSearch() {
        // 执行小红书搜索模式
        Log.d(TAG, "执行小红书搜索模式")
        
        // 1. 点击搜索按钮
        clickSearchButton()
        delay(currentTaskConfig?.timeIntervals?.searchStayTime?.toLong() ?: 3000)
        
        // 2. 输入搜索关键词
        inputSearchKeywords()
        delay(2000)
        
        // 3. 点击搜索
        clickSearchButton()
        delay(currentTaskConfig?.timeIntervals?.searchStayTime?.toLong() ?: 3000)
        
        // 4. 浏览搜索结果
        browseSearchResults()
    }
    
    private suspend fun performXiaohongshuRecommend() {
        // 执行小红书推荐模式
        Log.d(TAG, "执行小红书推荐模式")
        
        // 1. 浏览推荐笔记
        browseRecommendNotes()
    }
    
    private suspend fun performCurrentTask() {
        when (currentPlatform) {
            Platform.DOUYIN -> performDouyinActions()
            Platform.XIAOHONGSHU -> performXiaohongshuActions()
            else -> {}
        }
    }
    
    private suspend fun performDouyinActions() {
        // 执行抖音相关操作
        if (currentTaskConfig?.features?.enableAutoLike == true) {
            performLikeAction()
        }
        
        if (currentTaskConfig?.features?.enableAutoFollow == true) {
            performFollowAction()
        }
        
        // 滑动到下一个视频
        performScrollAction()
    }
    
    private suspend fun performXiaohongshuActions() {
        // 执行小红书相关操作
        if (currentTaskConfig?.features?.enableAutoLike == true) {
            performLikeAction()
        }
        
        if (currentTaskConfig?.features?.enableAutoFollow == true) {
            performFollowAction()
        }
        
        // 滑动到下一个笔记
        performScrollAction()
    }
    
    private suspend fun clickSearchButton() {
        // 查找并点击搜索按钮
        val searchButton = findNodeByText("搜索") ?: findNodeByDesc("搜索")
        searchButton?.let {
            it.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            Log.d(TAG, "点击搜索按钮")
        }
    }
    
    private suspend fun inputSearchKeywords() {
        // 输入搜索关键词
        val keywords = currentTaskConfig?.searchKeywords ?: return
        val searchInput = findNodeByClass("android.widget.EditText")
        searchInput?.let {
            it.performAction(AccessibilityNodeInfo.ACTION_FOCUS)
            it.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, keywords)
            Log.d(TAG, "输入搜索关键词: $keywords")
        }
    }
    
    private suspend fun browseSearchResults() {
        // 浏览搜索结果
        Log.d(TAG, "浏览搜索结果")
        delay(currentTaskConfig?.timeIntervals?.videoStayTime?.toLong() ?: 5000)
    }
    
    private suspend fun browseRecommendVideos() {
        // 浏览推荐视频
        Log.d(TAG, "浏览推荐视频")
        delay(currentTaskConfig?.timeIntervals?.videoStayTime?.toLong() ?: 5000)
    }
    
    private suspend fun browseRecommendNotes() {
        // 浏览推荐笔记
        Log.d(TAG, "浏览推荐笔记")
        delay(currentTaskConfig?.timeIntervals?.videoStayTime?.toLong() ?: 5000)
    }
    
    private suspend fun performLikeAction() {
        // 执行点赞操作
        val likeButton = findNodeByDesc("点赞") ?: findNodeByText("点赞")
        likeButton?.let {
            it.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            Log.d(TAG, "执行点赞操作")
            delay(currentTaskConfig?.timeIntervals?.likeStayTime?.toLong() ?: 1000)
            
            // 记录任务历史
            recordTaskHistory(Action.LIKE, true)
        }
    }
    
    private suspend fun performFollowAction() {
        // 执行关注操作
        val followButton = findNodeByText("关注") ?: findNodeByDesc("关注")
        followButton?.let {
            it.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            Log.d(TAG, "执行关注操作")
            delay(currentTaskConfig?.timeIntervals?.followStayTime?.toLong() ?: 2000)
            
            // 记录任务历史
            recordTaskHistory(Action.FOLLOW, true)
        }
    }
    
    private suspend fun performScrollAction() {
        // 执行滑动操作
        val screenHeight = resources.displayMetrics.heightPixels
        val centerX = resources.displayMetrics.widthPixels / 2
        val startY = screenHeight * 0.8f
        val endY = screenHeight * 0.2f
        
        val path = Path().apply {
            moveTo(centerX.toFloat(), startY)
            lineTo(centerX.toFloat(), endY)
        }
        
        val gestureBuilder = GestureDescription.Builder(path)
        gestureBuilder.setDisplayId(displayId)
        
        dispatchGesture(gestureBuilder.build(), null, null)
        Log.d(TAG, "执行滑动操作")
        
        delay(currentTaskConfig?.timeIntervals?.scrollStayTime?.toLong() ?: 2000)
    }
    
    private fun findNodeByText(text: String): AccessibilityNodeInfo? {
        return rootInActiveWindow?.findAccessibilityNodeInfosByText(text)?.firstOrNull()
    }
    
    private fun findNodeByDesc(desc: String): AccessibilityNodeInfo? {
        return rootInActiveWindow?.findAccessibilityNodeInfosByViewId("android:id/content")?.firstOrNull()
            ?.findAccessibilityNodeInfosByText(desc)?.firstOrNull()
    }
    
    private fun findNodeByClass(className: String): AccessibilityNodeInfo? {
        return findNodeByClassRecursive(rootInActiveWindow, className)
    }
    
    private fun findNodeByClassRecursive(node: AccessibilityNodeInfo?, className: String): AccessibilityNodeInfo? {
        if (node == null) return null
        
        if (node.className == className) {
            return node
        }
        
        for (i in 0 until node.childCount) {
            val child = node.getChild(i)
            val result = findNodeByClassRecursive(child, className)
            if (result != null) {
                return result
            }
        }
        
        return null
    }
    
    private suspend fun recordTaskHistory(action: Action, success: Boolean) {
        val taskHistory = TaskHistory(
            taskConfigId = currentTaskConfig?.id ?: 0,
            platform = currentPlatform ?: Platform.DOUYIN,
            action = action,
            success = success,
            timestamp = System.currentTimeMillis()
        )
        
        // 这里应该保存到数据库，暂时只记录日志
        Log.d(TAG, "记录任务历史: $taskHistory")
    }
    
    fun startTask(taskConfig: com.dianzan.autolike.data.model.TaskConfig) {
        currentTaskConfig = taskConfig
        isRunning = true
        Log.d(TAG, "开始任务: ${taskConfig.name}")
    }
    
    fun stopTask() {
        isRunning = false
        currentTaskConfig = null
        Log.d(TAG, "停止任务")
    }
    
    fun isTaskRunning(): Boolean = isRunning
} 