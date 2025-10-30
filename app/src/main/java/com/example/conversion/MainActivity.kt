package com.example.conversion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.example.conversion.navigation.ConversionNavHost
import com.example.conversion.navigation.Route
import com.example.conversion.presentation.settings.SettingsViewModel
import com.example.conversion.ui.theme.ConversionTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Get theme preferences from Settings ViewModel
            val settingsViewModel: SettingsViewModel = hiltViewModel()
            val settingsState by settingsViewModel.state.collectAsStateWithLifecycle()
            
            ConversionTheme(
                themeMode = settingsState.preferences.themeMode,
                dynamicColor = settingsState.preferences.useDynamicColors
            ) {
                val navController = rememberNavController()
                
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ConversionNavHost(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding),
                        onNavigateToSettings = {
                            navController.navigate(Route.Settings)
                        }
                    )
                }
            }
        }
    }
}