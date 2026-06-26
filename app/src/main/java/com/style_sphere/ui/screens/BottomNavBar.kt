package com.style_sphere.ui.screens

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.style_sphere.navigation.Screen

@Composable
fun BottomNavBar(navController: NavController, current: String) {
    val purple = Color(0xFF7B5EA7)
    val gray = Color.Gray

    NavigationBar(containerColor = Color.White) {
        NavigationBarItem(
            selected = current == "home",
            onClick = { navController.navigate(Screen.Home.route) },
            icon = { Text("🏠", fontSize = 20.sp) },
            label = { Text("Home") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = purple,
                selectedTextColor = purple,
                indicatorColor = Color(0xFFEDE7F6)
            )
        )
        NavigationBarItem(
            selected = current == "forum",
            onClick = { navController.navigate(Screen.Forum.route) },
            icon = { Text("👥", fontSize = 20.sp) },
            label = { Text("Forum") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = purple,
                selectedTextColor = purple,
                indicatorColor = Color(0xFFEDE7F6)
            )
        )
        NavigationBarItem(
            selected = current == "closet",
            onClick = { navController.navigate(Screen.Closet.route) },
            icon = { Text("👗", fontSize = 20.sp) },
            label = { Text("My Closet") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = purple,
                selectedTextColor = purple,
                indicatorColor = Color(0xFFEDE7F6)
            )
        )
        NavigationBarItem(
            selected = current == "profile",
            onClick = { navController.navigate(Screen.Profile.route) },
            icon = { Text("👤", fontSize = 20.sp) },
            label = { Text("Profile") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = purple,
                selectedTextColor = purple,
                indicatorColor = Color(0xFFEDE7F6)
            )
        )
    }
}