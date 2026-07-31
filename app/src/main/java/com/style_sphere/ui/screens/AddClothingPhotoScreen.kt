package com.style_sphere.ui.screens

import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.style_sphere.data.NewClothingDraft
import com.style_sphere.navigation.Screen

private fun uriToBitmap(context: android.content.Context, uri: Uri): Bitmap? {
    return try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = android.graphics.ImageDecoder.createSource(context.contentResolver, uri)
            android.graphics.ImageDecoder.decodeBitmap(source)
        } else {
            @Suppress("DEPRECATION")
            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        }
    } catch (e: Exception) {
        null
    }
}

@Composable
fun AddClothingPhotoScreen(navController: NavController) {
    val purple = Color(0xFF7B5EA7)
    val yellow = Color(0xFFFFD600)
    val lavender = Color(0xFFE6DFF5)

    val context = LocalContext.current
    var photo by remember { mutableStateOf(NewClothingDraft.photoBitmap) }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) photo = bitmap
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            uriToBitmap(context, uri)?.let { photo = it }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = purple)
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "Add\nClothing",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = purple
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.Filled.Star, contentDescription = null, tint = yellow, modifier = Modifier.size(18.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(lavender, RoundedCornerShape(24.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (photo != null) {
                Image(
                    bitmap = photo!!.asImageBitmap(),
                    contentDescription = "Selected clothing photo",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(onClick = {
                    galleryLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }) {
                    Icon(Icons.Filled.PhotoLibrary, contentDescription = "Open Gallery", tint = purple)
                }
                Text("Open Gallery", fontSize = 12.sp, color = Color.Gray)
            }

            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(purple, CircleShape)
                    .clickable { cameraLauncher.launch(null) }
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = {
                        NewClothingDraft.photoBitmap = photo
                        navController.navigate(Screen.AddClothingDetails.route)
                    },
                    enabled = photo != null
                ) {
                    Icon(
                        Icons.Filled.Save,
                        contentDescription = "Save",
                        tint = if (photo != null) purple else Color.LightGray
                    )
                }
                Text("Save", fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}