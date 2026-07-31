package com.style_sphere.data
data class Post(
    var id: String = "",
    val authorId: String = "",
    val authorUsername: String = "fashionista",
    val text: String = "",
    val timestamp: Long = System.currentTimeMillis()
)