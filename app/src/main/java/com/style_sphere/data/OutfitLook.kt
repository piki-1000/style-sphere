package com.style_sphere.data

data class OutfitLook(
    var id: String = "",
    val ownerId: String = "",
    val itemIds: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
)