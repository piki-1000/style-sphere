package com.style_sphere.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.ArrowBack
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
import com.style_sphere.data.NewClothingDraft
import com.style_sphere.navigation.Screen
import com.style_sphere.util.bitmapToBase64

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddClothingDetailsScreen(navController: NavController) {
    val coral = Color(0xFFFF7A6E)
    val purple = Color(0xFF7B5EA7)

    val categories = listOf("T-shirts", "Pants", "Skirts", "Dresses", "Shorts", "Shoes")
    val colors = listOf("Red", "Blue", "Black", "White", "Pink", "Purple", "Yellow", "Green", "Multicolor")
    val styles = listOf("Casual", "Formal", "Sporty", "Streetwear", "Elegant")

    var category by remember { mutableStateOf("") }
    var color by remember { mutableStateOf("") }
    var style by remember { mutableStateOf("") }
    var tagInput by remember { mutableStateOf("") }
    var tags by remember { mutableStateOf(listOf<String>()) }
    var observations by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSaving by remember { mutableStateOf(false) }

    val photo = NewClothingDraft.photoBitmap
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

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
            Text(
                "Describe it",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = coral
            )
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = coral)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Category dropdown
        DropdownField(label = "Category", options = categories, selected = category, onSelected = { category = it }, accent = coral)

        Spacer(modifier = Modifier.height(12.dp))

        // Color dropdown
        DropdownField(label = "Color", options = colors, selected = color, onSelected = { color = it }, accent = coral)

        Spacer(modifier = Modifier.height(12.dp))

        // Style dropdown
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

        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            if (photo != null) {
                Image(
                    bitmap = photo.asImageBitmap(),
                    contentDescription = "Selected photo",
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.LightGray, RoundedCornerShape(8.dp))
                )
                Spacer(modifier = Modifier.width(12.dp))
            } else {
                Icon(Icons.Filled.AddPhotoAlternate, contentDescription = null, tint = coral)
                Spacer(modifier = Modifier.width(8.dp))
            }

            TextButton(onClick = { navController.popBackStack() }) {
                Text(
                    if (photo != null) "Change photo" else "Add photo",
                    color = coral,
                    fontSize = 13.sp
                )
            }
        }

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(errorMessage ?: "", color = Color.Red, fontSize = 13.sp)
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                errorMessage = null
                val uid = auth.currentUser?.uid

                if (uid == null) {
                    errorMessage = "You need to be signed in to save an item."
                    return@Button
                }
                if (photo == null) {
                    errorMessage = "Please add a photo first."
                    return@Button
                }
                if (category.isBlank() || color.isBlank() || style.isBlank()) {
                    errorMessage = "Please fill in category, color, and style."
                    return@Button
                }

                isSaving = true
                val imageBase64 = bitmapToBase64(photo)

                val item = ClothingItem(
                    ownerId = uid,
                    category = category,
                    color = color,
                    style = style,
                    tags = tags,
                    observations = observations,
                    imageBase64 = imageBase64
                )

                db.collection("clothingItems")
                    .add(item)
                    .addOnSuccessListener {
                        isSaving = false
                        NewClothingDraft.clear()
                        navController.navigate(Screen.Closet.route) {
                            popUpTo(Screen.Home.route)
                        }
                    }
                    .addOnFailureListener { e ->
                        isSaving = false
                        errorMessage = e.localizedMessage ?: "Failed to save item."
                    }
            },
            enabled = !isSaving,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(containerColor = coral)
        ) {
            if (isSaving) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
            } else {
                Text("Done", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DropdownField(
    label: String,
    options: List<String>,
    selected: String,
    onSelected: (String) -> Unit,
    accent: Color
) {
    var expanded by remember { mutableStateOf(false) }

    Text(label, color = accent, fontSize = 13.sp)
    Spacer(modifier = Modifier.height(4.dp))
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            placeholder = { Text("Select") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            shape = RoundedCornerShape(50),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = accent,
                unfocusedBorderColor = accent.copy(alpha = 0.5f)
            )
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}