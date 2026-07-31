package com.style_sphere.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import com.style_sphere.data.ClothingItem
import com.style_sphere.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditClothingItemScreen(navController: NavController, itemId: String) {
    val coral = Color(0xFFFF7A6E)

    val categories = listOf("T-shirts", "Pants", "Skirts", "Dresses", "Shorts", "Shoes")
    val colors = listOf("Red", "Blue", "Black", "White", "Pink", "Purple", "Yellow", "Green", "Multicolor")
    val styles = listOf("Casual", "Formal", "Sporty", "Streetwear", "Elegant")

    val db = FirebaseFirestore.getInstance()

    var originalItem by remember { mutableStateOf<ClothingItem?>(null) }
    var category by remember { mutableStateOf("") }
    var color by remember { mutableStateOf("") }
    var style by remember { mutableStateOf("") }
    var tagInput by remember { mutableStateOf("") }
    var tags by remember { mutableStateOf(listOf<String>()) }
    var observations by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var isSaving by remember { mutableStateOf(false) }
    var isDeleting by remember { mutableStateOf(false) }

    // Fetch the existing item once, then pre-fill every field from it.
    LaunchedEffect(itemId) {
        db.collection("clothingItems").document(itemId).get()
            .addOnSuccessListener { doc ->
                val item = doc.toObject(ClothingItem::class.java)?.apply { id = doc.id }
                if (item == null) {
                    errorMessage = "This item couldn't be found."
                } else {
                    originalItem = item
                    category = item.category
                    color = item.color
                    style = item.style
                    tags = item.tags
                    observations = item.observations
                }
                isLoading = false
            }
            .addOnFailureListener { e ->
                isLoading = false
                errorMessage = e.localizedMessage ?: "Failed to load this item."
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Edit item", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = coral)
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = coral)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        when {
            isLoading -> {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = coral)
                }
            }
            originalItem == null -> {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text(errorMessage ?: "Item not found.", color = Color.Red, fontSize = 14.sp)
                }
            }
            else -> {
                ClothingItemImage(
                    item = originalItem,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                DropdownField(label = "Category", options = categories, selected = category, onSelected = { category = it }, accent = coral)

                Spacer(modifier = Modifier.height(12.dp))

                DropdownField(label = "Color", options = colors, selected = color, onSelected = { color = it }, accent = coral)

                Spacer(modifier = Modifier.height(12.dp))

                DropdownField(label = "Style", options = styles, selected = style, onSelected = { style = it }, accent = coral)

                Spacer(modifier = Modifier.height(12.dp))

                Text("Tags", color = coral, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = tagInput,
                    onValueChange = { tagInput = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(50),
                    singleLine = true,
                    trailingIcon = {
                        IconButton(onClick = {
                            if (tagInput.isNotBlank()) {
                                tags = tags + tagInput.trim()
                                tagInput = ""
                            }
                        }) {
                            Text("+", color = coral, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = coral,
                        unfocusedBorderColor = coral.copy(alpha = 0.5f)
                    )
                )

                if (tags.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(tags) { tag ->
                            Box(
                                modifier = Modifier
                                    .background(coral.copy(alpha = 0.15f), RoundedCornerShape(50))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(tag, fontSize = 12.sp, color = coral)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text("Obs.", color = coral, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = observations,
                    onValueChange = { observations = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = coral,
                        unfocusedBorderColor = coral.copy(alpha = 0.5f)
                    )
                )

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(errorMessage ?: "", color = Color.Red, fontSize = 13.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            isDeleting = true
                            db.collection("clothingItems").document(itemId).delete()
                                .addOnSuccessListener {
                                    isDeleting = false
                                    navController.popBackStack()
                                }
                                .addOnFailureListener { e ->
                                    isDeleting = false
                                    errorMessage = e.localizedMessage ?: "Failed to delete this item."
                                }
                        },
                        enabled = !isDeleting && !isSaving,
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        shape = RoundedCornerShape(50)
                    ) {
                        if (isDeleting) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Filled.Delete, contentDescription = null, tint = Color.Red)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Delete", color = Color.Red)
                        }
                    }

                    Button(
                        onClick = {
                            val current = originalItem ?: return@Button
                            errorMessage = null

                            if (category.isBlank() || color.isBlank() || style.isBlank()) {
                                errorMessage = "Please fill in category, color, and style."
                                return@Button
                            }

                            isSaving = true
                            // Keep the original ownerId/imageBase64 - only the fields
                            // edited on this screen should actually change.
                            val updatedItem = current.copy(
                                category = category,
                                color = color,
                                style = style,
                                tags = tags,
                                observations = observations
                            )

                            db.collection("clothingItems").document(itemId).set(updatedItem)
                                .addOnSuccessListener {
                                    isSaving = false
                                    navController.popBackStack()
                                }
                                .addOnFailureListener { e ->
                                    isSaving = false
                                    errorMessage = e.localizedMessage ?: "Failed to save changes."
                                }
                        },
                        enabled = !isSaving && !isDeleting,
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(containerColor = coral)
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                        } else {
                            Text("Save", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}