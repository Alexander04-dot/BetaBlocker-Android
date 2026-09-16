package com.isla2d.betablocker.customization

import android.graphics.Bitmap
import android.graphics.Rect
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class CensorRule(
    val id: String,
    val contentType: String, // FACE, PERSON, HAND, HEAD, CUSTOM
    val effectType: String, // pixelate, blur, solid_box
    val effectColor: Int = 0xFF000000.toInt(),
    val enabled: Boolean = true,
    val priority: Int = 0,
    val customBoxBitmap: Bitmap? = null, // For custom image censoring
    val isCustomUpload: Boolean = false
)

data class CensorProfile(
    val id: String,
    val name: String,
    val description: String,
    val rules: List<CensorRule>,
    val createdAt: Long = System.currentTimeMillis()
)

class CensorizationCustomizer {
    private val _censorRules = MutableStateFlow<List<CensorRule>>(emptyList())
    val censorRules: Flow<List<CensorRule>> = _censorRules.asStateFlow()

    private val _censorProfiles = MutableStateFlow<List<CensorProfile>>(emptyList())
    val censorProfiles: Flow<List<CensorProfile>> = _censorProfiles.asStateFlow()

    private val _activeCensorRules = MutableStateFlow<Set<String>>(emptySet())
    val activeCensorRules: Flow<Set<String>> = _activeCensorRules.asStateFlow()

    // Preset profiles based on Isla2D reference
    fun initializePresets() {
        val presets = listOf(
            CensorProfile(
                id = "preset_basic",
                name = "Basic Censoring",
                description = "Censor faces only",
                rules = listOf(
                    CensorRule(
                        id = "rule_faces_basic",
                        contentType = "FACE",
                        effectType = "pixelate",
                        priority = 1
                    )
                )
            ),
            CensorProfile(
                id = "preset_moderate",
                name = "Moderate Censoring",
                description = "Censor faces and hands",
                rules = listOf(
                    CensorRule(
                        id = "rule_faces_moderate",
                        contentType = "FACE",
                        effectType = "blur",
                        priority = 1
                    ),
                    CensorRule(
                        id = "rule_hands_moderate",
                        contentType = "HAND",
                        effectType = "pixelate",
                        priority = 2
                    )
                )
            ),
            CensorProfile(
                id = "preset_strict",
                name = "Strict Censoring",
                description = "Censor all body parts and persons",
                rules = listOf(
                    CensorRule(
                        id = "rule_faces_strict",
                        contentType = "FACE",
                        effectType = "solid_box",
                        effectColor = 0xFF000000.toInt(),
                        priority = 1
                    ),
                    CensorRule(
                        id = "rule_hands_strict",
                        contentType = "HAND",
                        effectType = "solid_box",
                        effectColor = 0xFF000000.toInt(),
                        priority = 2
                    ),
                    CensorRule(
                        id = "rule_person_strict",
                        contentType = "PERSON",
                        effectType = "blur",
                        priority = 0
                    )
                )
            )
        )
        _censorProfiles.value = presets
    }

    suspend fun addCensorRule(rule: CensorRule) {
        val currentRules = _censorRules.value.toMutableList()
        currentRules.add(rule)
        _censorRules.value = currentRules
    }

    suspend fun removeCensorRule(ruleId: String) {
        val currentRules = _censorRules.value.filter { it.id != ruleId }
        _censorRules.value = currentRules
    }

    suspend fun updateCensorRule(ruleId: String, updatedRule: CensorRule) {
        val currentRules = _censorRules.value.map {
            if (it.id == ruleId) updatedRule else it
        }
        _censorRules.value = currentRules
    }

    suspend fun toggleRuleEnabled(ruleId: String) {
        val currentRules = _censorRules.value.map { rule ->
            if (rule.id == ruleId) rule.copy(enabled = !rule.enabled) else rule
        }
        _censorRules.value = currentRules
    }

    suspend fun applyProfile(profileId: String) {
        val profile = _censorProfiles.value.find { it.id == profileId }
        profile?.let {
            _censorRules.value = it.rules
        }
    }

    suspend fun createCustomProfile(
        name: String,
        description: String,
        rules: List<CensorRule>
    ): CensorProfile {
        val profile = CensorProfile(
            id = "custom_${System.currentTimeMillis()}",
            name = name,
            description = description,
            rules = rules
        )
        val currentProfiles = _censorProfiles.value.toMutableList()
        currentProfiles.add(profile)
        _censorProfiles.value = currentProfiles
        return profile
    }

    suspend fun uploadCustomCensorBox(
        contentType: String,
        imageBitmap: Bitmap,
        effectType: String = "custom_image"
    ): CensorRule {
        val rule = CensorRule(
            id = "custom_${System.currentTimeMillis()}",
            contentType = contentType,
            effectType = effectType,
            customBoxBitmap = imageBitmap,
            isCustomUpload = true,
            priority = 10
        )
        addCensorRule(rule)
        return rule
    }

    suspend fun setCensorRuleActive(ruleId: String, active: Boolean) {
        val current = _activeCensorRules.value.toMutableSet()
        if (active) {
            current.add(ruleId)
        } else {
            current.remove(ruleId)
        }
        _activeCensorRules.value = current
    }

    fun getActiveRules(): List<CensorRule> {
        val activeIds = _activeCensorRules.value
        return _censorRules.value.filter { it.id in activeIds && it.enabled }
    }

    fun getRulesForContentType(contentType: String): List<CensorRule> {
        return getActiveRules().filter { it.contentType == contentType }
            .sortedByDescending { it.priority }
    }

    suspend fun saveCustomProfile(profile: CensorProfile) {
        val currentProfiles = _censorProfiles.value.toMutableList()
        val existingIndex = currentProfiles.indexOfFirst { it.id == profile.id }
        if (existingIndex >= 0) {
            currentProfiles[existingIndex] = profile
        } else {
            currentProfiles.add(profile)
        }
        _censorProfiles.value = currentProfiles
    }

    suspend fun deleteCustomProfile(profileId: String) {
        val currentProfiles = _censorProfiles.value.filter { it.id != profileId }
        _censorProfiles.value = currentProfiles
    }
}
