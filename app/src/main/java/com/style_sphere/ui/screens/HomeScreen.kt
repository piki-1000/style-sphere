package com.style_sphere.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.style_sphere.navigation.Screen

@Composable
fun HomeScreen(navController: NavController) {
    val purple = Color(0xFF7B5EA7)
    val yellow = Color(0xFFFFD600)
    val cream = Color(0xFFFFF8E1)
    val pink = Color(0xFFFFE4E9)

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
                    Text(
                        "Rebeka",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = yellow
                    )
                }
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(Color.LightGray, RoundedCornerShape(50))
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Card Add new clothing
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .background(cream, RoundedCornerShape(20.dp))
                    .clickable { navController.navigate(Screen.Closet.route) }
                    .padding(24.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    "Add new\nclothing",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Card Create a new Look
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .background(pink, RoundedCornerShape(20.dp))
                    .clickable { navController.navigate(Screen.Closet.route) }
                    .padding(24.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    "Create a\nnew Look",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }
    }
}