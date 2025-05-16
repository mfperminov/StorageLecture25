package com.yandex.storagelecture25.firestore.model

import com.google.firebase.firestore.DocumentId

data class Track(
    @DocumentId val id: String = "",
    val albumId: String = "",
    val artistId: String = "",
    val title: String = "",
    val durationSeconds: Int = 0,
    val genre: String = "",
)