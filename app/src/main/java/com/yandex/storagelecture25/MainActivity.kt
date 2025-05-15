package com.yandex.storagelecture25

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yandex.storagelecture25.keystore.EncryptionScreen
import com.yandex.storagelecture25.secure.SecureStorageScreen
import com.yandex.storagelecture25.ui.theme.StorageLecture25Theme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StorageLecture25Theme {
                var showEncryptionScreen by remember { mutableStateOf(false) }
                var showSecureStorageScreen by remember { mutableStateOf(false) }
                
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    if (showEncryptionScreen) {
                        EncryptionScreen(
                            onNavigateBack = { showEncryptionScreen = false }
                        )
                    } else if (showSecureStorageScreen) {
                        SecureStorageScreen(
                            onNavigateBack = { showSecureStorageScreen = false }
                        )
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Button(
                                onClick = { showEncryptionScreen = true },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Open Encryption Screen")
                            }

                            Button(
                                onClick = { showSecureStorageScreen = true },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Open Secure Storage Screen")
                            }
                        }
                    }
                }
            }
        }
    }
}