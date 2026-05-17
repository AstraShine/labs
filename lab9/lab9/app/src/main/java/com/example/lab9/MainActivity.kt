package com.example.lab9

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lab9.data.AppTheme
import com.example.lab9.data.SettingsManager
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import com.example.lab9.ui.screens.SettingsScreen
import com.example.lab9.ui.theme.ThemeSwitcherTheme
import com.example.lab9.ui.theme.ThemeViewModel
import com.example.lab9.ui.theme.ThemeViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val settingsManager = SettingsManager(this)

        setContent {
            var showSettings by remember { mutableStateOf(false) }

            ThemeSwitcherTheme(
                viewModel = viewModel(factory = ThemeViewModelFactory(settingsManager))
            ) {
                if (showSettings) {
                    SettingsScreen(
                        onBack = { showSettings = false }
                    )
                } else {
                    ThemeScreen(
                        onSettingsClick = { showSettings = true }
                    )
                }
            }
        }
    }
}



@Composable
fun ThemeScreen(
    viewModel: ThemeViewModel = viewModel(),
    onSettingsClick: () -> Unit
) {
    val appTheme by viewModel.appTheme.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Текущая тема: ${getThemeDisplayName(appTheme)}",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Пример карточки",
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = "Это демонстрация того, как тема влияет на цвета компонентов. " +
                            "Primary цвет: ${MaterialTheme.colorScheme.primary}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { /* Действие 1 */ },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Text("Кнопка 1")
            }

            OutlinedButton(
                onClick = { /* Действие 2 */ }
            ) {
                Text("Кнопка 2")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = { onSettingsClick() },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text("Настройки")
        }
    }
}

@Composable
fun ThemeOption(text: String, selected: Boolean, onSelect: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable(onClick = onSelect),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onSelect
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

private fun getThemeDisplayName(theme: AppTheme): String {
    return when (theme) {
        AppTheme.LIGHT -> "Светлая"
        AppTheme.DARK -> "Тёмная"
        AppTheme.SYSTEM -> "Системная"
    }
}

