package com.isla2d.betablocker.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.isla2d.betablocker.customization.CensorRule
import com.isla2d.betablocker.customization.CensorizationCustomizer

@Composable
fun CustomizationScreen(customizer: CensorizationCustomizer) {
    val censorRules by customizer.censorRules.collectAsState(initial = emptyList())
    val censorProfiles by customizer.censorProfiles.collectAsState(initial = emptyList())
    var selectedTab by remember { mutableStateOf(0) }
    var showAddRuleDialog by remember { mutableStateOf(false) }
    var showProfileDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        customizer.initializePresets()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Customization",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        TabRow(selectedTabIndex = selectedTab) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Profiles") }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Rules") }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("Custom Boxes") }
            )
        }

        when (selectedTab) {
            0 -> ProfilesTab(censorProfiles, customizer)
            1 -> RulesTab(censorRules, customizer, onAddClick = { showAddRuleDialog = true })
            2 -> CustomBoxesTab(customizer)
        }

        if (showAddRuleDialog) {
            AddRuleDialog(
                customizer = customizer,
                onDismiss = { showAddRuleDialog = false }
            )
        }
    }
}

@Composable
fun ProfilesTab(
    profiles: List<com.isla2d.betablocker.customization.CensorProfile>,
    customizer: CensorizationCustomizer
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(profiles) { profile ->
            ProfileCard(profile, customizer)
        }
    }
}

@Composable
fun ProfileCard(
    profile: com.isla2d.betablocker.customization.CensorProfile,
    customizer: CensorizationCustomizer
) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (isExpanded) Modifier.height(200.dp)
                else Modifier.height(100.dp)
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = profile.name,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = profile.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Icon(
                    imageVector = if (isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = null
                )
            }

            if (isExpanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Rules: ${profile.rules.size}",
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        // Apply profile
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Apply Profile")
                }
            }
        }
    }
}

@Composable
fun RulesTab(
    censorRules: List<CensorRule>,
    customizer: CensorizationCustomizer,
    onAddClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Button(
            onClick = onAddClick,
            modifier = Modifier
                .align(Alignment.End)
                .padding(16.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = null)
            Text("Add Rule")
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(censorRules) { rule ->
                RuleCard(rule, customizer)
            }
        }
    }
}

@Composable
fun RuleCard(rule: CensorRule, customizer: CensorizationCustomizer) {
    var isChecked by remember { mutableStateOf(rule.enabled) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = rule.contentType,
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = "Effect: ${rule.effectType}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
            Checkbox(
                checked = isChecked,
                onCheckedChange = {
                    isChecked = it
                    // Update rule
                }
            )
        }
    }
}

@Composable
fun CustomBoxesTab(customizer: CensorizationCustomizer) {
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            // Handle image upload
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Upload Custom Censor Images",
            style = MaterialTheme.typography.headlineSmall
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(8.dp)
                ),
            onClick = { imagePickerLauncher.launch("image/*") }
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.CloudUpload,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text("Tap to select image")
            }
        }

        Text(
            text = "Supported formats: PNG, JPG, JPEG",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )

        Divider()

        Text(
            text = "Select Content Type",
            style = MaterialTheme.typography.titleMedium
        )

        val contentTypes = listOf("FACE", "PERSON", "HAND", "HEAD")
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            contentTypes.forEach { type ->
                Button(
                    onClick = { },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Censor $type")
                }
            }
        }
    }
}

@Composable
fun AddRuleDialog(
    customizer: CensorizationCustomizer,
    onDismiss: () -> Unit
) {
    var contentType by remember { mutableStateOf("FACE") }
    var effectType by remember { mutableStateOf("pixelate") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Censor Rule") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Content Type:", style = MaterialTheme.typography.bodyMedium)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("FACE", "PERSON", "HAND", "HEAD").forEach { type ->
                        FilterChip(
                            selected = contentType == type,
                            onClick = { contentType = type },
                            label = { Text(type) }
                        )
                    }
                }

                Text("Effect:", style = MaterialTheme.typography.bodyMedium)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("pixelate", "blur", "solid_box").forEach { effect ->
                        FilterChip(
                            selected = effectType == effect,
                            onClick = { effectType = effect },
                            label = { Text(effect) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                // Create rule
                onDismiss()
            }) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
