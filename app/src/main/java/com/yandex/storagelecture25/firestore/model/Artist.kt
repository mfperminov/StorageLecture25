package com.yandex.storagelecture25.firestore.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.*

data class Artist(
    @DocumentId val id: String = "",
    val name: String = "",
    val genre: String = "",
    val country: String = "",
    val imageUrl: String? = null,
    @ServerTimestamp val createdAt: Date? = null
) 