package com.style_sphere.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.style_sphere.navigation.Screen

@Composable
fun ProfileScreen(navController: NavController) {
    val purple = Color(0xFF7B5EA7)
    val yellow = Color(0xFFFFD600)
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Posts", "Media", "Likes")

    Scaffold(
        bottomBar = { BottomNavBar(navController = navController, current = "profile") }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Text("←", fontSize = 24.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.Top) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(Color(0xFFFFE4E9), RoundedCornerShape(12.dp))
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    TextButton(onClick = {}) { Text("⚙ Settings", color = Color.Gray) }
                    TextButton(onClick = {}) { Text("✎ Edit Profile", color = Color.Gray) }
                    TextButton(onClick = {
                        navController.navigate(Screen.SignIn.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }) { Text("→ Logout", color = Color.Gray) }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text("Rebeka", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = yellow)
            Row {
                Text("Birthday: ", fontSize = 14.sp)
                Text("26/05/2006", fontSize = 14.sp, color = purple, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("201", fontWeight = FontWeight.Bold)
                    Text("friends", fontSize = 12.sp, color = Color.Gray)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("200", fontWeight = FontWeight.Bold)
                    Text("following", fontSize = 12.sp, color = Color.Gray)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("100", fontWeight = FontWeight.Bold)
                    Text("posts", fontSize = 12.sp, color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = "",
                onValueChange = {},
                placeholder = { Text("Bio") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = yellow)
            )

            Spacer(modifier = Modifier.height(12.dp))

            TabRow(selectedTabIndex = selectedTab, containerColor = Color.White) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title, color = if (selectedTab == index) purple else Color.Gray) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.LightGray, RoundedCornerShape(50))
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text("Text text text text text #tag", fontSize = 14.sp)
            }
        }
    }
}