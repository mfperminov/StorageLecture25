package com.yandex.storagelecture25.firestore.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.*

data class Album(
    @DocumentId val id: String = "",
    val artistId: String = "",
    val title: String = "",
    val releaseYear: Int = 0,
    val coverImageUrl: String? = null,
    @ServerTimestamp val createdAt: Date? = null
) 