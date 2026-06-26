package com.style_sphere.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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

@Composable
fun ForumScreen(navController: NavController) {
    val purple = Color(0xFF7B5EA7)

    Scaffold(
        bottomBar = { BottomNavBar(navController = navController, current = "forum") }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Top bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("☰", fontSize = 24.sp)
                Text("✉", fontSize = 24.sp)
                Text("🔍", fontSize = 24.sp)
                Text("🏠", fontSize = 24.sp)
                Text("🔔", fontSize = 24.sp)
            }

            // Post input
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFFFFE4E9), RoundedCornerShape(50))
                )
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    placeholder = { Text("What's Happening ?", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = purple,
                        unfocusedBorderColor = Color.LightGray
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Posts list
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(5) {
                    ForumPost(purple = purple)
                }
            }
        }
    }
}

@Composable
fun ForumPost(purple: Color) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.LightGray, RoundedCornerShape(50))
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text("Username", fontWeight = FontWeight.Bold, color = purple, fontSize = 14.sp)
                Text("Text text text text text #tag", fontSize = 13.sp)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .background(Color.LightGray, RoundedCornerShape(12.dp))
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("💬 20", fontSize = 13.sp, color = Color.Gray)
            Text("🔁 20", fontSize = 13.sp, color = Color.Gray)
            Text("❤ 20", fontSize = 13.sp, color = Color.Gray)
        }
        Divider(modifier = Modifier.padding(top = 12.dp), color = Color.LightGray)
    }
}