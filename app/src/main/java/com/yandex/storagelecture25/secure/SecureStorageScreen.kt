package com.yandex.storagelecture25.secure

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.yandex.storagelecture25.network.ApiClient
import kotlinx.coroutines.launch

@Composable
fun SecureStorageScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val secureStorage = remember { SecureStorageManager(context) }
    val token: String? = remember {
        secureStorage.getToken()
    }
    var tokenInput by remember { mutableStateOf(token.orEmpty()) }
    var savedToken by remember { mutableStateOf(token.orEmpty()) }
    var requestResult by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Безопасное хранилище",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        TextField(
            value = tokenInput,
            onValueChange = { tokenInput = it },
            label = { Text("Введите токен для сохранения") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                secureStorage.saveToken(tokenInput)
                savedToken = tokenInput
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Сохранить токен")
        }

        if (savedToken.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        "Сохраненный токен:",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        savedToken,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Button(
                onClick = {
                    scope.launch {
                        isLoading = true
                        try {
                            val response = ApiClient.service.getPost("Bearer $savedToken")
                            requestResult = """
                                Заголовки запроса:
                                Authorization: Bearer $savedToken
                                
                                Ответ:
                                ${response.body()}
                            """.trimIndent()
                        } catch (e: Exception) {
                            requestResult = "Ошибка: ${e.message}"
                        } finally {
                            isLoading = false
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Выполнить запрос")
                }
            }

            if (requestResult.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            "Детали запроса:",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            requestResult,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onNavigateBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Назад на главный экран")
        }
    }
} 