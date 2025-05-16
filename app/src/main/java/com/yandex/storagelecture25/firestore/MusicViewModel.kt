package com.yandex.storagelecture25.firestore

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObjects
import com.google.firebase.ktx.Firebase
import com.yandex.storagelecture25.firestore.model.Album
import com.yandex.storagelecture25.firestore.model.Artist
import com.yandex.storagelecture25.firestore.model.ArtistPicture
import com.yandex.storagelecture25.firestore.model.Track
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MusicViewModel : ViewModel() {

    private val db = Firebase.firestore

    private val _artists = MutableStateFlow<List<Artist>>(emptyList())
    val artists: StateFlow<List<Artist>> = _artists.asStateFlow()

    private val _albums = MutableStateFlow<List<Album>>(emptyList())
    val albums: StateFlow<List<Album>> = _albums.asStateFlow()

    private val _tracks = MutableStateFlow<List<Track>>(emptyList())
    val tracks: StateFlow<List<Track>> = _tracks.asStateFlow()

    private val _artistPictures = MutableStateFlow<List<ArtistPicture>>(emptyList())
    val artistPictures: StateFlow<List<ArtistPicture>> = _artistPictures.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _allAlbumsForSearch = MutableStateFlow<List<Album>>(emptyList())
    private val _allTracksForSearch = MutableStateFlow<List<Track>>(emptyList())
    private val _allArtistPictures = MutableStateFlow<List<ArtistPicture>>(emptyList())
    val allArtistPictures: StateFlow<List<ArtistPicture>> = _allArtistPictures.asStateFlow()

    val searchResults: StateFlow<Map<String, List<Any>>> =
        combine(_searchQuery, artists, _allAlbumsForSearch, _allTracksForSearch) { query, artistsList, albumsList, tracksList ->
            val results = mutableMapOf<String, List<Any>>()
            if (query.isNotBlank()) {
                val lowerCaseQuery = query.lowercase()
                results["Артисты"] = artistsList.filter {
                    it.name.lowercase().contains(lowerCaseQuery) ||
                            it.genre.lowercase().contains(lowerCaseQuery) ||
                            it.country.lowercase().contains(lowerCaseQuery)
                }
                results["Альбомы"] = albumsList.filter {
                    it.title.lowercase().contains(lowerCaseQuery)
                }
                results["Треки"] = tracksList.filter {
                    it.title.lowercase().contains(lowerCaseQuery) ||
                            it.genre.lowercase().contains(lowerCaseQuery)
                }
            }
            results
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyMap()
        )

    private val _selectedArtistId = MutableStateFlow<String?>(null)
    val selectedArtistId: StateFlow<String?> = _selectedArtistId.asStateFlow()

    private val _selectedAlbumId = MutableStateFlow<String?>(null)
    val selectedAlbumId: StateFlow<String?> = _selectedAlbumId.asStateFlow()

    companion object {
        private const val TAG = "MusicViewModel"
        private const val ARTISTS_COLLECTION = "artists"
        private const val ALBUMS_COLLECTION = "albums"
        private const val TRACKS_COLLECTION = "tracks"
        private const val ARTIST_PICTURES_COLLECTION = "artistPictures"
    }

    init {
        fetchArtistsRealtime()
        fetchAllAlbumsForSearchRealtime()
        fetchAllTracksForSearchRealtime()
        fetchAllArtistPicturesRealtime()
        addSampleData() // Call to add sample data
    }

    fun selectArtist(artistId: String?) {
        _selectedArtistId.value = artistId
        if (artistId != null) {
            fetchAlbumsForArtistRealtime(artistId)
            fetchArtistPicturesRealtime(artistId)
            _tracks.value = emptyList()
            _selectedAlbumId.value = null
        } else {
            _albums.value = emptyList()
            _tracks.value = emptyList()
            _artistPictures.value = emptyList()
            _selectedAlbumId.value = null
        }
    }

    fun selectAlbum(albumId: String?) {
        _selectedAlbumId.value = albumId
        if (albumId != null && _selectedArtistId.value != null) {
            fetchTracksForAlbumRealtime(albumId)
        } else {
            _tracks.value = emptyList()
        }
    }

    fun addArtist(artist: Artist) {
        viewModelScope.launch {
            try {
                db.collection(ARTISTS_COLLECTION).add(artist).await()
                Log.d(TAG, "Artist added successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Error adding artist", e)
            }
        }
    }

    fun addAlbum(album: Album) {
        viewModelScope.launch {
            try {
                db.collection(ALBUMS_COLLECTION).add(album).await()
                Log.d(TAG, "Album added successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Error adding album", e)
            }
        }
    }

    fun addTrack(track: Track) {
        viewModelScope.launch {
            try {
                db.collection(TRACKS_COLLECTION).add(track).await()
                Log.d(TAG, "Track added successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Error adding track", e)
            }
        }
    }

    fun addArtistPicture(artistPicture: ArtistPicture) {
        viewModelScope.launch {
            try {
                db.collection(ARTIST_PICTURES_COLLECTION).add(artistPicture).await()
                Log.d(TAG, "Artist picture added successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Error adding artist picture", e)
            }
        }
    }

    private fun fetchArtistsRealtime() {
        db.collection(ARTISTS_COLLECTION)
            .orderBy("name")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    _artists.value = emptyList()
                    return@addSnapshotListener
                }

                val artistList = snapshots?.mapNotNull { it.toObject(Artist::class.java) } ?: emptyList()
                _artists.value = artistList
                Log.d(TAG, "Fetched artists: ${artistList.size}")
            }
    }

    private fun fetchAlbumsForArtistRealtime(artistId: String) {
        db.collection(ALBUMS_COLLECTION)
            .whereEqualTo("artistId", artistId)
            .orderBy("releaseYear")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed for albums.", e)
                    _albums.value = emptyList()
                    return@addSnapshotListener
                }
                val albumList = snapshots?.mapNotNull { it.toObject(Album::class.java) } ?: emptyList()
                _albums.value = albumList
                Log.d(TAG, "Fetched albums for $artistId: ${albumList.size}")
            }
    }

    private fun fetchTracksForAlbumRealtime(albumId: String) {
        viewModelScope.launch {
            val albumTracks = db.collection(TRACKS_COLLECTION)
                .whereEqualTo("albumId", albumId)
                .get()
                .await()
                .toObjects<Track>()
        }
        db.collection(TRACKS_COLLECTION)
            .whereEqualTo("albumId", albumId)
            .orderBy("title")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    _tracks.value = emptyList()
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.metadata.hasPendingWrites()) {
                    // локальные изменения
                }
                val trackList = snapshot?.mapNotNull { it.toObject(Track::class.java) } ?: emptyList()
                _tracks.value = trackList
            }
    }

    private fun fetchArtistPicturesRealtime(artistId: String) {
        db.collection(ARTIST_PICTURES_COLLECTION)
            .whereEqualTo("artistId", artistId)
            .orderBy("uploadedAt")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed for artist pictures.", e)
                    _artistPictures.value = emptyList()
                    return@addSnapshotListener
                }
                val pictureList = snapshots?.mapNotNull { it.toObject(ArtistPicture::class.java) } ?: emptyList()
                _artistPictures.value = pictureList
                Log.d(TAG, "Fetched artist pictures for $artistId: ${pictureList.size}")
            }
    }

    private fun fetchAllAlbumsForSearchRealtime() {
        db.collection(ALBUMS_COLLECTION)
            .orderBy("title")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed for all albums search.", e)
                    _allAlbumsForSearch.value = emptyList()
                    return@addSnapshotListener
                }
                val albumList = snapshots?.mapNotNull { it.toObject(Album::class.java) } ?: emptyList()
                _allAlbumsForSearch.value = albumList
                Log.d(TAG, "Fetched all albums for search: ${albumList.size}")
            }
    }

    private fun fetchAllTracksForSearchRealtime() {
        db.collection(TRACKS_COLLECTION)
            .orderBy("title")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed for all tracks search.", e)
                    _allTracksForSearch.value = emptyList()
                    return@addSnapshotListener
                }
                val trackList = snapshots?.mapNotNull { it.toObject(Track::class.java) } ?: emptyList()
                _allTracksForSearch.value = trackList
                Log.d(TAG, "Fetched all tracks for search: ${trackList.size}")
            }
    }

    private fun fetchAllArtistPicturesRealtime() {
        db.collection(ARTIST_PICTURES_COLLECTION)
            .orderBy("uploadedAt")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed for all artist pictures.", e)
                    _allArtistPictures.value = emptyList()
                    return@addSnapshotListener
                }
                val pictureList = snapshots?.mapNotNull { it.toObject(ArtistPicture::class.java) } ?: emptyList()
                _allArtistPictures.value = pictureList
                Log.d(TAG, "Fetched all artist pictures: ${pictureList.size}")
            }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun clearSearchQuery() {
        _searchQuery.value = ""
    }

    fun addSampleData() {
        viewModelScope.launch {
            SampleDataProvider.addSampleData(db)
        }
    }
} 