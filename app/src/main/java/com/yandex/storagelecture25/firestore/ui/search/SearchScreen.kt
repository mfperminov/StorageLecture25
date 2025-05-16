package com.yandex.storagelecture25.firestore.ui.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yandex.storagelecture25.firestore.MusicViewModel
import com.yandex.storagelecture25.firestore.model.Album
import com.yandex.storagelecture25.firestore.model.Artist
import com.yandex.storagelecture25.firestore.model.Track
import com.yandex.storagelecture25.firestore.ui.main.TrackItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    musicViewModel: MusicViewModel,
    onNavigateUp: () -> Unit
) {
    val searchQuery by musicViewModel.searchQuery.collectAsState()
    val searchResults by musicViewModel.searchResults.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Поиск по медиатеке") },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { musicViewModel.setSearchQuery(it) },
                label = { Text("Введите запрос для поиска") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { musicViewModel.clearSearchQuery() }) {
                            Icon(Icons.Filled.Clear, contentDescription = "Очистить поиск")
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (searchQuery.isBlank()) {
                Text(
                    text = "Введите что-нибудь для начала поиска.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else if (searchResults.all { it.value.isEmpty() }) {
                Text(
                    text = "По вашему запросу ничего не найдено.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    searchResults.forEach { (category, items) ->
                        if (items.isNotEmpty()) {
                            item {
                                Text(category, style = MaterialTheme.typography.titleMedium)
                            }
                            items(items) { item ->
                                when (item) {
                                    is Artist -> ArtistSearchResultItem(artist = item)
                                    is Album -> AlbumSearchResultItem(album = item)
                                    is Track -> TrackItem(track = item) // Reusing from MusicScreen
                                }
                                HorizontalDivider()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ArtistSearchResultItem(artist: Artist) {
    ListItem(
        headlineContent = { Text(artist.name) },
        supportingContent = { Text("Жанр: ${artist.genre}, Страна: ${artist.country}") },
        overlineContent = { Text("Артист") }
    )
}

@Composable
fun AlbumSearchResultItem(album: Album) {
    // We might need artist name here, for now just album info
    // Consider fetching artist name if needed via viewmodel or passing more complete data
    ListItem(
        headlineContent = { Text(album.title) },
        supportingContent = { Text("Год выпуска: ${album.releaseYear}") },
        overlineContent = { Text("Альбом") }
        // leadingContent = { AsyncImage(...) } if you have cover art and want to show it
    )
} 