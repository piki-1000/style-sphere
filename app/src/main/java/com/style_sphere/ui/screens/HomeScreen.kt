package com.style_sphere.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.style_sphere.data.ClothingItem
import com.style_sphere.data.OutfitLook
import com.style_sphere.data.UserProfile
import com.style_sphere.navigation.Screen
import com.style_sphere.util.base64ToBitmap

@Composable
fun HomeScreen(navController: NavController) {
    val purple = Color(0xFF7B5EA7)
    val yellow = Color(0xFFFFD600)
    val cream = Color(0xFFFFF8E1)
    val pink = Color(0xFFFFE4E9)
    val darkyellow = Color(0xFF7B3F00)
    val darkpink = Color(0xFFE75480)

    val categories = listOf(
        "T-shirts" to Color(0xFFD0C4E8),
        "Pants" to Color(0xFFC8D8C0),
        "Skirts" to Color(0xFFFFF0B0),
        "Dresses" to Color(0xFFB8D8E8),
        "Shorts" to Color(0xFFE8C8C0),
        "Shoes" to Color(0xFFF0D8E8)
    )

    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val uid = auth.currentUser?.uid

    var isLoading by remember { mutableStateOf(true) }
    // All the user's clothing items, grouped by category (for "My Clothes")
    var itemsByCategory by remember { mutableStateOf<Map<String, List<ClothingItem>>>(emptyMap()) }
    // The same items, keyed by their own ID (used to resolve a look's itemIds into real items)
    var itemsById by remember { mutableStateOf<Map<String, ClothingItem>>(emptyMap()) }
    // The user's saved outfit looks (for "My Looks")
    var looks by remember { mutableStateOf<List<OutfitLook>>(emptyList()) }

    // Profile info shown in the greeting header
    var username by remember { mutableStateOf("fashionista") }
    var profilePictureBase64 by remember { mutableStateOf("") }

    LaunchedEffect(uid) {
        if (uid == null) {
            isLoading = false
            return@LaunchedEffect
        }
        fetchClothingItems(db, uid) { items ->
            itemsByCategory = items.groupBy { it.category }
            itemsById = items.associateBy { it.id }
            isLoading = false
        }
        fetchOutfitLooks(db, uid) { fetchedLooks ->
            looks = fetchedLooks
        }
        db.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                val profile = doc.toObject(UserProfile::class.java)
                if (profile != null) {
                    username = profile.username
                    profilePictureBase64 = profile.profilePictureBase64
                }
            }
    }

    val profileBitmap = profilePictureBase64.takeIf { it.isNotBlank() }?.let { base64ToBitmap(it) }

    Scaffold(
        bottomBar = { BottomNavBar(navController = navController, current = "home") }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Hello,", fontSize = 22.sp, color = Color.Black)
                    Row {
                        Text(
                            username,
                            fontSize = 26.sp,
                            color = purple
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = "⭐", fontSize = 24.sp)
                    }
                }
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray, RoundedCornerShape(50))
                        .clickable { navController.navigate(Screen.Profile.route) },
                    contentAlignment = Alignment.Center
                ) {
                    if (profileBitmap != null) {
                        Image(
                            bitmap = profileBitmap.asImageBitmap(),
                            contentDescription = "Profile picture",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()

                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Cards row: Add new clothing + Create a new Look, side by side
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(110.dp)
                        .background(cream, RoundedCornerShape(20.dp))
                        .clickable { navController.navigate(Screen.AddClothingPhoto.route) }
                        .padding(16.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        "Add new\nclothing",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = darkyellow
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(110.dp)
                        .background(pink, RoundedCornerShape(20.dp))
                        .clickable { navController.navigate(Screen.OutfitRoulette.route) }
                        .padding(16.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        "Create a\nnew Look",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = darkpink
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))


            Column(
                modifier = Modifier.clickable {
                    navController.navigate(Screen.Closet.route)
                    navController.currentBackStackEntry
                        ?.savedStateHandle
                        ?.set("openTab", 1)
                }
            ) {
                Text(
                    "My Looks",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = yellow
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "By generator",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = purple
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (looks.isEmpty()) {
                    Text("No looks generated yet", color = Color.Gray, fontSize = 13.sp)
                } else {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(looks) { look ->
                            OutfitLookThumbnail(
                                look = look,
                                itemsById = itemsById,
                                modifier = Modifier.size(200.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "By me",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = purple
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(1) {
                        Text(
                            "Coming soon!",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}