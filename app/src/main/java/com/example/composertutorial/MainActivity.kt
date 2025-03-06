package com.example.composertutorial

import android.content.Intent
import android.os.Build
import androidx.navigation.compose.composable
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.composertutorial.ui.theme.ComposerTutorialTheme
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost

class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().apply{
            setKeepOnScreenCondition {
                !viewModel.isReady.value
            }
        }
        enableEdgeToEdge()

        val serviceIntent = Intent(this, SensorService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }

        setContent {
            ComposerTutorialTheme {
                val navController = rememberNavController()
                MyAppNavHost(navController)
            }
        }
    }
}

@Composable
fun MyAppNavHost(navController: NavHostController) {
    NavHost (
        navController = navController,
        startDestination = "front_page"
    ){
        composable("front_page") {
            FrontPage(
                onNavigateToMessages = {
                    navController.navigate("message_page")
                }
            )
        }
        composable("message_page") {
            Conversation(
                onNavigateBack = {
                    if (navController.popBackStack("front_page", false)) {
                        navController.navigate("front_page")
                    }
                },
                messages = SampleData.conversationSample
            )
        }

    }
}

