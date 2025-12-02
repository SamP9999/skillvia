package com.skillvia.app.ui.main

import android.os.Bundle
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.skillvia.app.ui.theme.SkillviaTheme
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.skillvia.app.ui.skills.SkillsListScreen
import com.skillvia.app.ui.skills.SkillDetailScreen
import com.skillvia.app.ui.skills.AddSkillRequestScreen
import com.skillvia.app.ui.profile.ProfileScreen
import com.skillvia.app.ui.auth.LoginScreen
import com.skillvia.app.ui.auth.SignupScreen
import com.skillvia.app.ui.skills.AddSkillScreen
import com.skillvia.app.ui.skills.EditSkillScreen
import com.skillvia.app.ui.skills.MapViewScreen
import com.skillvia.app.ui.requests.RequestManagementScreen
import com.skillvia.app.ui.profile.EditProfileScreen


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SkillviaTheme {
                    SkillviaApp()
            }
        }
    }
}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Composable
fun SkillviaApp() {
    val navController = rememberNavController()
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "login",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("login"){
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate("skills_list") {
                            popUpTo("login") { inclusive = true }
                        }
                    },
                    onNavigateToSignup = {
                        navController.navigate("signup")
                    }
                )
            }
            composable("signup"){
                SignupScreen(
                    onSignupSuccess = {
                        navController.navigate("skills_list") {
                            popUpTo("login") { inclusive = true }
                        }
                    },
                    onNavigateToLogin = {
                        navController.popBackStack()
                    }
                )
            }
            composable("skills_list") {
                SkillsListScreen(
                    onSkillClick = { skillId ->
                      navController.navigate("skill_detail/$skillId")
                    },
                    onProfileClick = {
                        navController.navigate("profile")
                    },
                    onLogout = {
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
            composable("skill_detail/{skillId}") { backStackEntry ->
                val skillId = backStackEntry.arguments?.getString("skillId") ?: ""
                SkillDetailScreen(
                    skillId = skillId,
                    onRequestSkill =  { skillId: String -> navController.navigate("add_request/$skillId") },
                    onBackClick = { navController.popBackStack() },
                    onViewMap = { locationName, latitude, longitude ->
                        // Encode values for safe navigation arguments
                        val encodedLocation = Uri.encode(locationName)
                        val latParam = latitude?.toString() ?: ""
                        val lngParam = longitude?.toString() ?: ""
                        navController.navigate("map_view?loc=$encodedLocation&lat=$latParam&lng=$lngParam")
                    }
                )
            }
            composable(
                route = "map_view?loc={loc}&lat={lat}&lng={lng}",
                arguments = listOf(
                    navArgument("loc") { type = NavType.StringType; nullable = true; defaultValue = "" },
                    navArgument("lat") { type = NavType.StringType; nullable = true; defaultValue = "" },
                    navArgument("lng") { type = NavType.StringType; nullable = true; defaultValue = "" }
                )
            ) { backStackEntry ->
                val args = backStackEntry.arguments
                val locationName = args?.getString("loc")?.let { Uri.decode(it) } ?: ""
                val latitude = args?.getString("lat")?.toDoubleOrNull()
                val longitude = args?.getString("lng")?.toDoubleOrNull()
                
                MapViewScreen(
                    locationName = locationName,
                    latitude = latitude,
                    longitude = longitude,
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable("add_request/{skillId}") { backStackEntry ->
                val skillId = backStackEntry.arguments?.getString("skillId") ?: ""
                AddSkillRequestScreen(
                    skillId = skillId,
                    onRequestSubmitted = { navController.popBackStack() },
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable("profile") { backStackEntry ->
                ProfileScreen(
                    onBackClick = { navController.popBackStack() },
                    onAddSkillClick = { navController.navigate("add_skill") },
                    onManageRequestsClick = { navController.navigate("manage_requests") },
                    onEditProfileClick = { navController.navigate("edit_profile") },
                    onEditSkillClick = { skillId ->
                        navController.navigate("edit_skill/$skillId")
                    },
                    refreshTrigger = backStackEntry.savedStateHandle.get<Int>("refresh") ?: 0
                )
            }
            composable("edit_profile") {
                EditProfileScreen(
                    onBackClick = { navController.popBackStack() },
                    onProfileUpdated = {
                        navController.previousBackStackEntry?.savedStateHandle?.set("refresh", System.currentTimeMillis().toInt())
                        navController.popBackStack()
                    }
                )
            }
            composable("add_skill") {
                AddSkillScreen(
                    onSkillAdded = { navController.popBackStack() },
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable("edit_skill/{skillId}") { backStackEntry ->
                val skillId = backStackEntry.arguments?.getString("skillId") ?: ""
                EditSkillScreen(
                    skillId = skillId,
                    onSkillUpdated = {
                        navController.previousBackStackEntry?.savedStateHandle?.set("refresh", System.currentTimeMillis().toInt())
                        navController.popBackStack()
                    },
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable("manage_requests") {
                RequestManagementScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SkillviaTheme {
        Greeting("Skillvia")
    }
}