package com.isla2d.betablocker.ml

import android.content.Context
import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.custom.CustomObjectDetectorOptions

class MLContentDetector(context: Context) {
    private val faceDetector: FaceDetector = FaceDetection.getClient()
    private var objectDetector = setupObjectDetector(context)

    companion object {
        const val FACE = "FACE"
        const val PERSON = "PERSON"
        const val HAND = "HAND"
        const val HEAD = "HEAD"
    }

    private fun setupObjectDetector(context: Context): com.google.mlkit.vision.objects.ObjectDetector {
        val options = CustomObjectDetectorOptions.Builder()
            .setDetectorMode(CustomObjectDetectorOptions.STREAM_MODE)
            .build()
        return ObjectDetection.getClient(options)
    }

    suspend fun detectFaces(bitmap: Bitmap): List<DetectedObject> {
        return try {
            val image = InputImage.fromBitmap(bitmap, 0)
            val faces = faceDetector.process(image)
                .addOnSuccessListener { detectedFaces ->
                    // Faces detected
                }
                .result

            faces.map { face ->
                DetectedObject(
                    type = FACE,
                    boundingBox = face.boundingBox,
                    confidence = 0.95f,
                    label = "Face"
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun detectObjects(bitmap: Bitmap): List<DetectedObject> {
        return try {
            val image = InputImage.fromBitmap(bitmap, 0)
            val detectedObjects = objectDetector.process(image).result

            detectedObjects.mapNotNull { obj ->
                val label = obj.labels.firstOrNull()?.text ?: return@mapNotNull null
                val type = when {
                    label.contains("person", ignoreCase = true) -> PERSON
                    label.contains("hand", ignoreCase = true) -> HAND
                    label.contains("head", ignoreCase = true) -> HEAD
                    else -> label
                }

                DetectedObject(
                    type = type,
                    boundingBox = obj.boundingBox,
                    confidence = obj.labels.firstOrNull()?.confidence ?: 0.5f,
                    label = label
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun detectAllContent(bitmap: Bitmap): List<DetectedObject> {
        val faces = detectFaces(bitmap)
        val objects = detectObjects(bitmap)
        return (faces + objects).distinctBy { "${it.boundingBox}" }
    }

    fun close() {
        faceDetector.close()
        objectDetector.close()
    }
}

data class DetectedObject(
    val type: String,
    val boundingBox: android.graphics.Rect,
    val confidence: Float,
    val label: String
)
