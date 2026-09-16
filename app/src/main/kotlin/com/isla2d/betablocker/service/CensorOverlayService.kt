package com.isla2d.betablocker.service

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout

class CensorOverlayService : Service() {
    companion object {
        private const val TAG = "CensorOverlayService"
    }

    private lateinit var windowManager: WindowManager
    private var overlayView: View? = null
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        Log.d(TAG, "CensorOverlayService Created")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "Service Started")
        startOverlay()
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun startOverlay() {
        handler.post {
            if (overlayView == null) {
                overlayView = createOverlayView()
                val params = WindowManager.LayoutParams().apply {
                    type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                    } else {
                        WindowManager.LayoutParams.TYPE_PHONE
                    }
                    format = PixelFormat.RGBA_8888
                    flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                    width = 1
                    height = 1
                    x = 0
                    y = 0
                }
                windowManager.addView(overlayView, params)
                Log.d(TAG, "Overlay View Added")
            }
        }
    }

    private fun createOverlayView(): View {
        val container = FrameLayout(this)
        container.setBackgroundColor(0x00000000) // Transparent
        return container
    }

    private fun stopOverlay() {
        handler.post {
            overlayView?.let {
                try {
                    windowManager.removeView(it)
                    overlayView = null
                    Log.d(TAG, "Overlay View Removed")
                } catch (e: Exception) {
                    Log.e(TAG, "Error removing overlay: ${e.message}")
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopOverlay()
        Log.d(TAG, "CensorOverlayService Destroyed")
    }
}
