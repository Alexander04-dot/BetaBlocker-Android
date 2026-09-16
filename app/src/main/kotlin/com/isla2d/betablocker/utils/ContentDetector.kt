package com.isla2d.betablocker.utils

import android.view.accessibility.AccessibilityNodeInfo

class ContentDetector {
    fun detectCensorableContent(rootNode: AccessibilityNodeInfo?): List<CensorableContent> {
        val censorableContent = mutableListOf<CensorableContent>()
        
        rootNode?.let {
            traverseNodeTree(it, censorableContent)
        }
        
        return censorableContent
    }

    private fun traverseNodeTree(
        node: AccessibilityNodeInfo,
        censorableContent: MutableList<CensorableContent>
    ) {
        // Check if node should be censored based on content type
        val contentType = detectContentType(node)
        if (contentType != null) {
            val bounds = android.graphics.Rect()
            node.getBoundsInScreen(bounds)
            censorableContent.add(
                CensorableContent(
                    rect = bounds,
                    contentType = contentType,
                    confidence = 0.8f
                )
            )
        }

        // Recursively check child nodes
        for (i in 0 until node.childCount) {
            node.getChild(i)?.let {
                traverseNodeTree(it, censorableContent)
            }
        }
    }

    private fun detectContentType(node: AccessibilityNodeInfo): ContentType? {
        return when {
            node.contentDescription?.contains("image", ignoreCase = true) == true -> ContentType.IMAGE
            node.contentDescription?.contains("video", ignoreCase = true) == true -> ContentType.VIDEO
            node.className?.contains("ImageView", ignoreCase = true) == true -> ContentType.IMAGE
            else -> null
        }
    }
}

enum class ContentType {
    IMAGE,
    VIDEO,
    TEXT,
    UNKNOWN
}

data class CensorableContent(
    val rect: android.graphics.Rect,
    val contentType: ContentType,
    val confidence: Float
)
