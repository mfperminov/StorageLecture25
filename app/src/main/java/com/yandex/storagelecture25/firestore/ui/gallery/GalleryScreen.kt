package com.yandex.storagelecture25.firestore.ui.gallery

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.yandex.storagelecture25.firestore.MusicViewModel
import com.yandex.storagelecture25.firestore.model.ArtistPicture

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryScreen(
    musicViewModel: MusicViewModel,
    onNavigateUp: () -> Unit
) {
    val allPictures by musicViewModel.allArtistPictures.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Галерея русского рока") },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (allPictures.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                Text("В галерее пока нет фотографий.", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 128.dp),
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .padding(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(allPictures, key = { it.id }) { picture ->
                    GalleryItem(picture)
                }
            }
        }
    }
}

@Composable
fun GalleryItem(picture: ArtistPicture) {
    Card(
        modifier = Modifier
            .aspectRatio(1f) // Makes items square
            .clip(MaterialTheme.shapes.medium)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(picture.imageUrl)
                .crossfade(true)
                .build(),
            contentDescription = picture.description,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
} 