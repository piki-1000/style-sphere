package com.style_sphere.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.style_sphere.data.ClothingItem
import com.style_sphere.data.OutfitLook
import com.style_sphere.navigation.Screen

@Composable
fun ClosetScreen(navController: NavController) {
    val purple = Color(0xFF7B5EA7)

    val openTab = navController.currentBackStackEntry
        ?.savedStateHandle
        ?.get<Int>("openTab")

    var selectedTab by remember { mutableStateOf(openTab ?: 0) }
    val tabs = listOf("My Clothes", "My Looks")

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
    var itemsByCategory by remember { mutableStateOf<Map<String, List<ClothingItem>>>(emptyMap()) }
    var itemsById by remember { mutableStateOf<Map<String, ClothingItem>>(emptyMap()) }
    var looks by remember { mutableStateOf<List<OutfitLook>>(emptyList()) }

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
    }

    Scaffold(
        bottomBar = { BottomNavBar(navController = navController, current = "closet") }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            TabRow(selectedTabIndex = selectedTab, containerColor = Color.White) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                title,
                                color = if (selectedTab == index) purple else Color.Gray
                            )
                        }
                    )
                }
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = purple)
                }
                return@Column
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                if (selectedTab == 0) {
                    items(categories) { (name, color) ->
                        val categoryItems = itemsByCategory[name].orEmpty()
                        Column(modifier = Modifier.padding(bottom = 16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    name,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = purple
                                )
                                Icon(
                                    imageVector = Icons.Filled.FilterList,
                                    contentDescription = "filter",
                                    tint = Color.Gray
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            if (categoryItems.isEmpty()) {
                                ClothingItemImage(
                                    item = null,
                                    placeholderColor = color,
                                    placeholderLabel = "No $name yet",
                                    modifier = Modifier.size(72.dp)
                                )
                            } else {
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    items(categoryItems.take(4)) { item ->
                                        ClothingItemImage(
                                            item = item,
                                            placeholderColor = color,
                                            modifier = Modifier.size(72.dp),
                                            onClick = {
                                                navController.navigate(
                                                    Screen.EditClothingItem.createRoute(item.id)
                                                )
                                            }
                                        )
                                    }
                                }
                            }
                            if (categoryItems.size > 4) {
                                TextButton(onClick = {}) {
                                    Text("See more", color = Color.Gray, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                } else {
                    item {
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
                                        modifier = Modifier.size(100.dp),
                                        onClick = {
                                            navController.navigate(
                                                Screen.LookDetail.createRoute(look.id)
                                            )
                                        }
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "By me",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFD600)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        // "Make My Own" isn't built yet, so there's nothing real
                        // to show here yet - once that screen saves its own
                        // OutfitLooks, this section can filter/fetch those too.
                        Text("Coming soon", color = Color.Gray, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}