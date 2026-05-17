<div align="center">
    
МИНИСТЕРСТВО НАУКИ И ВЫСШЕГО ОБРАЗОВАНИЯ РОССИЙСКОЙ ФЕДЕРАЦИИ ФЕДЕРАЛЬНОЕ ГОСУДАРСТВЕННОЕ БЮДЖЕТНОЕ ОБРАЗОВАТЕЛЬНОЕ УЧРЕЖДЕНИЕ ВЫСШЕГО ОБРАЗОВАНИЯ
"САХАЛИНСКИЙ ГОСУДАРСТВЕННЫЙ УНИВЕРСИТЕТ»

<br><br><br><br>

Институт естественных наук и техносферной безопасности

Кафедра информатики

Лапырёнок Анастасия

<br><br><br><br>

Лабораторная работа №9

«Сохранение настроек темы. Тёмная/светлая тема в Compose»

01.03.02 Прикладная математика и информатика

<br><br><br><br><br>

<div align="right">
Научный руководитель

Соболев Евгений Игоревич
</div>

<br><br><br><br><br>

г. Южно-Сахалинск
2026 г.

</div>

<br><br>

**Цель работы:** Изучить механизмы смены и сохранения темы приложения в Jetpack Compose, научиться использовать DataStore/SharedPreferences для хранения пользовательских настроек, реализовать переключение между тёмной и светлой темами.

<br><br>

## Листинг файла `Color.kt`
```kotlin
package com.example.lab9.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.material3.*

// Светлая тема
val LightColors = lightColorScheme(
    primary = Color(0xFF006C4C),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFF89F8C7),
    onPrimaryContainer = Color(0xFF002114),
    secondary = Color(0xFF4D635A),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFCFE9DD),
    onSecondaryContainer = Color(0xFF0A1F19),
    tertiary = Color(0xFF3A637A),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFC1E8FF),
    onTertiaryContainer = Color(0xFF001E2C),
    background = Color(0xFFF4FBF5),
    onBackground = Color(0xFF161D1A),
    surface = Color(0xFFF4FBF5),
    onSurface = Color(0xFF161D1A)
)

// Тёмная тема
val DarkColors = darkColorScheme(
    primary = Color(0xFF6CDBB0),
    onPrimary = Color(0xFF003825),
    primaryContainer = Color(0xFF005239),
    onPrimaryContainer = Color(0xFF89F8C7),
    secondary = Color(0xFFB3CCC1),
    onSecondary = Color(0xFF1F352D),
    secondaryContainer = Color(0xFF354B43),
    onSecondaryContainer = Color(0xFFCFE9DD),
    tertiary = Color(0xFF9DC9E5),
    onTertiary = Color(0xFF003549),
    tertiaryContainer = Color(0xFF1F4B63),
    onTertiaryContainer = Color(0xFFC1E8FF),
    background = Color(0xFF161D1A),
    onBackground = Color(0xFFE1E3DF),
    surface = Color(0xFF161D1A),
    onSurface = Color(0xFFE1E3DF)
)
```

<br><br>

## Листинг файла `Theme.kt`
```kotlin
package com.example.lab9.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lab9.data.AppTheme


@Composable
fun ThemeSwitcherTheme(
    viewModel: ThemeViewModel = viewModel(),
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val appTheme by viewModel.appTheme.collectAsState()

    // Определяем, должна ли быть тёмная тема
    val isDarkTheme = when (appTheme) {
        AppTheme.DARK -> true
        AppTheme.LIGHT -> false
        AppTheme.SYSTEM -> isSystemInDarkTheme()
    }

    // Динамические цвета доступны на Android 12+
    val colorScheme = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (isDarkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        isDarkTheme -> DarkColors
        else -> LightColors
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !isDarkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background // <-- фон для всего экрана
        ) {
            content()
        }
    }
}
```

<br><br>

## Листинг файла `SettingsManager.kt`

```kotlin
package com.example.lab9.data

import android.content.Context
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "settings")

// Тип для хранения темы
enum class AppTheme {
    LIGHT, DARK, SYSTEM
}

class SettingsManager(private val context: Context) {

    companion object {
        val THEME_MODE_KEY = intPreferencesKey("theme_mode")
    }

    val appTheme: Flow<AppTheme> = context.dataStore.data
        .map { preferences ->
            val themeValue = preferences[THEME_MODE_KEY] ?: 2 // по умолчанию — системная тема
            AppTheme.values()[themeValue]
        }

    suspend fun saveTheme(theme: AppTheme) {
        context.dataStore.edit { preferences ->
            preferences[THEME_MODE_KEY] = theme.ordinal
        }
    }
}
```

<br><br>

## Листинг файла `ThemeViewModel.kt`

```kotlin
package com.example.lab9.ui.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lab9.data.AppTheme
import com.example.lab9.data.SettingsManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ThemeViewModel(
    private val settingsManager: SettingsManager
) : ViewModel() {

    val appTheme: StateFlow<AppTheme> = settingsManager.appTheme
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppTheme.SYSTEM
        )

    fun setTheme(theme: AppTheme) {
        viewModelScope.launch {
            settingsManager.saveTheme(theme)
        }
    }
}
```

