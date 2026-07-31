package com.style_sphere.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore
import com.style_sphere.data.ClothingItem
import com.style_sphere.data.OutfitLook
import com.style_sphere.util.base64ToBitmap

@Composable
fun ClothingItemImage(
    item: ClothingItem?,
    modifier: Modifier = Modifier,
    placeholderColor: Color = Color(0xFFEDEDED),
    placeholderLabel: String = "",
    onClick: (() -> Unit)? = null
) {
    val bitmap = item?.imageBase64?.let { base64ToBitmap(it) }
    val clickableModifier = if (onClick != null) modifier.clickable(onClick = onClick) else modifier
    Box(
        modifier = clickableModifier.background(placeholderColor, RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = item?.category ?: placeholderLabel,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else if (placeholderLabel.isNotBlank()) {
            Text(placeholderLabel, fontSize = 11.sp, color = Color.Gray)
        }
    }
}

@Composable
fun OutfitLookThumbnail(
    look: OutfitLook,
    itemsById: Map<String, ClothingItem>,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val items = look.itemIds.mapNotNull { itemsById[it] }
    val clickableModifier = if (onClick != null) modifier.clickable(onClick = onClick) else modifier
    Column(
        modifier = clickableModifier
            .background(Color(0xFFF3F3F3), RoundedCornerShape(12.dp))
            .padding(4.dp),
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        items.forEach { item ->
            ClothingItemImage(
                item = item,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
        }
    }
}

fun fetchClothingItems(
    db: FirebaseFirestore,
    uid: String,
    onResult: (List<ClothingItem>) -> Unit
) {
    db.collection("clothingItems")
        .whereEqualTo("ownerId", uid)
        .get()
        .addOnSuccessListener { result ->
            val items = result.documents.mapNotNull { doc ->
                doc.toObject(ClothingItem::class.java)?.apply { id = doc.id }
            }
            onResult(items)
        }
        .addOnFailureListener { onResult(emptyList()) }
}

fun fetchOutfitLooks(
    db: FirebaseFirestore,
    uid: String,
    onResult: (List<OutfitLook>) -> Unit
) {
    db.collection("looks")
        .whereEqualTo("ownerId", uid)
        .get()
        .addOnSuccessListener { result ->
            val looks = result.documents.mapNotNull { doc ->
                doc.toObject(OutfitLook::class.java)?.apply { id = doc.id }
            }
            onResult(looks)
        }
        .addOnFailureListener { onResult(emptyList()) }
}