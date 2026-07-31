package com.style_sphere.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.style_sphere.data.UserProfile
import com.style_sphere.navigation.Screen
import com.style_sphere.util.base64ToBitmap
import com.style_sphere.util.bitmapToBase64
import com.style_sphere.util.uriToBitmap

@Composable
fun ProfileScreen(navController: NavController) {
    val purple = Color(0xFF7B5EA7)
    val yellow = Color(0xFFFFD600)
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Posts", "Media", "Likes")

    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val uid = auth.currentUser?.uid

    // What's saved in Firestore right now
    var savedUsername by remember { mutableStateOf("fashionista") }
    var savedPictureBase64 by remember { mutableStateOf("") }
    var savedBio by remember { mutableStateOf("") }

    // Editing state (only used while isEditing is true)
    var isEditing by remember { mutableStateOf(false) }
    var editedUsername by remember { mutableStateOf("") }
    var editedBio by remember { mutableStateOf("") }
    var editedPictureBitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }
    var isSaving by remember { mutableStateOf(false) }

    LaunchedEffect(uid) {
        if (uid == null) return@LaunchedEffect
        db.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                val profile = doc.toObject(UserProfile::class.java)
                if (profile != null) {
                    savedUsername = profile.username
                    savedPictureBase64 = profile.profilePictureBase64
                    savedBio = profile.bio
                }
            }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            uriToBitmap(context, uri)?.let { editedPictureBitmap = it }
        }
    }

    val displayedPictureBitmap = editedPictureBitmap
        ?: savedPictureBase64.takeIf { it.isNotBlank() }?.let { base64ToBitmap(it) }

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

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // Left half: just the picture, centered
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(200.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFE4E9))
                            .then(
                                if (isEditing) Modifier.clickable {
                                    galleryLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                } else Modifier
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (displayedPictureBitmap != null) {
                            Image(
                                bitmap = displayedPictureBitmap.asImageBitmap(),
                                contentDescription = "Profile picture",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else if (isEditing) {
                            Text("Tap to\nadd photo", fontSize = 11.sp, color = Color.DarkGray)
                        }
                    }
                }

                // Right half: icon row at top, username/bio centered below it
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = {}) {
                            Icon(Icons.Filled.Settings, contentDescription = "Settings", tint = Color.Gray)
                        }
                        IconButton(onClick = {
                            if (isEditing) {
                                // Save changes
                                if (uid != null) {
                                    isSaving = true
                                    val newPictureBase64 = editedPictureBitmap?.let { bitmapToBase64(it) }
                                        ?: savedPictureBase64
                                    val updatedProfile = UserProfile(
                                        uid = uid,
                                        username = editedUsername.ifBlank { "fashionista" },
                                        profilePictureBase64 = newPictureBase64,
                                        bio = editedBio
                                    )
                                    db.collection("users").document(uid)
                                        .set(updatedProfile)
                                        .addOnSuccessListener {
                                            savedUsername = updatedProfile.username
                                            savedPictureBase64 = updatedProfile.profilePictureBase64
                                            savedBio = updatedProfile.bio
                                            editedPictureBitmap = null
                                            isSaving = false
                                            isEditing = false
                                        }
                                        .addOnFailureListener {
                                            isSaving = false
                                        }
                                }
                            } else {
                                // Enter edit mode
                                editedUsername = savedUsername
                                editedBio = savedBio
                                editedPictureBitmap = null
                                isEditing = true
                            }
                        }) {
                            Icon(
                                if (isEditing) Icons.Filled.Check else Icons.Filled.Edit,
                                contentDescription = if (isEditing) "Save Profile" else "Edit Profile",
                                tint = Color.Gray
                            )
                        }
                        IconButton(onClick = {
                            navController.navigate(Screen.SignIn.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        }) {
                            Icon(Icons.Filled.ExitToApp, contentDescription = "Logout", tint = Color.Gray)
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    if (isEditing) {
                        OutlinedTextField(
                            value = editedUsername,
                            onValueChange = { editedUsername = it },
                            label = { Text("Username") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(50),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = yellow,
                                focusedLabelColor = yellow
                            )
                        )
                    } else {
                        Text(
                            savedUsername,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = purple
                        )
                    }

                    if (isEditing) {
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = editedBio,
                            onValueChange = { editedBio = it },
                            label = { Text("Bio") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(50),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = purple,
                                focusedLabelColor = purple
                            )
                        )
                    } else if (savedBio.isNotBlank()) {
                        Text(savedBio, fontSize = 14.sp, color = purple, fontWeight = FontWeight.Bold)
                    }

                    if (isSaving) {
                        Spacer(modifier = Modifier.height(8.dp))
                        CircularProgressIndicator(color = purple, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

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