<br><br>

## Листинг файла `MainActivity.kt`

```kotlin
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
```

<br><br>

## Листинг файла `SettingsScreen.kt`

```kotlin
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
```

<br><br>

## Скриншот приложения с отображением результатов
![My Image](images/Screen.jpg)

<br><br>

## Ответы на контрольные вопросы:

#### 1. Как в Compose определить, какая тема активна в данный момент (тёмная/светлая)?

В Jetpack Compose определить текущую тему (тёмную или светлую) можно с помощью функции `isSystemInDarkTheme()`. Она возвращает `true`, если система использует тёмную тему, и `false` — если светлую.

**Пример использования:**
```kotlin
@Composable
fun MyApp() {
    val isDarkTheme = isSystemInDarkTheme()

    MyTheme(darkTheme = isDarkTheme) {
        // Содержимое приложения
    }
}
```

**Важные нюансы:**
* Функция опирается на системные настройки темы устройства.
* Если пользователь вручную выбрал тему в приложении (не следуя системным настройкам), `isSystemInDarkTheme()` не отразит этот выбор — потребуется дополнительная логика для учёта пользовательских предпочтений.
* Для корректной работы убедитесь, что в проекте подключены необходимые зависимости Compose и Material 3.

---

#### 2. Что такое `MaterialTheme.colorScheme` и какие основные цвета он содержит?

`MaterialTheme.colorScheme` — это объект в Jetpack Compose (в рамках Material Design 3), который хранит набор цветов для текущего цветового решения темы. Он позволяет централизованно управлять цветами во всём приложении и автоматически переключаться между светлой и тёмной темами.

**Основные цвета в `colorScheme` (Material 3):**

* `primary` — основной акцентный цвет приложения. Используется для ключевых элементов интерфейса (кнопок, иконок и т. д.).
* `onPrimary` — цвет текста и иконок на элементах основного цвета.
* `secondary` — вторичный акцентный цвет. Применяется для менее важных элементов.
* `onSecondary` — цвет текста/иконок на вторичных элементах.
* `tertiary` — третичный акцентный цвет (новый в Material 3). Используется для дополнительных акцентов.
* `onTertiary` — цвет текста/иконок на третичных элементах.
* `background` — цвет фона контейнера (например, `Scaffold`).
* `onBackground` — цвет текста на фоне контейнера.
* `surface` — цвет поверхностей (карточек, меню и т. п.).
* `onSurface` — цвет текста/иконок на поверхностях.
* `error` — цвет для отображения ошибок.
* `onError` — цвет текста/иконок в элементах с цветом ошибки.

**Пример доступа к цветам:**
```kotlin
Text(
    text = "Пример текста",
    color = MaterialTheme.colorScheme.primary
)
```

---

#### 3. Как сохранить выбор темы пользователя между сессиями работы приложения?

Чтобы сохранить выбор темы между сессиями, нужно:

1. **Сохранить предпочтение пользователя** (тёмная, светлая или системная тема) в постоянном хранилище. Лучший вариант — `DataStore` (современная замена `SharedPreferences`).
2. **При запуске приложения** прочитать сохранённое значение и применить соответствующую тему.

**Пошаговая реализация:**

**Шаг 1. Создайте класс для работы с настройками (например, `ThemePreferences`):**
```kotlin
class ThemePreferences(private val dataStore: DataStore<Preferences>) {

    companion object {
        val THEME_KEY = stringPreferencesKey("app_theme")
    }

    val themeFlow: Flow<String> = dataStore.data
        .map { preferences ->
            preferences[THEME_KEY] ?: "system" // По умолчанию — следовать системе
        }

    suspend fun saveTheme(theme: String) {
        dataStore.edit { preferences ->
            preferences[THEME_KEY] = theme
        }
    }
}
```

**Шаг 2. В главном композабле (`MainActivity`) прочитайте сохранённую тему:**
```kotlin
@Composable
fun MyApp(themePreferences: ThemePreferences) {
    val selectedTheme by themePreferences.themeFlow.collectAsState(initial = "system")

    val isDarkTheme = when (selectedTheme) {
        "dark" -> true
        "light" -> false
        else -> isSystemInDarkTheme() // "system"
    }

    MyTheme(darkTheme = isDarkTheme) {
        // Содержимое приложения
    }
}
```

**Шаг 3. Предоставьте пользователю UI для выбора темы** (например, в настройках) и вызовите `themePreferences.saveTheme("dark")` или `"light"`.

---

#### 4. В чём разница между `isSystemInDarkTheme()` и сохранённым пользовательским выбором?

Разница заключается в источнике данных и приоритете:

| Параметр | `isSystemInDarkTheme()` | Сохранённый пользовательский выбор |
|--------|-----------------------|-------------------------------|
| **Источник** | Настройки ОС устройства | Локальное хранилище приложения (`DataStore`, `SharedPreferences`) |
| **Что отражает** | Текущий режим темы системы (тёмный/светлый) | Явный выбор пользователя внутри приложения |
| **Приоритет** | Низкий (базовое значение) | Высокий (переопределяет систему) |
| **Пример** | Пользователь включил тёмную тему в настройках Android | Пользователь в настройках приложения выбрал «Всегда светлая тема» |
| **Зависимость от пользователя** | Нет (автоматически) | Да (требует действия пользователя) |

