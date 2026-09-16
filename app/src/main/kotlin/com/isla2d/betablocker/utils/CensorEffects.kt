package com.isla2d.betablocker.utils

import android.graphics.*

sealed class CensorEffect {
    data class Pixelate(val pixelSize: Int = 10) : CensorEffect()
    data class Blur(val radius: Float = 15f) : CensorEffect()
    data class SolidBox(val color: Int = 0xFF000000.toInt()) : CensorEffect()
}

class CensorEffectRenderer {
    fun applyEffect(bitmap: Bitmap, effect: CensorEffect, rect: Rect): Bitmap {
        return when (effect) {
            is CensorEffect.Pixelate -> pixelate(bitmap, rect, effect.pixelSize)
            is CensorEffect.Blur -> blur(bitmap, rect, effect.radius)
            is CensorEffect.SolidBox -> solidBox(bitmap, rect, effect.color)
        }
    }

    private fun pixelate(bitmap: Bitmap, rect: Rect, pixelSize: Int): Bitmap {
        val canvas = Canvas(bitmap)
        val paint = Paint(Paint.FILTER_BITMAP_FLAG).apply {
            isAntiAlias = false
        }

        var x = rect.left
        while (x < rect.right) {
            var y = rect.top
            while (y < rect.bottom) {
                val pixelColor = bitmap.getPixel(
                    (x + pixelSize / 2).coerceIn(0, bitmap.width - 1),
                    (y + pixelSize / 2).coerceIn(0, bitmap.height - 1)
                )
                paint.color = pixelColor
                canvas.drawRect(
                    x.toFloat(),
                    y.toFloat(),
                    (x + pixelSize).toFloat(),
                    (y + pixelSize).toFloat(),
                    paint
                )
                y += pixelSize
            }
            x += pixelSize
        }
        return bitmap
    }

    private fun blur(bitmap: Bitmap, rect: Rect, radius: Float): Bitmap {
        // Placeholder for blur implementation
        // In production, use RenderScript or Kotlin bitmap extensions
        return bitmap
    }

    private fun solidBox(bitmap: Bitmap, rect: Rect, color: Int): Bitmap {
        val canvas = Canvas(bitmap)
        val paint = Paint().apply {
            this.color = color
        }
        canvas.drawRect(rect.toRectF(), paint)
        return bitmap
    }

    private fun Rect.toRectF() = RectF(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat())
}
