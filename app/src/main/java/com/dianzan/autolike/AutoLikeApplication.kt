package com.dianzan.autolike

import android.app.Application
import com.dianzan.autolike.data.database.AppDatabase

class AutoLikeApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // 初始化数据库
        AppDatabase.getDatabase(this)
    }
} 