**Практический вывод:**
* Используйте `isSystemInDarkTheme()` как **значение по умолчанию**, если пользователь ещё не делал выбора.
* Используйте сохранённый выбор как **основной источник истины**, если пользователь явно указал предпочтительную тему.

---

#### 5. Что такое динамические цвета (dynamic color) и на каких версиях Android они доступны?

**Динамические цвета** (`Dynamic Color`, также известные как `Monet`) — функция Android 12 (API 31) и выше, которая автоматически генерирует цветовую палитру для приложения на основе обоев пользователя. Система анализирует доминирующие цвета на обоях и создаёт гармоничную схему из 5 основных тонов (A1–A5), которые можно применить к элементам интерфейса.

**Ключевые особенности:**
* **Автоматизация.** Не нужно вручную подбирать цвета — система делает это за вас.
* **Персонализация.** Интерфейс каждого пользователя уникален, так как основан на его обоях.
* **Интеграция с Material 3.** Динамические цвета полностью поддерживаются в `MaterialTheme` Compose.
* **Гармония.** Цвета подобраны по правилам Material Design, обеспечивая доступность и читаемость.

**Как включить динамические цвета в Compose:**

1. Убедитесь, что приложение таргетит **Android 12+ (API 31)**.
2. В теме приложения (обычно в `MyTheme.kt`) проверьте флаг `useDynamicColor`:
```kotlin
@Composable
fun MyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    useDynamicColor: Boolean = true, // Включить динамические цвета
    content: @Composable () -> Unit
) {
    val dynamicColorScheme = when {
        useDynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(LocalContext.current)
            else dynamicLightColorScheme(LocalContext.current)
        }
        darkTheme -> darkColorScheme()
        else -> lightColorScheme()
    }

    MaterialTheme(
        colorScheme = dynamicColorScheme,
        content = content
    )
}
```

**Версии Android:**
* **Доступно:** Android 12 (API 31) и новее.
* **Недоступно:** Android 11 и более ранние версии. В этом случае приложение использует стандартную цветовую схему (`lightColorScheme` / `darkColorScheme`).

**Важно:** Для работы динамических цветов необходимо, чтобы на устройстве были установлены обои с достаточной цветовой палитрой. Если обои монохромные или слишком простые, система может использовать цветовую схему по умолчанию.

## Вывод

В ходе работы успешно реализованы механизмы смены и сохранения темы приложения в Jetpack Compose. Достигнуты все поставленные цели:

1. **Изучены механизмы управления темами** в Jetpack Compose:
* для определения системной темы используется функция `isSystemInDarkTheme()`;
* переключение между темами реализовано через условную логику в `Theme.kt`, учитывающую выбор пользователя и настройки системы;
* обеспечена поддержка трёх режимов: светлая тема, тёмная тема и режим «системная» (следование настройкам ОС).

2. **Освоено хранение пользовательских настроек** с применением `DataStore` (современной альтернативы `SharedPreferences`):
* создан класс `SettingsManager`, отвечающий за сохранение и чтение настроек темы;
* настройки сохраняются между сессиями работы приложения;
* данные хранятся в виде целочисленного значения (`ordinal` enum‑класса `AppTheme`), что упрощает сериализацию.

3. **Реализовано переключение между тёмной и светлой темами**:
* определены цветовые схемы для светлой (`LightColors`) и тёмной (`DarkColors`) тем в файле `Color.kt` в соответствии с принципами Material Design 3;
* реализована поддержка динамических цветов (Monet) для устройств на Android 12+ (`dynamicLightColorScheme` / `dynamicDarkColorScheme`);
* цвета автоматически применяются ко всем компонентам интерфейса через `MaterialTheme.colorScheme`.

4. **Создан пользовательский интерфейс для выбора темы**:
* экран настроек (`SettingsScreen.kt`) предоставляет три варианта выбора (светлая, тёмная, системная тема) через группу `RadioButton`;
* текущее состояние темы отображается на основном экране (`ThemeScreen.kt`);
* интерфейс реагирует на смену темы без перезапуска приложения.

5. **Обеспечена интеграция компонентов**:
* `ThemeViewModel` выступает связующим звеном между UI и слоем данных, управляет состоянием темы и передаёт его в Composable‑функции;
* `MainActivity` инициализирует `SettingsManager` и передаёт его в ViewModel через фабрику `ThemeViewModelFactory`;
* состояние темы синхронизируется между экранами и сохраняется при навигации.

---

**Итог**: разработанное решение демонстрирует современный подход к управлению темами в Jetpack Compose. Оно сочетает удобство для пользователя (гибкие настройки, динамические цвета), надёжность хранения данных (`DataStore`) и соответствие рекомендациям Material Design 3. Код структурирован, разделён на логические слои (данные, логика, UI) и готов к дальнейшему расширению (например, добавлением других пользовательских настроек).