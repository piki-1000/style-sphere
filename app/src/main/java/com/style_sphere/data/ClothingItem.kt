package com.style_sphere.data

data class ClothingItem(
    var id: String = "",
    val ownerId: String = "",
    val category: String = "",
    val color: String = "",
    val style: String = "",
    val tags: List<String> = emptyList(),
    val observations: String = "",
    val imageBase64: String = "",
    val createdAt: Long = System.currentTimeMillis()
)