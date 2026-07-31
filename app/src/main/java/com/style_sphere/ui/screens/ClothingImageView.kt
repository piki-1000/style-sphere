package com.style_sphere.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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

/**
 * Shows one clothing item's photo (decoded from its stored base64 string).
 * If there's no item, or it has no photo yet, shows a soft colored
 * placeholder box instead - so the UI never just shows a blank gap.
 */
@Composable
fun ClothingItemImage(
    item: ClothingItem?,
    modifier: Modifier = Modifier,
    placeholderColor: Color = Color(0xFFEDEDED),
    placeholderLabel: String = ""
) {
    val bitmap = item?.imageBase64?.let { base64ToBitmap(it) }
    Box(
        modifier = modifier.background(placeholderColor, RoundedCornerShape(12.dp)),
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

/**
 * A compact vertical stack of an outfit look's item photos - a mini version
 * of the roulette screen's column layout, sized to fit inside a small grid
 * thumbnail (e.g. the 100dp boxes on Home/Closet).
 *
 * `itemsById` is a lookup (item ID -> ClothingItem) built once from the
 * user's full clothing collection, used to turn the look's stored item IDs
 * back into real items with real photos.
 */
@Composable
fun OutfitLookThumbnail(
    look: OutfitLook,
    itemsById: Map<String, ClothingItem>,
    modifier: Modifier = Modifier
) {
    val items = look.itemIds.mapNotNull { itemsById[it] }
    Column(
        modifier = modifier
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

/** Fetches every clothing item the given user owns. */
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

/** Fetches every outfit look the given user has saved. */
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