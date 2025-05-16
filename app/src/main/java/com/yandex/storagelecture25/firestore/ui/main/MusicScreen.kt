package com.yandex.storagelecture25.firestore.ui.main

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.yandex.storagelecture25.firestore.MusicViewModel
import com.yandex.storagelecture25.firestore.model.Album
import com.yandex.storagelecture25.firestore.model.Artist
import com.yandex.storagelecture25.firestore.model.ArtistPicture
import com.yandex.storagelecture25.firestore.model.Track
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicScreen(
    musicViewModel: MusicViewModel = viewModel(),
    onNavigateToSearch: () -> Unit,
    onNavigateToGallery: () -> Unit,
    onNavigateBackHome: (() -> Unit)? = null
) {
    val artists by musicViewModel.artists.collectAsState()
    val albums by musicViewModel.albums.collectAsState()
    val tracks by musicViewModel.tracks.collectAsState()
    val artistPictures by musicViewModel.artistPictures.collectAsState()
    val selectedArtistId by musicViewModel.selectedArtistId.collectAsState()
    val selectedAlbumId by musicViewModel.selectedAlbumId.collectAsState()

    val selectedArtist = artists.find { it.id == selectedArtistId }
    val selectedAlbum = albums.find { it.id == selectedAlbumId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Firestore Музыка") },
                navigationIcon = {
                    onNavigateBackHome?.let {
                        IconButton(onClick = it) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "На главный экран")
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { musicViewModel.addSampleData() }) {
                        Icon(Icons.Filled.Add, contentDescription = "Добавить тестовые данные")
                    }
                    IconButton(onClick = onNavigateToSearch) {
                        Icon(Icons.Filled.Search, contentDescription = "Поиск")
                    }
                    IconButton(onClick = onNavigateToGallery) {
                        Icon(Icons.Filled.Favorite, contentDescription = "Галерея")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                ArtistSelector(artists, selectedArtist) {
                    musicViewModel.selectArtist(it?.id)
                }
            }

            if (selectedArtist != null) {
                item {
                    ArtistDetailsSection(selectedArtist, artistPictures)
                }

                item {
                    AlbumSelector(albums, selectedAlbum, selectedArtist) {
                        musicViewModel.selectAlbum(it?.id)
                    }
                }

                if (selectedAlbum != null) {
                    item {
                        TrackListSection(selectedAlbum, tracks)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtistSelector(
    artists: List<Artist>,
    selectedArtist: Artist?,
    onArtistSelected: (Artist?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text("Исполнители", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedArtist?.name ?: "Выберите исполнителя",
                onValueChange = {}, // Not directly editable
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                artists.forEach { artist ->
                    DropdownMenuItem(
                        text = { Text(artist.name) },
                        onClick = {
                            onArtistSelected(artist)
                            expanded = false
                        }
                    )
                }
                if (artists.isEmpty()) {
                    DropdownMenuItem(
                        text = { Text("Нет доступных исполнителей") },
                        onClick = { expanded = false },
                        enabled = false
                    )
                }
            }
        }
    }
}

@Composable
fun ArtistDetailsSection(artist: Artist, pictures: List<ArtistPicture>) {
    Column {
        Text("Об исполнителе", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        Card {
            Column(Modifier.padding(16.dp)) {
                artist.imageUrl?.let { imageUrl ->
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(imageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = artist.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp) // Adjust height as needed
                            .clip(MaterialTheme.shapes.medium),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(Modifier.height(16.dp))
                }
                Text("Имя: ${artist.name}", style = MaterialTheme.typography.bodyLarge)
                Text("Жанр: ${artist.genre}", style = MaterialTheme.typography.bodyMedium)
                Text("Страна: ${artist.country}", style = MaterialTheme.typography.bodyMedium)
                artist.createdAt?.let {
                    Text(
                        "Добавлен: ${SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(it)}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                if (pictures.isNotEmpty()) {
                    Spacer(Modifier.height(8.dp))
                    Text("Фотографии:", style = MaterialTheme.typography.titleSmall)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(pictures) { picture ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(picture.imageUrl)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = picture.description ?: artist.name,
                                    modifier = Modifier
                                        .size(120.dp)
                                        .clip(MaterialTheme.shapes.medium)
                                        .border(1.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.medium),
                                    contentScale = ContentScale.Crop
                                )
                                picture.description?.let {
                                    Text(it, fontSize = 10.sp, textAlign = TextAlign.Center, modifier = Modifier.width(120.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumSelector(
    albums: List<Album>,
    selectedAlbum: Album?,
    selectedArtist: Artist, // Added to provide context even if albums list is empty initially
    onAlbumSelected: (Album?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text("Альбомы (${selectedArtist.name})", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))

        if (albums.isEmpty() && selectedAlbum == null) {
            Text("Нет альбомов для выбранного исполнителя.", style = MaterialTheme.typography.bodyMedium)
            return
        }

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedAlbum?.title ?: "Выберите альбом",
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                albums.forEach { album ->
                    DropdownMenuItem(
                        text = { Text("${album.title} (${album.releaseYear})") },
                        onClick = {
                            onAlbumSelected(album)
                            expanded = false
                        }
                    )
                }
                if (albums.isEmpty()) { // Should ideally not be reached if the above check works
                    DropdownMenuItem(
                        text = { Text("Нет доступных альбомов") },
                        onClick = { expanded = false },
                        enabled = false
                    )
                }
            }
        }
    }
}

@Composable
fun TrackListSection(album: Album, tracks: List<Track>) {
    Column {
        Text("Треки альбома: ${album.title}", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        if (tracks.isEmpty()) {
            Text("Нет треков в этом альбоме.", style = MaterialTheme.typography.bodyMedium)
        } else {
            Card {
                LazyColumn(
                    modifier = Modifier
                        .padding(8.dp)
                        .heightIn(max = 300.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(tracks) { track ->
                        TrackItem(track)
                    }
                }
            }
        }
    }
}

@Composable
fun TrackItem(track: Track) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(track.title, style = MaterialTheme.typography.bodyLarge)
            Text("Жанр: ${track.genre}", style = MaterialTheme.typography.bodySmall)
        }
        Text("${track.durationSeconds / 60}:${String.format("%02d", track.durationSeconds % 60)}")
    }
} 