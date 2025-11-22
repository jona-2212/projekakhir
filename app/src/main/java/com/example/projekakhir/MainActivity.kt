package com.example.projekakhir

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.projekakhir.fe.MarketplaceScreen
import com.example.projekakhir.fe.AddItemScreen
import com.example.projekakhir.be.data.viewmodel.MarketPlaceViewModel
import com.example.projekakhir.ui.theme.ReStyleTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ReStyleTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val viewModel: MarketPlaceViewModel = viewModel()

                    NavHost(
                        navController = navController,
                        startDestination = "marketplace"
                    ) {

                        composable("marketplace") {
                            MarketplaceScreen(
                                viewModel = viewModel,
                                onAddClick = { navController.navigate("add") }
                            )
                        }

                        composable("add") {
                            AddItemScreen(
                                onBack = { navController.popBackStack() }, // ✅ TAMBAH INI
                                onSuccess = {
                                    viewModel.loadItems() // Refresh data
                                    navController.popBackStack()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}