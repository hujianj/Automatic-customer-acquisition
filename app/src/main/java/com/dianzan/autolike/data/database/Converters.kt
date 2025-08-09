package com.dianzan.autolike.data.database

import androidx.room.TypeConverter
import com.dianzan.autolike.data.model.Platform
import com.dianzan.autolike.data.model.Action

class Converters {
    
    @TypeConverter
    fun fromPlatform(platform: Platform): String {
        return platform.name
    }
    
    @TypeConverter
    fun toPlatform(value: String): Platform {
        return Platform.valueOf(value)
    }
    
    @TypeConverter
    fun fromAction(action: Action): String {
        return action.name
    }
    
    @TypeConverter
    fun toAction(value: String): Action {
        return Action.valueOf(value)
    }
} 