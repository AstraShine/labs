package com.example.lab9.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lab9.data.AppTheme
import com.example.lab9.ui.theme.ThemeViewModel
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.res.stringResource


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: ThemeViewModel = viewModel(),
    onBack: () -> Unit
) {
    val appTheme by viewModel.appTheme.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Настройки темы") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                },
                actions = {} // если нужны дополнительные кнопки
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Выберите тему оформления",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Группа RadioButton для выбора темы
            Column {
                ThemeOption(
                    text = "Светлая",
                    selected = appTheme == AppTheme.LIGHT,
                    onSelect = { viewModel.setTheme(AppTheme.LIGHT) }
                )
                ThemeOption(
                    text = "Тёмная",
                    selected = appTheme == AppTheme.DARK,
                    onSelect = { viewModel.setTheme(AppTheme.DARK) }
                )
                ThemeOption(
                    text = "Системная",
                    selected = appTheme == AppTheme.SYSTEM,
                    onSelect = { viewModel.setTheme(AppTheme.SYSTEM) }
                )
            }
        }
    }
}

@Composable
private fun ThemeOption(text: String, selected: Boolean, onSelect: () -> Unit) {
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
