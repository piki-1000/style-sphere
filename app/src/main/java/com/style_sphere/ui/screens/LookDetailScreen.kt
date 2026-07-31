package com.style_sphere.ui.screens

import androidx.compose.foundation.layout.*
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
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.style_sphere.data.ClothingItem
import com.style_sphere.data.OutfitLook

@Composable
fun LookDetailScreen(navController: NavController, lookId: String) {
    val purple = Color(0xFF7B5EA7)
    val db = FirebaseFirestore.getInstance()

    var look by remember { mutableStateOf<OutfitLook?>(null) }
    var items by remember { mutableStateOf<List<ClothingItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var isDeleting by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(lookId) {
        db.collection("looks").document(lookId).get()
            .addOnSuccessListener { doc ->
                val fetchedLook = doc.toObject(OutfitLook::class.java)?.apply { id = doc.id }
                if (fetchedLook == null) {
                    isLoading = false
                    errorMessage = "This look couldn't be found."
                    return@addOnSuccessListener
                }
                look = fetchedLook

                if (fetchedLook.itemIds.isEmpty()) {
                    isLoading = false
                    return@addOnSuccessListener
                }

                db.collection("clothingItems")
                    .whereIn(FieldPath.documentId(), fetchedLook.itemIds)
                    .get()
                    .addOnSuccessListener { result ->
                        items = result.documents.mapNotNull { itemDoc ->
                            itemDoc.toObject(ClothingItem::class.java)?.apply { id = itemDoc.id }
                        }
                        isLoading = false
                    }
                    .addOnFailureListener { e ->
                        isLoading = false
                        errorMessage = e.localizedMessage ?: "Failed to load this look's items."
                    }
            }
            .addOnFailureListener { e ->
                isLoading = false
                errorMessage = e.localizedMessage ?: "Failed to load this look."
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
            Text("Look", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = purple)
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = purple)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        when {
            isLoading -> {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = purple)
                }
            }
            errorMessage != null -> {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text(errorMessage ?: "", color = Color.Red, fontSize = 14.sp)
                }
            }
            else -> {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    items.forEach { item ->
                        ClothingItemImage(
                            item = item,
                            modifier = Modifier
                                .fillMaxWidth(0.7f)
                                .weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = {
                        isDeleting = true
                        db.collection("looks").document(lookId).delete()
                            .addOnSuccessListener { navController.popBackStack() }
                            .addOnFailureListener { e ->
                                isDeleting = false
                                errorMessage = e.localizedMessage ?: "Failed to delete this look."
                            }
                    },
                    enabled = !isDeleting,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (isDeleting) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                    } else {
                        Icon(Icons.Filled.Delete, contentDescription = null, tint = Color.Red)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Delete this look", color = Color.Red)
                    }
                }
            }
        }
    }
}