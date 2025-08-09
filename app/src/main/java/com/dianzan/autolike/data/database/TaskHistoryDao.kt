package com.dianzan.autolike.data.database

import androidx.room.*
import com.dianzan.autolike.data.model.TaskHistory
import com.dianzan.autolike.data.model.Platform
import com.dianzan.autolike.data.model.Action
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskHistoryDao {
    
    @Query("SELECT * FROM task_history ORDER BY timestamp DESC")
    fun getAllTaskHistory(): Flow<List<TaskHistory>>
    
    @Query("SELECT * FROM task_history WHERE platform = :platform ORDER BY timestamp DESC")
    fun getTaskHistoryByPlatform(platform: Platform): Flow<List<TaskHistory>>
    
    @Query("SELECT * FROM task_history WHERE action = :action ORDER BY timestamp DESC")
    fun getTaskHistoryByAction(action: Action): Flow<List<TaskHistory>>
    
    @Query("SELECT * FROM task_history WHERE timestamp >= :startTime AND timestamp <= :endTime ORDER BY timestamp DESC")
    fun getTaskHistoryByTimeRange(startTime: Long, endTime: Long): Flow<List<TaskHistory>>
    
    @Query("SELECT COUNT(*) FROM task_history WHERE action = 'LIKE' AND platform = :platform AND timestamp >= :startTime")
    suspend fun getLikesCountByPlatform(platform: Platform, startTime: Long): Int
    
    @Query("SELECT COUNT(*) FROM task_history WHERE action = 'FOLLOW' AND platform = :platform AND timestamp >= :startTime")
    suspend fun getFollowsCountByPlatform(platform: Platform, startTime: Long): Int
    
    @Query("SELECT COUNT(*) FROM task_history WHERE action = 'COMMENT' AND platform = :platform AND timestamp >= :startTime")
    suspend fun getCommentsCountByPlatform(platform: Platform, startTime: Long): Int
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTaskHistory(taskHistory: TaskHistory)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTaskHistories(taskHistories: List<TaskHistory>)
    
    @Delete
    suspend fun deleteTaskHistory(taskHistory: TaskHistory)
    
    @Query("DELETE FROM task_history WHERE timestamp < :timestamp")
    suspend fun deleteOldTaskHistory(timestamp: Long)
    
    @Query("DELETE FROM task_history")
    suspend fun deleteAllTaskHistory()
} 