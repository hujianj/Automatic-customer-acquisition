package com.dianzan.autolike.manager

import android.content.Context
import android.content.Intent
import com.dianzan.autolike.data.model.TaskConfig
import com.dianzan.autolike.service.AutoLikeAccessibilityService
import com.dianzan.autolike.service.AutoLikeService
import com.dianzan.autolike.utils.PermissionUtils

object TaskManager {
    
    private var isRunning = false
    private var currentTaskConfig: TaskConfig? = null
    
    /**
     * 开始任务
     */
    fun startTask(context: Context, taskConfig: TaskConfig): Boolean {
        if (!PermissionUtils.isAccessibilityServiceEnabled(context)) {
            return false
        }
        
        // 启动前台服务
        val serviceIntent = Intent(context, AutoLikeService::class.java)
        context.startForegroundService(serviceIntent)
        
        // 启动无障碍服务任务
        AutoLikeAccessibilityService.instance?.startTask(taskConfig)
        
        isRunning = true
        currentTaskConfig = taskConfig
        
        return true
    }
    
    /**
     * 停止任务
     */
    fun stopTask(context: Context) {
        // 停止前台服务
        val serviceIntent = Intent(context, AutoLikeService::class.java)
        context.stopService(serviceIntent)
        
        // 停止无障碍服务任务
        AutoLikeAccessibilityService.instance?.stopTask()
        
        isRunning = false
        currentTaskConfig = null
    }
    
    /**
     * 检查任务是否正在运行
     */
    fun isTaskRunning(): Boolean {
        return isRunning && AutoLikeAccessibilityService.instance?.isTaskRunning() == true
    }
    
    /**
     * 获取当前任务配置
     */
    fun getCurrentTaskConfig(): TaskConfig? {
        return currentTaskConfig
    }
    
    /**
     * 检查无障碍服务是否可用
     */
    fun isAccessibilityServiceAvailable(): Boolean {
        return PermissionUtils.isAccessibilityServiceEnabled(null) && 
               PermissionUtils.isAccessibilityServiceRunning()
    }
} 