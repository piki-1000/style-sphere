package com.style_sphere.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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

@Composable
fun ClosetScreen(navController: NavController) {
    val purple = Color(0xFF7B5EA7)
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("My Clothes", "My Looks")

    val categories = listOf(
        "T-shirts" to Color(0xFFD0C4E8),
        "Pants" to Color(0xFFC8D8C0),
        "Skirts" to Color(0xFFFFF0B0),
        "Dresses" to Color(0xFFB8D8E8),
        "Coats" to Color(0xFFE8C8C0)
    )

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

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                if (selectedTab == 0) {
                    items(categories) { (name, color) ->
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
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(4) {
                                    Box(
                                        modifier = Modifier
                                            .size(72.dp)
                                            .background(color, RoundedCornerShape(12.dp))
                                    )
                                }
                            }
                            TextButton(onClick = {}) {
                                Text("See more", color = Color.Gray, fontSize = 12.sp)
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
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(3) {
                                Box(
                                    modifier = Modifier
                                        .size(100.dp)
                                        .background(Color(0xFFD0C4E8), RoundedCornerShape(12.dp))
                                )
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
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(3) {
                                Box(
                                    modifier = Modifier
                                        .size(100.dp)
                                        .background(Color(0xFFFFF0B0), RoundedCornerShape(12.dp))
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}