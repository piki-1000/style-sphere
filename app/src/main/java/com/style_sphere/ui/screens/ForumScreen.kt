package com.style_sphere.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
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
import com.google.firebase.firestore.Query
import com.style_sphere.data.Post
import com.style_sphere.data.UserProfile

@Composable
fun ForumScreen(navController: NavController) {
    val purple = Color(0xFF7B5EA7)

    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val uid = auth.currentUser?.uid

    var postText by remember { mutableStateOf("") }
    var isPosting by remember { mutableStateOf(false) }
    var posts by remember { mutableStateOf<List<Post>>(emptyList()) }
    var currentUsername by remember { mutableStateOf("fashionista") }

    // Grab the current user's display name once, so new posts are tagged with it
    LaunchedEffect(uid) {
        if (uid == null) return@LaunchedEffect
        db.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                doc.toObject(UserProfile::class.java)?.let { currentUsername = it.username }
            }
    }

    // Live feed: any signed-in user sees every post, newest first, updating in real time
    DisposableEffect(Unit) {
        val listener = db.collection("posts")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    posts = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(Post::class.java)?.apply { id = doc.id }
                    }
                }
            }
        onDispose { listener.remove() }
    }

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
                    value = postText,
                    onValueChange = { postText = it },
                    placeholder = { Text("What's Happening ?", color = Color.Gray) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = purple,
                        unfocusedBorderColor = Color.LightGray
                    )
                )
                IconButton(
                    onClick = {
                        if (uid != null && postText.isNotBlank()) {
                            isPosting = true
                            val post = Post(
                                authorId = uid,
                                authorUsername = currentUsername,
                                text = postText.trim()
                            )
                            db.collection("posts").add(post)
                                .addOnSuccessListener {
                                    postText = ""
                                    isPosting = false
                                }
                                .addOnFailureListener {
                                    isPosting = false
                                }
                        }
                    },
                    enabled = !isPosting && postText.isNotBlank()
                ) {
                    Icon(Icons.Filled.Send, contentDescription = "Post", tint = purple)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Posts list
            if (posts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No posts yet — be the first to say something!", color = Color.Gray, fontSize = 14.sp)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(posts) { post ->
                        ForumPost(post = post, purple = purple)
                    }
                }
            }
        }
    }
}

@Composable
fun ForumPost(post: Post, purple: Color) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.LightGray, RoundedCornerShape(50))
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(post.authorUsername, fontWeight = FontWeight.Bold, color = purple, fontSize = 14.sp)
                Text(post.text, fontSize = 13.sp)
            }
        }
        Divider(modifier = Modifier.padding(top = 12.dp), color = Color.LightGray)
    }
}