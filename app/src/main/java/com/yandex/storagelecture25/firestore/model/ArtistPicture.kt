package com.yandex.storagelecture25.firestore.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.*

data class ArtistPicture(
    @DocumentId val id: String = "",
    val artistId: String = "",
    val imageUrl: String = "",
    val description: String? = null,
    val isPrimary: Boolean = false,
    @ServerTimestamp val uploadedAt: Date? = null
) 