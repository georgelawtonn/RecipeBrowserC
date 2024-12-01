package com.example.recipebrowserc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.recipebrowserc.ui.*
import com.example.recipebrowserc.ui.theme.GroceryAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GroceryAppTheme {
                GroceryApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroceryApp() {
    val navController = rememberNavController()
    val items = listOf(
        Screen.Recipes,
        Screen.Kitchen,
        Screen.GroceryList
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(painterResource(id = screen.iconResourceId), contentDescription = null) },
                        label = { Text(stringResource(screen.resourceId)) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(navController, startDestination = Screen.GroceryList.route, Modifier.padding(innerPadding)) {
            composable(Screen.Recipes.route) {
                RecipesScreen(
                    onNavigateToRecipeDetails = { recipeId ->
                        // TODO: Implement navigation to recipe details
                    }
                )
            }
            composable(Screen.Kitchen.route) {
                // TODO: Implement KitchenScreen
            }

            composable("newGroceryList") {
                NewGroceryListScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable(Screen.GroceryList.route) {
                GroceryListScreen(
                    onNavigateToGroceryListDetail = { groceryListId ->
                        navController.navigate("groceryListDetail/$groceryListId")
                    },
                    onNavigateToNewList = {
                        navController.navigate("newGroceryList")
                    }
                )
            }

            composable(
                route = "groceryListDetail/{groceryListId}",
                arguments = listOf(navArgument("groceryListId") { type = NavType.IntType })
            ) { backStackEntry ->
                val groceryListId = backStackEntry.arguments?.getInt("groceryListId") ?: return@composable
                GroceryListDetailScreen(
                    groceryListId = groceryListId,
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onNavigateToUnitManager = {
                        navController.navigate("unitManager")
                    }
                )
            }

            composable("unitManager") {
                UnitManagerScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}