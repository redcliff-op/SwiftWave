package com.example.swiftwave

import android.app.Application
import io.sanghun.compose.video.cache.VideoPlayerCacheManager

class SwiftWave : Application() {
    override fun onCreate() {
        super.onCreate()
        VideoPlayerCacheManager.initialize(this, 1024 * 1024 * 1024)
    }
}