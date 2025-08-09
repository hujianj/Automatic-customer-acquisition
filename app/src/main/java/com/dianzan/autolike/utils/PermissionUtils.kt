package com.dianzan.autolike.utils

import android.content.Context
import android.provider.Settings
import com.dianzan.autolike.service.AutoLikeAccessibilityService

object PermissionUtils {
    
    /**
     * 检查无障碍服务是否已启用
     */
    fun isAccessibilityServiceEnabled(context: Context): Boolean {
        val accessibilityEnabled = Settings.Secure.getInt(
            context.contentResolver,
            Settings.Secure.ACCESSIBILITY_ENABLED, 0
        )
        
        if (accessibilityEnabled == 1) {
            val serviceString = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            )
            
            return serviceString?.contains("${context.packageName}/.service.AutoLikeAccessibilityService") == true
        }
        
        return false
    }
    
    /**
     * 检查无障碍服务是否正在运行
     */
    fun isAccessibilityServiceRunning(): Boolean {
        return AutoLikeAccessibilityService.instance != null
    }
    
    /**
     * 获取无障碍服务实例
     */
    fun getAccessibilityService(): AutoLikeAccessibilityService? {
        return AutoLikeAccessibilityService.instance
    }
} 