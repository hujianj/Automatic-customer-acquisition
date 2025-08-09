package com.dianzan.autolike.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task_history")
data class TaskHistory(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val taskConfigId: Long = 0,
    val platform: Platform = Platform.DOUYIN,
    val action: Action = Action.LIKE,
    val targetUserId: String = "",
    val targetContentId: String = "",
    val success: Boolean = false,
    val errorMessage: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

enum class Action {
    LIKE,      // 点赞
    FOLLOW,    // 关注
    COMMENT    // 评论
}

data class DailyStats(
    val date: String,
    val platform: Platform,
    val likesCount: Int = 0,
    val followsCount: Int = 0,
    val commentsCount: Int = 0,
    val totalActions: Int = 0
) 