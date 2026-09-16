package com.isla2d.betablocker.customization

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class CustomCensorBox(
    val id: String,
    val name: String,
    val bitmap: Bitmap,
    val contentType: String, // FACE, PERSON, HAND, HEAD
    val uploadedAt: Long = System.currentTimeMillis(),
    val filePath: String
)

class CustomCensorBoxManager(private val context: Context) {
    private val _customBoxes = MutableStateFlow<List<CustomCensorBox>>(emptyList())
    val customBoxes: Flow<List<CustomCensorBox>> = _customBoxes.asStateFlow()

    private val censorBoxDir: File by lazy {
        File(context.cacheDir, "censor_boxes").apply {
            if (!exists()) mkdirs()
        }
    }

    suspend fun uploadCensorBox(
        uri: Uri,
        name: String,
        contentType: String
    ): Result<CustomCensorBox> = runCatching {
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: throw IllegalArgumentException("Cannot open file")

        val bitmap = BitmapFactory.decodeStream(inputStream)
            ?: throw IllegalArgumentException("Invalid image format")

        inputStream.close()

        // Save bitmap to local cache
        val fileName = "censor_${System.currentTimeMillis()}.png"
        val file = File(censorBoxDir, fileName)
        val fos = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
        fos.close()

        val censorBox = CustomCensorBox(
            id = "box_${System.currentTimeMillis()}",
            name = name,
            bitmap = bitmap,
            contentType = contentType,
            filePath = file.absolutePath
        )

        val currentBoxes = _customBoxes.value.toMutableList()
        currentBoxes.add(censorBox)
        _customBoxes.value = currentBoxes

        censorBox
    }

    suspend fun removeCensorBox(boxId: String) {
        val currentBoxes = _customBoxes.value.toMutableList()
        val box = currentBoxes.find { it.id == boxId }
        box?.let {
            // Delete file
            File(it.filePath).delete()
            currentBoxes.remove(it)
            _customBoxes.value = currentBoxes
        }
    }

    suspend fun renameCensorBox(boxId: String, newName: String) {
        val currentBoxes = _customBoxes.value.map { box ->
            if (box.id == boxId) box.copy(name = newName) else box
        }
        _customBoxes.value = currentBoxes
    }

    fun getCensorBoxesForContentType(contentType: String): List<CustomCensorBox> {
        return _customBoxes.value.filter { it.contentType == contentType }
    }

    fun getCensorBox(boxId: String): CustomCensorBox? {
        return _customBoxes.value.find { it.id == boxId }
    }

    fun getAllCensorBoxes(): List<CustomCensorBox> {
        return _customBoxes.value
    }

    suspend fun clearAllCensorBoxes() {
        _customBoxes.value.forEach { box ->
            File(box.filePath).delete()
        }
        _customBoxes.value = emptyList()
    }

    fun exportCensorBox(boxId: String): Uri? {
        val box = getCensorBox(boxId) ?: return null
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            File(box.filePath)
        )
    }

    suspend fun loadCensorBoxesFromCache() {
        val boxes = censorBoxDir.listFiles()?.mapNotNull { file ->
            try {
                val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                    ?: return@mapNotNull null

                CustomCensorBox(
                    id = file.nameWithoutExtension,
                    name = file.nameWithoutExtension,
                    bitmap = bitmap,
                    contentType = "CUSTOM",
                    filePath = file.absolutePath
                )
            } catch (e: Exception) {
                null
            }
        } ?: emptyList()

        _customBoxes.value = boxes
    }
}
