package com.example.cs4131_project

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.compose.AppTheme
import com.example.cs4131_project.pages.HomePage.Companion.HomePage
import com.example.cs4131_project.pages.Onboarding.Companion.Onboarding
import com.example.cs4131_project.pages.PersonalDashboardPage.Companion.PersonalDashboardPage
import com.example.cs4131_project.pages.SignInPage.Companion.SignInPage
import com.example.cs4131_project.pages.SignUpPage.Companion.SignUpPage
import com.example.cs4131_project.pages.StudentPromptPage.Companion.StudentPromptPage
import com.example.cs4131_project.pages.TeacherPromptPage.Companion.TeacherPromptPage

class MainActivity : ComponentActivity() {
    companion object {
        lateinit var sharedPreferences: SharedPreferences
        val PREF_KEY = "CS4131_PROJECT_KEY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        enableImmersiveMode()

        sharedPreferences = applicationContext.getSharedPreferences(PREF_KEY, MODE_PRIVATE)

        setContent {
            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    MainApp(resources, applicationContext)
                }
            }
        }
    }

    private fun enableImmersiveMode() {
        val decorView = window.decorView
        decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                )
    }
}

@Composable
fun MainApp(resources: Resources, context: Context) {
    val navController = rememberNavController()
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    var showOnboarding by remember {mutableStateOf(MainActivity.sharedPreferences.getBoolean("showOnboarding", true))}

    NavHost(
        navController = navController,
        startDestination = if (showOnboarding) "onboardingPage" else "homePage",
        modifier = Modifier
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                })
            }
    ) {
        composable("onboardingPage") { Onboarding(navController, MainActivity.sharedPreferences) }
        composable("homePage") { HomePage(navController) }
        composable(
            "signInPage/{mode}",
            arguments = listOf(
                navArgument("mode") {type = NavType.StringType}
            )
        ) { backStackEntry ->
            val mode = backStackEntry.arguments?.getString("mode")
            if (mode != null) {
                SignInPage(navController, mode)
            }
        }
        composable(
            "signUpPage/{mode}",
            arguments = listOf(
                navArgument("mode") {type = NavType.StringType}
            )
        ) { backStackEntry ->
            val mode = backStackEntry.arguments?.getString("mode")
            if (mode != null) {
                SignUpPage(navController, mode)
            }
        }
        composable("studentPromptPage") {StudentPromptPage(navController)}
        composable("teacherPromptPage") {TeacherPromptPage(navController)}
        composable("personalDashboardPage") { PersonalDashboardPage(navController)}
    }
}