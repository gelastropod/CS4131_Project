package com.example.cs4131_project

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.compose.AppTheme
import com.example.cs4131_project.pages.ClassDashboardPage.Companion.ClassDashboardPage
import com.example.cs4131_project.pages.ClassDetailsPage.Companion.ClassDetailsPage
import com.example.cs4131_project.pages.CreateClassPage.Companion.CreateClassPage
import com.example.cs4131_project.pages.CreateNewPage.Companion.CreateNewPage
import com.example.cs4131_project.pages.EquationPage
import com.example.cs4131_project.pages.EquationPage.Companion.EquationPage
import com.example.cs4131_project.pages.GraphPage.Companion.GraphPage
import com.example.cs4131_project.pages.StudentClassListPage.Companion.StudentClassListPage
import com.example.cs4131_project.pages.HomePage.Companion.HomePage
import com.example.cs4131_project.pages.JoinClassPage.Companion.JoinClassPage
import com.example.cs4131_project.pages.NotesPage.Companion.NotesPage
import com.example.cs4131_project.pages.Onboarding.Companion.Onboarding
import com.example.cs4131_project.pages.PersonalDashboardPage.Companion.PersonalDashboardPage
import com.example.cs4131_project.pages.SettingsPage.Companion.SettingsPage
import com.example.cs4131_project.pages.SignInPage.Companion.SignInPage
import com.example.cs4131_project.pages.SignUpPage.Companion.SignUpPage
import com.example.cs4131_project.pages.StudentClassListPage
import com.example.cs4131_project.pages.StudentDashboardPage.Companion.StudentDashboardPage
import com.example.cs4131_project.pages.StudentPromptPage.Companion.StudentPromptPage
import com.example.cs4131_project.pages.TeacherClassListPage.Companion.TeacherClassListPage
import com.example.cs4131_project.pages.TeacherDashboardPage.Companion.TeacherDashboardPage
import com.example.cs4131_project.pages.TeacherPromptPage.Companion.TeacherPromptPage

class MainActivity : ComponentActivity() {
    companion object {
        lateinit var sharedPreferences: SharedPreferences
        val PREF_KEY = "CS4131_PROJECT_KEY"
        var darkThemeState = mutableStateOf(false)
        var darkTheme: Boolean = false
            get() = darkThemeState.value
    }

    private fun isSystemInDarkMode(context: Context): Boolean {
        val nightModeFlags = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return nightModeFlags == Configuration.UI_MODE_NIGHT_YES
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        enableImmersiveMode()

        sharedPreferences = applicationContext.getSharedPreferences(PREF_KEY, MODE_PRIVATE)

        darkThemeState.value = isSystemInDarkMode(applicationContext)

        setContent {
            AppTheme(darkTheme = darkTheme) {
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
                    if (EquationPage.initialised)
                        EquationPage.selected.value = -1
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
        composable("studentDashboardPage") { StudentDashboardPage(navController)}
        composable("teacherDashboardPage") { TeacherDashboardPage(navController)}
        composable(
            "classDashboardPage/{mode}",
            arguments = listOf(
                navArgument("mode") {type = NavType.StringType}
            )
        ) { backStackEntry ->
            val mode = backStackEntry.arguments?.getString("mode")
            if (mode != null) {
                ClassDashboardPage(navController, mode)
            }
        }
        composable("studentClassListPage") { StudentClassListPage(navController) }
        composable("teacherClassListPage") { TeacherClassListPage(navController) }
        composable(
            "classDetailsPage/{mode}",
            arguments = listOf(
                navArgument("mode") {type = NavType.StringType}
            )
        ) { backStackEntry ->
            val mode = backStackEntry.arguments?.getString("mode")
            if (mode != null) {
                ClassDetailsPage(navController, mode)
            }
        }
        composable("joinClassPage") {JoinClassPage(navController)}
        composable("createClassPage") {CreateClassPage(navController)}
        composable(
            "settingsPage/{mode}",
            arguments = listOf(
                navArgument("mode") {type = NavType.StringType}
            )
        ) { backStackEntry ->
            val mode = backStackEntry.arguments?.getString("mode")
            if (mode != null) {
                SettingsPage(navController, mode)
            }
        }
        composable(
            "createNewPage/{mode}",
            arguments = listOf(
                navArgument("mode") {type = NavType.StringType}
            )
        ) { backStackEntry ->
            val mode = backStackEntry.arguments?.getString("mode")
            if (mode != null) {
                CreateNewPage(navController, mode)
            }
        }
        composable(
            "notesPage/{mode}",
            arguments = listOf(
                navArgument("mode") {type = NavType.StringType}
            )
        ) { backStackEntry ->
            val mode = backStackEntry.arguments?.getString("mode")
            if (mode != null) {
                NotesPage(navController, mode)
            }
        }
        composable(
            "graphPage/{mode}",
            arguments = listOf(
                navArgument("mode") {type = NavType.StringType}
            )
        ) { backStackEntry ->
            val mode = backStackEntry.arguments?.getString("mode")
            if (mode != null) {
                GraphPage(navController, mode)
            }
        }
        composable(
            "equationPage/{mode}",
            arguments = listOf(
                navArgument("mode") {type = NavType.StringType}
            )
        ) { backStackEntry ->
            val mode = backStackEntry.arguments?.getString("mode")
            if (mode != null) {
                EquationPage(navController, mode)
            }
        }
    }
}