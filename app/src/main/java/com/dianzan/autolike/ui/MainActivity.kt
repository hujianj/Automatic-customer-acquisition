package com.dianzan.autolike.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dianzan.autolike.R
import com.dianzan.autolike.data.model.*
import com.dianzan.autolike.service.AutoLikeAccessibilityService
import com.dianzan.autolike.service.AutoLikeService
import com.dianzan.autolike.utils.PermissionUtils
import com.dianzan.autolike.manager.TaskManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.radiobutton.MaterialRadioButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    
    private lateinit var tvTaskStatus: MaterialTextView
    private lateinit var btnStartTask: MaterialButton
    private lateinit var btnStopTask: MaterialButton
    private lateinit var btnSettings: MaterialButton
    private lateinit var btnHistory: MaterialButton
    
    // 运行模式
    private lateinit var rbSearchMode: MaterialRadioButton
    private lateinit var rbRecommendMode: MaterialRadioButton
    
    // 搜索设置
    private lateinit var cardSearchSettings: MaterialCardView
    private lateinit var etSearchKeywords: TextInputEditText
    private lateinit var etTitleFilter: TextInputEditText
    private lateinit var etTargetComments: TextInputEditText
    
    // 平台选择
    private lateinit var cbDouyin: MaterialCheckBox
    private lateinit var cbXiaohongshu: MaterialCheckBox
    
    // 时间间隔设置
    private lateinit var etSearchStayTime: TextInputEditText
    private lateinit var etVideoStayTime: TextInputEditText
    private lateinit var etScrollStayTime: TextInputEditText
    private lateinit var etLikeStayTime: TextInputEditText
    private lateinit var etFollowStayTime: TextInputEditText
    private lateinit var etFollowCountInterval: TextInputEditText
    
    // 功能开关
    private lateinit var cbEnableAutoLike: MaterialCheckBox
    private lateinit var cbEnableAutoFollow: MaterialCheckBox
    private lateinit var cbEnableAutoComment: MaterialCheckBox
    

    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        initViews()
        setupListeners()
        updateTaskStatus()
    }
    
    private fun initViews() {
        tvTaskStatus = findViewById(R.id.tv_task_status)
        btnStartTask = findViewById(R.id.btn_start_task)
        btnStopTask = findViewById(R.id.btn_stop_task)
        btnSettings = findViewById(R.id.btn_settings)
        btnHistory = findViewById(R.id.btn_history)
        
        rbSearchMode = findViewById(R.id.rb_search_mode)
        rbRecommendMode = findViewById(R.id.rb_recommend_mode)
        
        cardSearchSettings = findViewById(R.id.card_search_settings)
        etSearchKeywords = findViewById(R.id.et_search_keywords)
        etTitleFilter = findViewById(R.id.et_title_filter)
        etTargetComments = findViewById(R.id.et_target_comments)
        
        cbDouyin = findViewById(R.id.cb_douyin)
        cbXiaohongshu = findViewById(R.id.cb_xiaohongshu)
        
        etSearchStayTime = findViewById(R.id.et_search_stay_time)
        etVideoStayTime = findViewById(R.id.et_video_stay_time)
        etScrollStayTime = findViewById(R.id.et_scroll_stay_time)
        etLikeStayTime = findViewById(R.id.et_like_stay_time)
        etFollowStayTime = findViewById(R.id.et_follow_stay_time)
        etFollowCountInterval = findViewById(R.id.et_follow_count_interval)
        
        cbEnableAutoLike = findViewById(R.id.cb_enable_auto_like)
        cbEnableAutoFollow = findViewById(R.id.cb_enable_auto_follow)
        cbEnableAutoComment = findViewById(R.id.cb_enable_auto_comment)
    }
    
    private fun setupListeners() {
        btnStartTask.setOnClickListener {
            startTask()
        }
        
        btnStopTask.setOnClickListener {
            stopTask()
        }
        
        btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
        
        btnHistory.setOnClickListener {
            startActivity(Intent(this, TaskHistoryActivity::class.java))
        }
        
        // 运行模式切换时显示/隐藏搜索设置
        rbSearchMode.setOnCheckedChangeListener { _, isChecked ->
            cardSearchSettings.visibility = if (isChecked) android.view.View.VISIBLE else android.view.View.GONE
        }
        
        rbRecommendMode.setOnCheckedChangeListener { _, isChecked ->
            cardSearchSettings.visibility = if (isChecked) android.view.View.GONE else android.view.View.VISIBLE
        }
    }
    
    private fun startTask() {
        if (!PermissionUtils.isAccessibilityServiceEnabled(this)) {
            showAccessibilitySettingsDialog()
            return
        }
        
        if (!isPlatformSelected()) {
            Toast.makeText(this, getString(R.string.error_no_platform_selected), Toast.LENGTH_SHORT).show()
            return
        }
        
        val taskConfig = buildTaskConfig()
        if (taskConfig == null) {
            Toast.makeText(this, getString(R.string.error_invalid_time_interval), Toast.LENGTH_SHORT).show()
            return
        }
        
        if (TaskManager.startTask(this, taskConfig)) {
            updateTaskStatus()
            Toast.makeText(this, getString(R.string.success_task_started), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, getString(R.string.error_accessibility_not_enabled), Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun stopTask() {
        TaskManager.stopTask(this)
        updateTaskStatus()
        Toast.makeText(this, getString(R.string.success_task_stopped), Toast.LENGTH_SHORT).show()
    }
    
    private fun updateTaskStatus() {
        val isTaskRunning = TaskManager.isTaskRunning()
        val status = if (isTaskRunning) getString(R.string.running) else getString(R.string.stopped)
        val color = if (isTaskRunning) getColor(R.color.success) else getColor(R.color.text_secondary)
        
        tvTaskStatus.text = status
        tvTaskStatus.setTextColor(color)
        
        btnStartTask.isEnabled = !isTaskRunning
        btnStopTask.isEnabled = isTaskRunning
    }
    
    private fun buildTaskConfig(): TaskConfig? {
        return try {
            val runMode = if (rbSearchMode.isChecked) RunMode.SEARCH else RunMode.RECOMMEND
            
            val platforms = mutableListOf<Platform>()
            if (cbDouyin.isChecked) platforms.add(Platform.DOUYIN)
            if (cbXiaohongshu.isChecked) platforms.add(Platform.XIAOHONGSHU)
            
            val timeIntervals = TimeIntervals(
                searchStayTime = etSearchStayTime.text.toString().toIntOrNull() ?: 3,
                videoStayTime = etVideoStayTime.text.toString().toIntOrNull() ?: 5,
                scrollStayTime = etScrollStayTime.text.toString().toIntOrNull() ?: 2,
                likeStayTime = etLikeStayTime.text.toString().toIntOrNull() ?: 1,
                followStayTime = etFollowStayTime.text.toString().toIntOrNull() ?: 2,
                followCountInterval = etFollowCountInterval.text.toString().toIntOrNull() ?: 10
            )
            
            val features = Features(
                enableAutoLike = cbEnableAutoLike.isChecked,
                enableAutoFollow = cbEnableAutoFollow.isChecked,
                enableAutoComment = cbEnableAutoComment.isChecked
            )
            
            TaskConfig(
                name = "自动点赞任务",
                runMode = runMode,
                platforms = platforms,
                searchKeywords = etSearchKeywords.text.toString(),
                titleFilter = etTitleFilter.text.toString(),
                targetComments = etTargetComments.text.toString(),
                timeIntervals = timeIntervals,
                features = features,
                isEnabled = true
            )
        } catch (e: Exception) {
            null
        }
    }
    

    
    private fun isPlatformSelected(): Boolean {
        return cbDouyin.isChecked || cbXiaohongshu.isChecked
    }
    
    private fun showAccessibilitySettingsDialog() {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("需要无障碍权限")
            .setMessage("自动点赞功能需要无障碍服务权限才能正常工作。请在设置中启用无障碍服务。")
            .setPositiveButton("去设置") { _, _ ->
                val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                startActivity(intent)
            }
            .setNegativeButton("取消", null)
            .show()
    }
    
    override fun onResume() {
        super.onResume()
        updateTaskStatus()
    }
} 