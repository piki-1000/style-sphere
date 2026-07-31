package com.style_sphere.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.style_sphere.data.ClothingItem
import com.style_sphere.data.OutfitLook
import com.style_sphere.util.base64ToBitmap

private data class OutfitSlot(val label: String, val categories: List<String>)

private val outfitSlots = listOf(
    OutfitSlot("Top", listOf("T-shirts")),
    OutfitSlot("Bottom", listOf("Skirts", "Pants", "Shorts")),
    OutfitSlot("Shoes", listOf("Shoes"))
)

@Composable
fun OutfitRouletteScreen(navController: NavController) {
    val purple = Color(0xFF7B5EA7)
    val yellow = Color(0xFFFFD600)
    val slotColors = listOf(
        Color(0xFFE0DFF6), Color(0xFFDFF0F5), Color(0xFFFFF3D6)
    )

    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val uid = auth.currentUser?.uid

    var itemsByCategory by remember { mutableStateOf<Map<String, List<ClothingItem>>>(emptyMap()) }
    var isLoading by remember { mutableStateOf(true) }


    val currentSelection = remember { mutableStateMapOf<String, ClothingItem?>() }

    val lockedSlots = remember { mutableStateListOf<String>() }

    var isSaving by remember { mutableStateOf(false) }
    var saveMessage by remember { mutableStateOf<String?>(null) }

    fun randomItemFor(slot: OutfitSlot): ClothingItem? {
        val pooledItems = slot.categories.flatMap { itemsByCategory[it].orEmpty() }
        return pooledItems.randomOrNull()
    }

    LaunchedEffect(uid) {
        if (uid == null) {
            isLoading = false
            return@LaunchedEffect
        }
        db.collection("clothingItems")
            .whereEqualTo("ownerId", uid)
            .get()
            .addOnSuccessListener { result ->
                val items = result.documents.mapNotNull { doc ->
                    doc.toObject(ClothingItem::class.java)?.apply { id = doc.id }
                }
                itemsByCategory = items.groupBy { it.category }
                // Do the first random draw once items are loaded
                outfitSlots.forEach { slot ->
                    currentSelection[slot.label] = randomItemFor(slot)
                }
                isLoading = false
            }
            .addOnFailureListener {
                isLoading = false
            }
    }

    fun rerollSlot(slot: OutfitSlot) {
        currentSelection[slot.label] = randomItemFor(slot)
    }

    fun generateAgain() {
        outfitSlots.forEach { slot ->
            if (slot.label !in lockedSlots) {
                rerollSlot(slot)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("New", fontSize = 24.sp, color = Color.Black)
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text("Look", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = purple)

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = purple)
            }
        } else {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                outfitSlots.forEachIndexed { index, slot ->
                    OutfitSlotCard(
                        slot = slot,
                        item = currentSelection[slot.label],
                        backgroundColor = slotColors[index % slotColors.size],
                        isLocked = slot.label in lockedSlots,
                        onToggleLock = {
                            if (slot.label in lockedSlots) lockedSlots.remove(slot.label) else lockedSlots.add(slot.label)
                        },
                        onClear = { currentSelection[slot.label] = null },
                        onReroll = { rerollSlot(slot) },
                        modifier = Modifier.fillMaxWidth(0.75f)
                    )
                }
            }
        }

        if (saveMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(saveMessage ?: "", color = purple, fontSize = 13.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ActionButton(icon = Icons.Filled.Edit, label = "Make My Own", tint = purple) {
                // TODO: navigate to the manual outfit-building screen once it exists
            }
            ActionButton(icon = Icons.Filled.Tune, label = "Preferences", tint = purple) {
                // TODO: outfit generation preferences (future work)
            }
            ActionButton(icon = Icons.Filled.Refresh, label = "Generate Again", tint = purple) {
                generateAgain()
            }
            ActionButton(icon = Icons.Filled.Favorite, label = "Save", tint = purple) {
                if (uid == null) {
                    saveMessage = "You need to be signed in to save a look."
                    return@ActionButton
                }
                val selectedIds = outfitSlots.mapNotNull { currentSelection[it.label]?.id }
                if (selectedIds.isEmpty()) {
                    saveMessage = "Add at least one item before saving."
                    return@ActionButton
                }

                isSaving = true
                val look = OutfitLook(ownerId = uid, itemIds = selectedIds)
                db.collection("looks")
                    .add(look)
                    .addOnSuccessListener {
                        isSaving = false
                        saveMessage = "Look saved!"
                    }
                    .addOnFailureListener { e ->
                        isSaving = false
                        saveMessage = e.localizedMessage ?: "Failed to save look."
                    }
            }
        }
    }
}

@Composable
private fun OutfitSlotCard(
    slot: OutfitSlot,
    item: ClothingItem?,
    backgroundColor: Color,
    isLocked: Boolean,
    onToggleLock: () -> Unit,
    onClear: () -> Unit,
    onReroll: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(backgroundColor, RoundedCornerShape(16.dp))
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            contentAlignment = Alignment.Center
        ) {
            val bitmap = item?.imageBase64?.let { base64ToBitmap(it) }
            if (bitmap != null) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = slot.label,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Text(slot.label, fontSize = 12.sp, color = Color.Gray)
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onToggleLock, modifier = Modifier.size(28.dp)) {
                Icon(
                    if (isLocked) Icons.Filled.Lock else Icons.Filled.LockOpen,
                    contentDescription = "Lock this item",
                    tint = Color.DarkGray,
                    modifier = Modifier.size(16.dp)
                )
            }
            IconButton(onClick = onClear, modifier = Modifier.size(28.dp)) {
                Icon(
                    Icons.Filled.Close,
                    contentDescription = "Clear this slot",
                    tint = Color.DarkGray,
                    modifier = Modifier.size(16.dp)
                )
            }
            IconButton(onClick = onReroll, modifier = Modifier.size(28.dp)) {
                Icon(
                    Icons.Filled.Refresh,
                    contentDescription = "Re-roll this slot",
                    tint = Color.DarkGray,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun ActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    tint: Color,
    onClick: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(onClick = onClick) {
            Icon(icon, contentDescription = label, tint = tint)
        }
        Text(label, fontSize = 10.sp, color = Color.Gray)
    }
}