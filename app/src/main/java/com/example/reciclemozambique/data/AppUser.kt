package com.example.reciclemozambique.data

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class AppUser(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val photoUrl: String? = null,
    @ServerTimestamp val createdAt: Date? = null
)
