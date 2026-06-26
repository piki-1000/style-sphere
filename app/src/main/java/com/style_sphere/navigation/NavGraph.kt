package com.style_sphere.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.style_sphere.ui.screens.SplashScreen
import com.style_sphere.ui.screens.SignInScreen
import com.style_sphere.ui.screens.HomeScreen
import com.style_sphere.ui.screens.ProfileScreen
import com.style_sphere.ui.screens.ClosetScreen
import com.style_sphere.ui.screens.ForumScreen

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object SignIn : Screen("signin")
    object Home : Screen("home")
    object Profile : Screen("profile")
    object Closet : Screen("closet")
    object Forum : Screen("forum")
}

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(navController = navController)
        }
        composable(Screen.SignIn.route) {
            SignInScreen(navController = navController)
        }
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }
        composable(Screen.Profile.route) {
            ProfileScreen(navController = navController)
        }
        composable(Screen.Closet.route) {
            ClosetScreen(navController = navController)
        }
        composable(Screen.Forum.route) {
            ForumScreen(navController = navController)
        }
    }
}