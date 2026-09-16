package com.isla2d.betablocker.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BetaBlockerTheme {
                SettingsScreen()
            }
        }
    }

    @Composable
    fun SettingsScreen() {
        var censorStyle by remember { mutableStateOf("pixelate") }
        var sensitivity by remember { mutableStateOf(50f) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Censor Settings",
                style = MaterialTheme.typography.headlineMedium
            )

            Card {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Censor Style", style = MaterialTheme.typography.titleMedium)
                    CensorStyleDropdown(censorStyle) { censorStyle = it }
                }
            }

            Card {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Sensitivity: ${sensitivity.toInt()}%")
                    Slider(
                        value = sensitivity,
                        onValueChange = { sensitivity = it },
                        valueRange = 0f..100f,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }

    @Composable
    fun CensorStyleDropdown(selected: String, onSelect: (String) -> Unit) {
        val options = listOf("Pixelate", "Blur", "Solid Box")
        var expanded by remember { mutableStateOf(false) }

        Box {
            Button(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
                Text(selected)
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onSelect(option.lowercase())
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
