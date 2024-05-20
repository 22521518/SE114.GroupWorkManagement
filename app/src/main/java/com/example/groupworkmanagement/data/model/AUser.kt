package com.example.groupworkmanagement.data.model

import com.example.groupworkmanagement.utils.getToday


data class AUser(
    var uid: String="",
    var email: String="",
    var name: String="",
    var createdAt: String = getToday(),
    var imageUrl: String?=null,
) {
    fun toMap() = mapOf(
        "uid" to uid,
        "email" to email,
        "name" to name,
        "createAt" to createdAt,
        "imageUrl" to imageUrl,
    )
}

//val imageDefault = "https://firebasestorage.googleapis.com/v0/b/se114-mobile-intro-project.appspot.com/o/images%2F587ca680-d099-4613-8b5a-509604ce6b0f?alt=media&token=55fd7dab-cb9a-406c-93f2-921bf979fb05"
