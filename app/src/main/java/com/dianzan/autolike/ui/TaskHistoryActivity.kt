package com.dianzan.autolike.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dianzan.autolike.R
import com.dianzan.autolike.data.database.AppDatabase
import com.dianzan.autolike.data.model.Action
import com.dianzan.autolike.data.model.Platform
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class TaskHistoryActivity : AppCompatActivity() {
    
    private lateinit var tvTodayLikes: MaterialTextView
    private lateinit var tvTodayFollows: MaterialTextView
    private lateinit var tvTodayComments: MaterialTextView
    private lateinit var btnFilterAll: MaterialButton
    private lateinit var btnFilterLikes: MaterialButton
    private lateinit var btnFilterFollows: MaterialButton
    private lateinit var rvTaskHistory: RecyclerView
    private lateinit var llEmptyState: android.widget.LinearLayout
    
    private lateinit var database: AppDatabase
    private var currentFilter: Action? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_history)
        
        database = AppDatabase.getDatabase(this)
        
        initViews()
        setupListeners()
        loadTodayStats()
        loadTaskHistory()
    }
    
    private fun initViews() {
        tvTodayLikes = findViewById(R.id.tv_today_likes)
        tvTodayFollows = findViewById(R.id.tv_today_follows)
        tvTodayComments = findViewById(R.id.tv_today_comments)
        btnFilterAll = findViewById(R.id.btn_filter_all)
        btnFilterLikes = findViewById(R.id.btn_filter_likes)
        btnFilterFollows = findViewById(R.id.btn_filter_follows)
        rvTaskHistory = findViewById(R.id.rv_task_history)
        llEmptyState = findViewById(R.id.ll_empty_state)
        
        rvTaskHistory.layoutManager = LinearLayoutManager(this)
        // TODO: 设置适配器
    }
    
    private fun setupListeners() {
        btnFilterAll.setOnClickListener {
            currentFilter = null
            updateFilterButtons()
            loadTaskHistory()
        }
        
        btnFilterLikes.setOnClickListener {
            currentFilter = Action.LIKE
            updateFilterButtons()
            loadTaskHistory()
        }
        
        btnFilterFollows.setOnClickListener {
            currentFilter = Action.FOLLOW
            updateFilterButtons()
            loadTaskHistory()
        }
    }
    
    private fun updateFilterButtons() {
        btnFilterAll.isSelected = currentFilter == null
        btnFilterLikes.isSelected = currentFilter == Action.LIKE
        btnFilterFollows.isSelected = currentFilter == Action.FOLLOW
    }
    
    private fun loadTodayStats() {
        lifecycleScope.launch {
            val today = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
            
            val likesCount = database.taskHistoryDao().getLikesCountByPlatform(Platform.DOUYIN, today) +
                    database.taskHistoryDao().getLikesCountByPlatform(Platform.XIAOHONGSHU, today)
            
            val followsCount = database.taskHistoryDao().getFollowsCountByPlatform(Platform.DOUYIN, today) +
                    database.taskHistoryDao().getFollowsCountByPlatform(Platform.XIAOHONGSHU, today)
            
            val commentsCount = database.taskHistoryDao().getCommentsCountByPlatform(Platform.DOUYIN, today) +
                    database.taskHistoryDao().getCommentsCountByPlatform(Platform.XIAOHONGSHU, today)
            
            tvTodayLikes.text = likesCount.toString()
            tvTodayFollows.text = followsCount.toString()
            tvTodayComments.text = commentsCount.toString()
        }
    }
    
    private fun loadTaskHistory() {
        lifecycleScope.launch {
            val flow = when (currentFilter) {
                Action.LIKE -> database.taskHistoryDao().getTaskHistoryByAction(Action.LIKE)
                Action.FOLLOW -> database.taskHistoryDao().getTaskHistoryByAction(Action.FOLLOW)
                Action.COMMENT -> database.taskHistoryDao().getTaskHistoryByAction(Action.COMMENT)
                null -> database.taskHistoryDao().getAllTaskHistory()
            }
            
            flow.collect { taskHistories ->
                if (taskHistories.isEmpty()) {
                    rvTaskHistory.visibility = android.view.View.GONE
                    llEmptyState.visibility = android.view.View.VISIBLE
                } else {
                    rvTaskHistory.visibility = android.view.View.VISIBLE
                    llEmptyState.visibility = android.view.View.GONE
                    // TODO: 更新适配器数据
                }
            }
        }
    }
} 