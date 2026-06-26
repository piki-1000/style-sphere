package com.style_sphere

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.style_sphere.navigation.NavGraph
import com.style_sphere.ui.theme.StyleSphereTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StyleSphereTheme {
                val navController = rememberNavController()
                NavGraph(navController = navController)
            }
        }
    }
}