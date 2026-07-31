package com.style_sphere.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.style_sphere.ui.screens.AddClothingDetailsScreen
import com.style_sphere.ui.screens.AddClothingPhotoScreen
import com.style_sphere.ui.screens.SplashScreen
import com.style_sphere.ui.screens.SignInScreen
import com.style_sphere.ui.screens.HomeScreen
import com.style_sphere.ui.screens.ProfileScreen
import com.style_sphere.ui.screens.ClosetScreen
import com.style_sphere.ui.screens.ForumScreen
import com.style_sphere.ui.screens.SignUpScreen
import com.style_sphere.ui.screens.OutfitRouletteScreen
import com.style_sphere.ui.screens.LookDetailScreen
import com.style_sphere.ui.screens.EditClothingItemScreen

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object SignIn : Screen("signin")
    object SignUp : Screen("signup")
    object Home : Screen("home")
    object Profile : Screen("profile")
    object Closet : Screen("closet")
    object Forum : Screen("forum")
    object AddClothingPhoto : Screen("add_clothing_photo")
    object AddClothingDetails : Screen("add_clothing_details")
    object OutfitRoulette : Screen("outfit_roulette")

    object LookDetail : Screen("look_detail/{lookId}") {
        fun createRoute(lookId: String) = "look_detail/$lookId"
    }
    object EditClothingItem : Screen("edit_clothing_item/{itemId}") {
        fun createRoute(itemId: String) = "edit_clothing_item/$itemId"
    }
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
        composable(Screen.SignUp.route) {
            SignUpScreen(navController = navController)
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
        composable(Screen.AddClothingPhoto.route) {
            AddClothingPhotoScreen(navController = navController)
        }
        composable(Screen.AddClothingDetails.route) {
            AddClothingDetailsScreen(navController = navController)
        }
        composable(Screen.OutfitRoulette.route) {
            OutfitRouletteScreen(navController = navController)
        }
        composable(
            route = Screen.LookDetail.route,
            arguments = listOf(navArgument("lookId") { type = NavType.StringType })
        ) { backStackEntry ->
            val lookId = backStackEntry.arguments?.getString("lookId") ?: return@composable
            LookDetailScreen(navController = navController, lookId = lookId)
        }
        composable(
            route = Screen.EditClothingItem.route,
            arguments = listOf(navArgument("itemId") { type = NavType.StringType })
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString("itemId") ?: return@composable
            EditClothingItemScreen(navController = navController, itemId = itemId)
        }
    }
}