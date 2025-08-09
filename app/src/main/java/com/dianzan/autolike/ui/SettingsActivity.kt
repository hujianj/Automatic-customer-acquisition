package com.dianzan.autolike.ui

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.dianzan.autolike.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.textfield.TextInputEditText

class SettingsActivity : AppCompatActivity() {
    
    private lateinit var etMaxLikesPerDay: TextInputEditText
    private lateinit var etMaxFollowsPerDay: TextInputEditText
    private lateinit var etMaxCommentsPerDay: TextInputEditText
    private lateinit var cbEnableDebugMode: MaterialCheckBox
    private lateinit var cbAutoClearHistory: MaterialCheckBox
    private lateinit var cbEnableNotifications: MaterialCheckBox
    private lateinit var btnSaveSettings: MaterialButton
    
    private lateinit var sharedPreferences: SharedPreferences
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        
        initViews()
        loadSettings()
        setupListeners()
    }
    
    private fun initViews() {
        etMaxLikesPerDay = findViewById(R.id.et_max_likes_per_day)
        etMaxFollowsPerDay = findViewById(R.id.et_max_follows_per_day)
        etMaxCommentsPerDay = findViewById(R.id.et_max_comments_per_day)
        cbEnableDebugMode = findViewById(R.id.cb_enable_debug_mode)
        cbAutoClearHistory = findViewById(R.id.cb_auto_clear_history)
        cbEnableNotifications = findViewById(R.id.cb_enable_notifications)
        btnSaveSettings = findViewById(R.id.btn_save_settings)
    }
    
    private fun loadSettings() {
        etMaxLikesPerDay.setText(sharedPreferences.getInt("max_likes_per_day", 100).toString())
        etMaxFollowsPerDay.setText(sharedPreferences.getInt("max_follows_per_day", 50).toString())
        etMaxCommentsPerDay.setText(sharedPreferences.getInt("max_comments_per_day", 20).toString())
        cbEnableDebugMode.isChecked = sharedPreferences.getBoolean("enable_debug_mode", false)
        cbAutoClearHistory.isChecked = sharedPreferences.getBoolean("auto_clear_history", false)
        cbEnableNotifications.isChecked = sharedPreferences.getBoolean("enable_notifications", true)
    }
    
    private fun setupListeners() {
        btnSaveSettings.setOnClickListener {
            saveSettings()
        }
    }
    
    private fun saveSettings() {
        try {
            val maxLikesPerDay = etMaxLikesPerDay.text.toString().toInt()
            val maxFollowsPerDay = etMaxFollowsPerDay.text.toString().toInt()
            val maxCommentsPerDay = etMaxCommentsPerDay.text.toString().toInt()
            
            if (maxLikesPerDay < 0 || maxFollowsPerDay < 0 || maxCommentsPerDay < 0) {
                Toast.makeText(this, "数值不能为负数", Toast.LENGTH_SHORT).show()
                return
            }
            
            sharedPreferences.edit().apply {
                putInt("max_likes_per_day", maxLikesPerDay)
                putInt("max_follows_per_day", maxFollowsPerDay)
                putInt("max_comments_per_day", maxCommentsPerDay)
                putBoolean("enable_debug_mode", cbEnableDebugMode.isChecked)
                putBoolean("auto_clear_history", cbAutoClearHistory.isChecked)
                putBoolean("enable_notifications", cbEnableNotifications.isChecked)
            }.apply()
            
            Toast.makeText(this, getString(R.string.success_settings_saved), Toast.LENGTH_SHORT).show()
            finish()
            
        } catch (e: NumberFormatException) {
            Toast.makeText(this, "请输入有效的数字", Toast.LENGTH_SHORT).show()
        }
    }
} 