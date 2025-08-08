package com.dianzan.autolike.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TaskConfig(
    val id: Long = 0,
    val name: String = "",
    val runMode: RunMode = RunMode.SEARCH,
    val platforms: List<Platform> = listOf(),
    val searchKeywords: String = "",
    val titleFilter: String = "",
    val targetComments: String = "",
    val timeIntervals: TimeIntervals = TimeIntervals(),
    val features: Features = Features(),
    val limits: Limits = Limits(),
    val isEnabled: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) : Parcelable

enum class RunMode {
    SEARCH,    // 搜索养号
    RECOMMEND  // 推荐养号
}

enum class Platform {
    DOUYIN,      // 抖音
    XIAOHONGSHU  // 小红书
}

@Parcelize
data class TimeIntervals(
    val searchStayTime: Int = 3,      // 搜索停留时间(秒)
    val videoStayTime: Int = 5,       // 视频/笔记停留时间(秒)
    val scrollStayTime: Int = 2,      // 停留下滑时间(秒)
    val likeStayTime: Int = 1,        // 点赞停留时间(秒)
    val followStayTime: Int = 2,      // 关注停留时间(秒)
    val followCountInterval: Int = 10  // 关注多少个用户后停留
) : Parcelable

@Parcelize
data class Features(
    val enableAutoLike: Boolean = true,
    val enableAutoFollow: Boolean = true,
    val enableAutoComment: Boolean = false
) : Parcelable

@Parcelize
data class Limits(
    val maxLikesPerDay: Int = 100,
    val maxFollowsPerDay: Int = 50,
    val maxCommentsPerDay: Int = 20
) : Parcelable 