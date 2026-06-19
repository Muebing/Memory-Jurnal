package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.data.AppDatabase
import com.example.data.CoupleRepository
import com.example.ui.*
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 1. Initialize Room Local Database
        val database = AppDatabase.getDatabase(this)
        val dao = database.coupleDao()
        val repository = CoupleRepository(dao)

        // 2. Setup ViewModel
        val factory = CoupleViewModelFactory(application, repository)
        val viewModel = ViewModelProvider(this, factory)[CoupleViewModel::class.java]

        setContent {
            MyApplicationTheme {
                val activeSession by viewModel.activeSession.collectAsStateWithLifecycle()
                val navController = rememberNavController()

                // If user is not logged in, force render the gorgeous romantic AuthScreen
                val user = activeSession
                if (user == null || !user.isLoggedIn) {
                    AuthScreen(viewModel = viewModel)
                } else {
                    // Logged in! Render full app with modern bottom navigation
                    var currentRoute by remember { mutableStateOf("dashboard") }

                    // Track backstack and synchronize the bottom navigation active selection automatically
                    LaunchedEffect(navController) {
                        navController.currentBackStackEntryFlow.collect { backStackEntry ->
                            backStackEntry.destination.route?.let { route ->
                                // Sync simple top tabs
                                if (route == "dashboard" || route == "kegiatan_list" || route == "memories_wall") {
                                    currentRoute = route
                                }
                            }
                        }
                    }

                    // Only show bottom navigation on primary 3 tabs
                    val shouldShowBottomBar = currentRoute in listOf("dashboard", "kegiatan_list", "memories_wall")

                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        bottomBar = {
                            if (shouldShowBottomBar) {
                                NavigationBar(
                                    modifier = Modifier
                                        .navigationBarsPadding()
                                        .testTag("app_navigation_bar"),
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    tonalElevation = 4.dp
                                ) {
                                    NavigationBarItem(
                                        selected = currentRoute == "dashboard",
                                        onClick = {
                                            if (currentRoute != "dashboard") {
                                                navController.navigate("dashboard") {
                                                    popUpTo("dashboard") { saveState = true }
                                                    launchSingleTop = true
                                                    restoreState = true
                                                }
                                            }
                                        },
                                        icon = {
                                            Icon(
                                                imageVector = if (currentRoute == "dashboard") Icons.Filled.Dashboard else Icons.Outlined.Dashboard,
                                                contentDescription = "Menu Utama / Dashboard"
                                            )
                                        },
                                        label = { Text("Mulai") },
                                        modifier = Modifier.testTag("nav_tab_dashboard")
                                    )

                                    NavigationBarItem(
                                        selected = currentRoute == "kegiatan_list",
                                        onClick = {
                                            if (currentRoute != "kegiatan_list") {
                                                navController.navigate("kegiatan_list") {
                                                    popUpTo("dashboard") { saveState = true }
                                                    launchSingleTop = true
                                                    restoreState = true
                                                }
                                            }
                                        },
                                        icon = {
                                            Icon(
                                                imageVector = if (currentRoute == "kegiatan_list") Icons.Filled.CalendarMonth else Icons.Outlined.CalendarMonth,
                                                contentDescription = "Menu Agenda Kencan"
                                            )
                                        },
                                        label = { Text("Agenda") },
                                        modifier = Modifier.testTag("nav_tab_agenda")
                                    )

                                    NavigationBarItem(
                                        selected = currentRoute == "memories_wall",
                                        onClick = {
                                            if (currentRoute != "memories_wall") {
                                                navController.navigate("memories_wall") {
                                                    popUpTo("dashboard") { saveState = true }
                                                    launchSingleTop = true
                                                    restoreState = true
                                                }
                                            }
                                        },
                                        icon = {
                                            Icon(
                                                imageVector = if (currentRoute == "memories_wall") Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                                contentDescription = "Menu Dinding Kenangan"
                                            )
                                        },
                                        label = { Text("Kenangan") },
                                        modifier = Modifier.testTag("nav_tab_kenangan")
                                    )
                                }
                            }
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f),
                                            MaterialTheme.colorScheme.background
                                        )
                                    )
                                )
                                .padding(if (shouldShowBottomBar) innerPadding else PaddingValues(0.dp))
                        ) {
                            NavHost(
                                navController = navController,
                                startDestination = "dashboard",
                                modifier = Modifier.fillMaxSize()
                            ) {
                                // 1. Tab Dashboard
                                composable("dashboard") {
                                    DashboardScreen(
                                        viewModel = viewModel,
                                        onNavigateToKegiatanList = { navController.navigate("kegiatan_list") },
                                        onNavigateToKegiatanDetail = { id -> navController.navigate("kegiatan_detail/$id") },
                                        onNavigateToCreateKegiatan = { navController.navigate("kegiatan_form/add/-1") }
                                    )
                                }

                                // 2. Tab Agenda/Kegiatan List
                                composable("kegiatan_list") {
                                    KegiatanListScreen(
                                        viewModel = viewModel,
                                        onNavigateToKegiatanDetail = { id -> navController.navigate("kegiatan_detail/$id") },
                                        onNavigateToCreateKegiatan = { navController.navigate("kegiatan_form/add/-1") }
                                    )
                                }

                                // 3. Tab Dinding Kenangan
                                composable("memories_wall") {
                                    MemoriesWallScreen(
                                        viewModel = viewModel,
                                        onNavigateToKegiatanDetail = { id -> navController.navigate("kegiatan_detail/$id") }
                                    )
                                }

                                // 4. Detail Kegiatan
                                composable(
                                    route = "kegiatan_detail/{id}",
                                    arguments = listOf(navArgument("id") { type = NavType.IntType })
                                ) { backStackEntry ->
                                    val id = backStackEntry.arguments?.getInt("id") ?: -1
                                    KegiatanDetailScreen(
                                        viewModel = viewModel,
                                        kegiatanId = id,
                                        onNavigateToEdit = { planId -> navController.navigate("kegiatan_form/edit/$planId") },
                                        onNavigateBack = { navController.popBackStack() }
                                    )
                                }

                                // 5. Form Kegiatan (Add / Edit)
                                composable(
                                    route = "kegiatan_form/{mode}/{id}",
                                    arguments = listOf(
                                        navArgument("mode") { type = NavType.StringType },
                                        navArgument("id") { type = NavType.IntType }
                                    )
                                ) { backStackEntry ->
                                    val mode = backStackEntry.arguments?.getString("mode") ?: "add"
                                    val id = backStackEntry.arguments?.getInt("id") ?: -1
                                    KegiatanFormScreen(
                                        viewModel = viewModel,
                                        mode = mode,
                                        kegiatanId = id,
                                        onNavigateBack = { navController.popBackStack() }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
