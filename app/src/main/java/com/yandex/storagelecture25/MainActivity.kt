package com.yandex.storagelecture25

import android.os.Bundle
import android.util.Log
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.yandex.storagelecture25.firestore.MusicViewModel
import com.yandex.storagelecture25.firestore.ui.gallery.GalleryScreen
import com.yandex.storagelecture25.firestore.ui.main.MusicScreen
import com.yandex.storagelecture25.firestore.ui.search.SearchScreen
import com.yandex.storagelecture25.keystore.EncryptionScreen
import com.yandex.storagelecture25.secure.SecureStorageScreen
import com.yandex.storagelecture25.ui.theme.StorageLecture25Theme

class MainActivity : ComponentActivity() {

    private lateinit var musicViewModel: MusicViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase
        FirebaseApp.initializeApp(this)

        // Enable Firestore offline persistence
        val firestore = FirebaseFirestore.getInstance()
        val settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
            .build()
        firestore.firestoreSettings = settings
        Log.d("MainActivity", "Firestore offline persistence enabled.")

        musicViewModel = ViewModelProvider(this)[MusicViewModel::class.java]

        setContent {
            StorageLecture25Theme {
                var showEncryptionScreen by remember { mutableStateOf(false) }
                var showSecureStorageScreen by remember { mutableStateOf(false) }
                var showFirestoreLectureScreen by remember { mutableStateOf(false) }
                var showGalleryScreen by remember { mutableStateOf(false) }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    if (showEncryptionScreen) {
                        EncryptionScreen(
                            onNavigateBack = { showEncryptionScreen = false }
                        )
                    } else if (showSecureStorageScreen) {
                        SecureStorageScreen(
                            onNavigateBack = { showSecureStorageScreen = false }
                        )
                    } else if (showFirestoreLectureScreen) {
                        FirestoreLectureAppNavHost(
                            musicViewModel = musicViewModel,
                            onNavigateBackToMainMenu = { showFirestoreLectureScreen = false },
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

                            Button(
                                onClick = { showFirestoreLectureScreen = true },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Открыть Демо Firestore")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FirestoreLectureAppNavHost(
    musicViewModel: MusicViewModel,
    onNavigateBackToMainMenu: () -> Unit,
) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "musicMain") {
        composable("musicMain") {
            MusicScreen(
                musicViewModel = musicViewModel,
                onNavigateToSearch = { navController.navigate("musicSearch") },
                onNavigateBackHome = onNavigateBackToMainMenu,
                onNavigateToGallery = { navController.navigate("gallery") },
            )
        }
        composable("musicSearch") {
            SearchScreen(
                musicViewModel = musicViewModel,
                onNavigateUp = { navController.navigateUp() }
            )
        }
        composable("gallery") {
            GalleryScreen(
                musicViewModel = musicViewModel,
                onNavigateUp = { navController.navigateUp() }
            )
        }
    }
}

object ScreenRoutes {

    const val HOME = "home"
    const val FIRESTORE_MUSIC = "firestore_music"
    const val FIRESTORE_SEARCH = "firestore_search"
    const val FIRESTORE_GALLERY = "firestore_gallery"
    const val KEYSTORE_ENCRYPTION = "keystore_encryption"
    const val SECURE_STORAGE = "secure_storage"
}