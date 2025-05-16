package com.yandex.storagelecture25.firestore

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.yandex.storagelecture25.firestore.model.Album
import com.yandex.storagelecture25.firestore.model.Artist
import com.yandex.storagelecture25.firestore.model.ArtistPicture
import com.yandex.storagelecture25.firestore.model.Track
import kotlinx.coroutines.tasks.await

object SampleDataProvider {

    private const val TAG = "SampleDataProvider"
    private const val ARTISTS_COLLECTION = "artists"
    private const val ALBUMS_COLLECTION = "albums"
    private const val TRACKS_COLLECTION = "tracks"
    private const val ARTIST_PICTURES_COLLECTION = "artistPictures"

    suspend fun addSampleData(db: FirebaseFirestore) {
        val artistsCollection = db.collection(ARTISTS_COLLECTION)
        val existingData = artistsCollection.limit(1).get().await()
        if (!existingData.isEmpty) {
            Log.d(TAG, "Sample data likely already exists. Skipping generation.")
            return
        }

        Log.d(TAG, "Adding sample data...")

        val kinoImageUrl = "https://upload.wikimedia.org/wikipedia/commons/c/c9/Kino1986leningrad_%28cropped_2%29.jpg"
        val alisaImageUrl =
            "https://upload.wikimedia.org/wikipedia/commons/d/d9/Группа_%22Алиса%22_в_Москве_во_время_тура_ХХХ.jpg"
        val kishImageUrl =
            "https://upload.wikimedia.org/wikipedia/ru/6/6e/Король_и_шут.jpg"

        val artistKino = Artist(name = "Кино", genre = "Рок", country = "СССР", imageUrl = kinoImageUrl)
        val artistAlisa = Artist(name = "Алиса", genre = "Рок", country = "СССР/Россия", imageUrl = alisaImageUrl)
        val artistKish = Artist(name = "Король и шут", genre = "Панк-рок", country = "Россия", imageUrl = kishImageUrl)

        try {
            val artistKinoRef = db.collection(ARTISTS_COLLECTION).add(artistKino).await()
            val artistAlisaRef = db.collection(ARTISTS_COLLECTION).add(artistAlisa).await()
            val artistKishRef = db.collection(ARTISTS_COLLECTION).add(artistKish).await()
            Log.d(TAG, "Sample artists added.")

            val albumKino1 = Album(artistId = artistKinoRef.id, title = "Группа крови", releaseYear = 1988)
            val albumKino2 = Album(
                artistId = artistKinoRef.id,
                title = "Звезда по имени Солнце",
                releaseYear = 1989,
                coverImageUrl = "https://upload.wikimedia.org/wikipedia/ru/thumb/1/13/%D0%97%D0%B2%D0%B5%D0%B7%D0%B4%D0%B0_%D0%BF%D0%BE_%D0%B8%D0%BC%D0%B5%D0%BD%D0%B8_%D0%A1%D0%BE%D0%BB%D0%BD%D1%86%D0%B5.jpg/220px-%D0%97%D0%B2%D0%B5%D0%B7%D0%B4%D0%B0_%D0%BF%D0%BE_%D0%B8%D0%BC%D0%B5%D0%BD%D0%B8_%D0%A1%D0%BE%D0%BB%D0%BD%D1%86%D0%B5.jpg"
            )
            val albumKino1Ref = db.collection(ALBUMS_COLLECTION).add(albumKino1).await()
            val albumKino2Ref = db.collection(ALBUMS_COLLECTION).add(albumKino2).await()

            db.collection(TRACKS_COLLECTION).add(
                Track(
                    albumId = albumKino1Ref.id,
                    artistId = artistKinoRef.id,
                    title = "Группа крови",
                    durationSeconds = 285,
                    genre = "Рок"
                )
            ).await()
            db.collection(TRACKS_COLLECTION).add(
                Track(
                    albumId = albumKino1Ref.id,
                    artistId = artistKinoRef.id,
                    title = "Закрой за мной дверь, я ухожу",
                    durationSeconds = 255,
                    genre = "Рок"
                )
            ).await()
            db.collection(TRACKS_COLLECTION).add(
                Track(
                    albumId = albumKino1Ref.id,
                    artistId = artistKinoRef.id,
                    title = "Война",
                    durationSeconds = 244,
                    genre = "Рок"
                )
            ).await()

            db.collection(TRACKS_COLLECTION).add(
                Track(
                    albumId = albumKino2Ref.id,
                    artistId = artistKinoRef.id,
                    title = "Песня без слов",
                    durationSeconds = 306,
                    genre = "Рок"
                )
            ).await()
            db.collection(TRACKS_COLLECTION).add(
                Track(
                    albumId = albumKino2Ref.id,
                    artistId = artistKinoRef.id,
                    title = "Звезда по имени Солнце",
                    durationSeconds = 225,
                    genre = "Рок"
                )
            ).await()
            db.collection(TRACKS_COLLECTION).add(
                Track(
                    albumId = albumKino2Ref.id,
                    artistId = artistKinoRef.id,
                    title = "Пачка сигарет",
                    durationSeconds = 268,
                    genre = "Рок"
                )
            ).await()

            val albumAlisa1 = Album(artistId = artistAlisaRef.id, title = "Энергия", releaseYear = 1985)
            val albumAlisa2 = Album(artistId = artistAlisaRef.id, title = "Блок ада", releaseYear = 1987)
            val albumAlisa1Ref = db.collection(ALBUMS_COLLECTION).add(albumAlisa1).await()
            val albumAlisa2Ref = db.collection(ALBUMS_COLLECTION).add(albumAlisa2).await()

            db.collection(TRACKS_COLLECTION).add(
                Track(
                    albumId = albumAlisa1Ref.id,
                    artistId = artistAlisaRef.id,
                    title = "Меломан",
                    durationSeconds = 200,
                    genre = "Рок"
                )
            ).await()
            db.collection(TRACKS_COLLECTION).add(
                Track(
                    albumId = albumAlisa1Ref.id,
                    artistId = artistAlisaRef.id,
                    title = "Мы вместе",
                    durationSeconds = 220,
                    genre = "Рок"
                )
            ).await()

            db.collection(TRACKS_COLLECTION).add(
                Track(
                    albumId = albumAlisa2Ref.id,
                    artistId = artistAlisaRef.id,
                    title = "Красное на чёрном",
                    durationSeconds = 250,
                    genre = "Рок"
                )
            ).await()
            db.collection(TRACKS_COLLECTION).add(
                Track(
                    albumId = albumAlisa2Ref.id,
                    artistId = artistAlisaRef.id,
                    title = "Время менять имена",
                    durationSeconds = 230,
                    genre = "Рок"
                )
            ).await()

            val albumKish1 = Album(artistId = artistKishRef.id, title = "Камнем по голове", releaseYear = 1996)
            val albumKish2 = Album(artistId = artistKishRef.id, title = "Жаль, нет ружья", releaseYear = 2002)
            val albumKish1Ref = db.collection(ALBUMS_COLLECTION).add(albumKish1).await()
            val albumKish2Ref = db.collection(ALBUMS_COLLECTION).add(albumKish2).await()

            db.collection(TRACKS_COLLECTION).add(
                Track(
                    albumId = albumKish1Ref.id,
                    artistId = artistKishRef.id,
                    title = "Дурак и молния",
                    durationSeconds = 180,
                    genre = "Панк-рок"
                )
            ).await()
            db.collection(TRACKS_COLLECTION).add(
                Track(
                    albumId = albumKish1Ref.id,
                    artistId = artistKishRef.id,
                    title = "Лесник",
                    durationSeconds = 200,
                    genre = "Панк-рок"
                )
            ).await()

            db.collection(TRACKS_COLLECTION).add(
                Track(
                    albumId = albumKish2Ref.id,
                    artistId = artistKishRef.id,
                    title = "Мёртвый анархист",
                    durationSeconds = 240,
                    genre = "Панк-рок"
                )
            ).await()
            db.collection(TRACKS_COLLECTION).add(
                Track(
                    albumId = albumKish2Ref.id,
                    artistId = artistKishRef.id,
                    title = "Проклятый старый дом",
                    durationSeconds = 260,
                    genre = "Панк-рок"
                )
            ).await()

            db.collection(ARTIST_PICTURES_COLLECTION).add(
                ArtistPicture(
                    artistId = artistKinoRef.id,
                    imageUrl = kinoImageUrl,
                    description = "Виктор Цой на концерте",
                    isPrimary = true
                )
            ).await()
            db.collection(ARTIST_PICTURES_COLLECTION).add(
                ArtistPicture(
                    artistId = artistAlisaRef.id,
                    imageUrl = alisaImageUrl,
                    description = "Алиса",
                    isPrimary = true
                )
            ).await()
            db.collection(ARTIST_PICTURES_COLLECTION).add(
                ArtistPicture(
                    artistId = artistKishRef.id,
                    imageUrl = kishImageUrl,
                    description = "Король и Шут",
                    isPrimary = true
                )
            ).await()

            Log.d(TAG, "Sample data addition process completed.")

        } catch (e: Exception) {
            Log.e(TAG, "Error adding sample data", e)
        }
    }
} 