package com.style_sphere.data

data class UserProfile(
    var uid: String = "",
    val username: String = "fashionista",
    val profilePictureBase64: String = "",
    val bio: String = ""
)