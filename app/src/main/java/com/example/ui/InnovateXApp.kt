package com.example.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.compose.material.icons.filled.VideoCall
import androidx.compose.material.icons.outlined.VideoCall
import com.example.ui.screens.AdminPanelScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.ClassesScreen
import com.example.ui.screens.ExploreScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MyProjectsScreen
import com.example.ui.screens.NotificationsScreen
import com.example.ui.screens.ProjectDetailScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.SubmitProjectScreen
import com.example.ui.viewmodel.InnovateXViewModel

sealed class Screen(val route: String, val title: String) {
    data object Home : Screen("home", "Home")
    data object Explore : Screen("explore", "Explore")
    data object Classes : Screen("classes", "Classes")
    data object Submit : Screen("submit", "Submit")
    data object Notifications : Screen("notifications", "Notifications")
    data object Profile : Screen("profile", "Profile")
    data object MyProjects : Screen("my_projects", "My Projects")
    data object AdminPanel : Screen("admin_panel", "Admin Panel")
    data object ProjectDetail : Screen("project_detail/{projectId}", "Project Detail") {
        fun createRoute(projectId: String) = "project_detail/$projectId"
    }
    data object Auth : Screen("auth", "Auth")
}

@Composable
fun InnovateXApp(
    viewModel: InnovateXViewModel,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val snackbarHostState = remember { SnackbarHostState() }
    val unreadNotifCount by viewModel.unreadNotifCount.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()

    // Listen to user snackbar messages
    LaunchedEffect(Unit) {
        viewModel.userMessage.collect { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    val topLevelRoutes = listOf(
        Screen.Home.route,
        Screen.Explore.route,
        Screen.Classes.route,
        Screen.Submit.route,
        Screen.Notifications.route,
        Screen.Profile.route
    )

    val showBottomBar = currentRoute in topLevelRoutes

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    modifier = Modifier.testTag("main_bottom_nav")
                ) {
                    // Home
                    NavigationBarItem(
                        selected = currentRoute == Screen.Home.route,
                        onClick = {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = if (currentRoute == Screen.Home.route) Icons.Default.Home else Icons.Outlined.Home,
                                contentDescription = "Home"
                            )
                        },
                        label = { Text("Home") },
                        modifier = Modifier.testTag("nav_home")
                    )

                    // Explore
                    NavigationBarItem(
                        selected = currentRoute == Screen.Explore.route,
                        onClick = {
                            navController.navigate(Screen.Explore.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = if (currentRoute == Screen.Explore.route) Icons.Default.Explore else Icons.Outlined.Explore,
                                contentDescription = "Explore"
                            )
                        },
                        label = { Text("Explore") },
                        modifier = Modifier.testTag("nav_explore")
                    )

                    // Classes (Zoom Live Classes & Payment Lock)
                    NavigationBarItem(
                        selected = currentRoute == Screen.Classes.route,
                        onClick = {
                            navController.navigate(Screen.Classes.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = if (currentRoute == Screen.Classes.route) Icons.Default.VideoCall else Icons.Outlined.VideoCall,
                                contentDescription = "Classes"
                            )
                        },
                        label = { Text("Classes") },
                        modifier = Modifier.testTag("nav_classes")
                    )

                    // Submit
                    NavigationBarItem(
                        selected = currentRoute == Screen.Submit.route,
                        onClick = {
                            viewModel.startNewSubmission()
                            navController.navigate(Screen.Submit.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = if (currentRoute == Screen.Submit.route) Icons.Default.AddCircle else Icons.Outlined.AddCircleOutline,
                                contentDescription = "Submit"
                            )
                        },
                        label = { Text("Submit") },
                        modifier = Modifier.testTag("nav_submit")
                    )

                    // Notifications with badge
                    NavigationBarItem(
                        selected = currentRoute == Screen.Notifications.route,
                        onClick = {
                            navController.navigate(Screen.Notifications.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (unreadNotifCount > 0) {
                                        Badge {
                                            Text("$unreadNotifCount")
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = if (currentRoute == Screen.Notifications.route) Icons.Default.Notifications else Icons.Outlined.Notifications,
                                    contentDescription = "Notifications"
                                )
                            }
                        },
                        label = { Text("Alerts") },
                        modifier = Modifier.testTag("nav_notifications")
                    )

                    // Profile
                    NavigationBarItem(
                        selected = currentRoute == Screen.Profile.route,
                        onClick = {
                            navController.navigate(Screen.Profile.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = if (currentRoute == Screen.Profile.route) Icons.Default.Person else Icons.Outlined.Person,
                                contentDescription = "Profile"
                            )
                        },
                        label = { Text("Profile") },
                        modifier = Modifier.testTag("nav_profile")
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    viewModel = viewModel,
                    onNavigateToExplore = { category ->
                        if (category != null) {
                            viewModel.selectedCategory.value = category
                        }
                        navController.navigate(Screen.Explore.route)
                    },
                    onNavigateToSubmit = {
                        navController.navigate(Screen.Submit.route)
                    },
                    onNavigateToDetail = { projectId ->
                        viewModel.selectProject(projectId)
                        navController.navigate(Screen.ProjectDetail.createRoute(projectId))
                    }
                )
            }

            composable(Screen.Explore.route) {
                ExploreScreen(
                    viewModel = viewModel,
                    onNavigateToDetail = { projectId ->
                        viewModel.selectProject(projectId)
                        navController.navigate(Screen.ProjectDetail.createRoute(projectId))
                    }
                )
            }

            composable(Screen.Classes.route) {
                ClassesScreen(
                    viewModel = viewModel
                )
            }

            composable(Screen.Submit.route) {
                SubmitProjectScreen(
                    viewModel = viewModel,
                    onSubmissionSuccess = {
                        navController.navigate(Screen.MyProjects.route) {
                            popUpTo(Screen.Home.route)
                        }
                    },
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable(Screen.Notifications.route) {
                NotificationsScreen(
                    viewModel = viewModel,
                    onNavigateToProject = { projectId ->
                        viewModel.selectProject(projectId)
                        navController.navigate(Screen.ProjectDetail.createRoute(projectId))
                    }
                )
            }

            composable(Screen.Profile.route) {
                ProfileScreen(
                    viewModel = viewModel,
                    onNavigateToMyProjects = {
                        navController.navigate(Screen.MyProjects.route)
                    },
                    onNavigateToAdminPanel = {
                        navController.navigate(Screen.AdminPanel.route)
                    },
                    onNavigateToAuth = {
                        navController.navigate(Screen.Auth.route)
                    }
                )
            }

            composable(Screen.MyProjects.route) {
                MyProjectsScreen(
                    viewModel = viewModel,
                    onNavigateToDetail = { projectId ->
                        viewModel.selectProject(projectId)
                        navController.navigate(Screen.ProjectDetail.createRoute(projectId))
                    },
                    onNavigateToSubmit = {
                        viewModel.startNewSubmission()
                        navController.navigate(Screen.Submit.route)
                    },
                    onEditProject = { project ->
                        viewModel.editExistingProject(project)
                        navController.navigate(Screen.Submit.route)
                    },
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable(Screen.AdminPanel.route) {
                AdminPanelScreen(
                    viewModel = viewModel,
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onNavigateToProjectDetail = { projectId ->
                        viewModel.selectProject(projectId)
                        navController.navigate(Screen.ProjectDetail.createRoute(projectId))
                    }
                )
            }

            composable(
                route = Screen.ProjectDetail.route,
                arguments = listOf(navArgument("projectId") { type = NavType.StringType })
            ) { backStackEntry ->
                val projId = backStackEntry.arguments?.getString("projectId") ?: ""
                ProjectDetailScreen(
                    projectId = projId,
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onOpenAdminReview = {
                        navController.navigate(Screen.AdminPanel.route)
                    },
                    onEditProject = {
                        viewModel.selectedProject.value?.let { p ->
                            viewModel.editExistingProject(p)
                            navController.navigate(Screen.Submit.route)
                        }
                    }
                )
            }

            composable(Screen.Auth.route) {
                AuthScreen(
                    viewModel = viewModel,
                    onAuthSuccess = {
                        navController.navigate(Screen.Profile.route) {
                            popUpTo(Screen.Home.route)
                        }
                    }
                )
            }
        }
    }
}
