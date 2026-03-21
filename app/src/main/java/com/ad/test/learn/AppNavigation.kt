package com.ad.test.learn

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

@Preview
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Screen.MainScreen.route
    ) {
        composable(Screen.MainScreen.route) {
            MainScreen {
                navController.navigate("${Screen.AccountsScreen.route}/1234")
            }
        }
        composable(
            "${Screen.AccountsScreen.route}/{emailId}",
            arguments = listOf(
                navArgument("emailId") { type = NavType.IntType }
            )
        ) {
            AccountsScreen(
                it.arguments?.getInt("emailId")
            ) { navController.popBackStack() }
        }
    }
}

@Composable
fun AccountsScreen(
    emailId:Int?,
    onNavigateBack: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(emailId.toString())
        Button(onNavigateBack) {
            Text(text = "Go Back")
        }
    }
}

@Composable
fun MainScreen(
    onNavigate: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        /* other fields */
        Button(onNavigate) {
            Text(text = "Go to accounts screen")
        }
    }
}

sealed class Screen(val route: String) {
    object MainScreen : Screen("main_screen")
    object AccountsScreen : Screen("accounts_screen")
}