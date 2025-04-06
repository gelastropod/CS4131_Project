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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.compose.AppTheme
import com.example.cs4131_project.model.firestoreModels.FirestoreHandler
import com.example.cs4131_project.model.firestoreModels.GlobalDatastore
import com.example.cs4131_project.model.graph.GraphViewModel
import com.example.cs4131_project.pages.dashboardPages.ClassDashboardPage
import com.example.cs4131_project.pages.dashboardPages.ClassDetailsPage
import com.example.cs4131_project.pages.dashboardPages.CreateClassPage
import com.example.cs4131_project.pages.dashboardPages.CreateNewPage
import com.example.cs4131_project.pages.contentPages.EquationPage
import com.example.cs4131_project.pages.contentPages.GraphPage
import com.example.cs4131_project.pages.dashboardPages.StudentClassListPage
import com.example.cs4131_project.pages.noToolbarPages.HomePage
import com.example.cs4131_project.pages.dashboardPages.JoinClassPage
import com.example.cs4131_project.pages.contentPages.NotesPage
import com.example.cs4131_project.pages.misc.Onboarding
import com.example.cs4131_project.pages.contentPages.EquationEditorPage
import com.example.cs4131_project.pages.dashboardPages.PersonalDashboardPage
import com.example.cs4131_project.pages.dashboardPages.SettingsPage
import com.example.cs4131_project.pages.noToolbarPages.SignInPage
import com.example.cs4131_project.pages.noToolbarPages.SignUpPage
import com.example.cs4131_project.pages.dashboardPages.StudentDashboardPage
import com.example.cs4131_project.pages.dashboardPages.TeacherClassListPage
import com.example.cs4131_project.pages.dashboardPages.TeacherDashboardPage
import com.example.cs4131_project.pages.misc.RedirectPage
import com.example.cs4131_project.pages.noToolbarPages.ModeChoosePage
import com.google.firebase.firestore.FirebaseFirestore

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

        val db = FirebaseFirestore.getInstance()
        val handler = FirestoreHandler(
            db.collection("mainData").document("userData"),
            db.collection("mainData").document("classData"),
            db.collection("mainData").document("classIDs")
        )

        sharedPreferences = applicationContext.getSharedPreferences(PREF_KEY, MODE_PRIVATE)
        GlobalDatastore.sharedPreferences = sharedPreferences

        darkThemeState.value = isSystemInDarkMode(applicationContext)

        setContent {
            AppTheme(darkTheme = darkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    MainApp(resources, applicationContext, handler)
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
fun MainApp(resources: Resources, context: Context, handler: FirestoreHandler) {
    val navController = rememberNavController()
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    var showOnboarding by remember {mutableStateOf(MainActivity.sharedPreferences.getBoolean("showOnboarding", true))}
    val graphViewModel: GraphViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = if (showOnboarding) "onboardingPage" else {
            if (GlobalDatastore.username.value.isEmpty()) "homePage"
            else {
                val userAccount = handler.data[GlobalDatastore.username.value]
                if (userAccount != null) {
                    userAccount.usage + "DashboardPage"
                }
                else {
                    "homePage"
                }
            }
        },
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
        composable("signInPage") { SignInPage(navController, handler) }
        composable("signUpPage") { SignUpPage(navController, handler) }
        composable("personalDashboardPage") { PersonalDashboardPage(navController, handler, graphViewModel)}
        composable("studentDashboardPage") { StudentDashboardPage(navController, handler, graphViewModel)}
        composable("teacherDashboardPage") { TeacherDashboardPage(navController, handler, graphViewModel)}
        composable(
            "classDashboardPage/{mode}/{className}",
            arguments = listOf(
                navArgument("mode") {type = NavType.StringType},
                navArgument("className") {type = NavType.StringType}
            )
        ) { backStackEntry ->
            val mode = backStackEntry.arguments?.getString("mode")
            val className = backStackEntry.arguments?.getString("className")
            if (mode != null && className != null) {
                ClassDashboardPage(navController, handler, graphViewModel, mode, className)
            }
        }
        composable("studentClassListPage") { StudentClassListPage(navController, handler) }
        composable("teacherClassListPage") { TeacherClassListPage(navController, handler) }
        composable(
            "classDetailsPage/{mode}/{className}",
            arguments = listOf(
                navArgument("mode") {type = NavType.StringType},
                navArgument("className") {type = NavType.StringType}
            )
        ) { backStackEntry ->
            val mode = backStackEntry.arguments?.getString("mode")
            val className = backStackEntry.arguments?.getString("className")
            if (mode != null && className != null) {
                ClassDetailsPage(navController, mode, handler, className)
            }
        }
        composable("joinClassPage") {JoinClassPage(navController, handler)}
        composable("createClassPage") {CreateClassPage(navController, handler)}
        composable(
            "settingsPage/{mode}",
            arguments = listOf(
                navArgument("mode") {type = NavType.StringType}
            )
        ) { backStackEntry ->
            val mode = backStackEntry.arguments?.getString("mode")
            if (mode != null) {
                SettingsPage(navController, mode, handler)
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
                CreateNewPage(navController, mode, handler)
            }
        }
        composable(
            "notesPage/{mode}/{json}/{name}",
            arguments = listOf(
                navArgument("mode") {type = NavType.StringType},
                navArgument("json") {type = NavType.StringType},
                navArgument("name") {type = NavType.StringType}
            )
        ) { backStackEntry ->
            val mode = backStackEntry.arguments?.getString("mode")
            val json = backStackEntry.arguments?.getString("json")
            val name = backStackEntry.arguments?.getString("name")
            if (mode != null && json != null && name != null) {
                NotesPage(navController, mode, json, handler, name)
            }
        }
        composable(
            "graphPage/{mode}/{name}",
            arguments = listOf(
                navArgument("mode") {type = NavType.StringType},
                navArgument("name") {type = NavType.StringType}
            )
        ) { backStackEntry ->
            val mode = backStackEntry.arguments?.getString("mode")
            val name = backStackEntry.arguments?.getString("name")
            if (mode != null && name != null) {
                GraphPage(navController, mode, graphViewModel, handler, name)
            }
        }
        composable(
            "equationPage/{mode}/{name}",
            arguments = listOf(
                navArgument("mode") {type = NavType.StringType},
                navArgument("name") {type = NavType.StringType}
            )
        ) { backStackEntry ->
            val mode = backStackEntry.arguments?.getString("mode")
            val name = backStackEntry.arguments?.getString("name")
            if (mode != null && name != null) {
                EquationPage(navController, mode, graphViewModel, handler, name)
            }
        }
        composable(
            "equationEditorPage/{mode}/{index}/{name}",
            arguments = listOf(
                navArgument("mode") {type = NavType.StringType},
                navArgument("index") {type = NavType.StringType},
                navArgument("name") {type = NavType.StringType}
            )
        ) {backStackEntry ->
            val mode = backStackEntry.arguments?.getString("mode")
            val index = backStackEntry.arguments?.getString("index")
            val name = backStackEntry.arguments?.getString("name")
            if (mode != null && index != null && name != null) {
                EquationEditorPage(navController, mode, index.toInt(), graphViewModel, handler, name)
            }
        }
        composable(
            "redirectPage/{fileName}/{fileContent}",
            arguments = listOf(
                navArgument("fileName") {type = NavType.StringType},
                navArgument("fileContent") {type = NavType.StringType}
            )
        ) { backStackEntry ->
            val fileName = backStackEntry.arguments?.getString("fileName")
            val fileContent = backStackEntry.arguments?.getString("fileContent")
            if (fileName != null && fileContent != null) {
                RedirectPage(navController, fileName, fileContent, handler)
            }
        }
        composable(
            "modeChoosePage/{username}/{password}",
            arguments = listOf(
                navArgument("username") {type = NavType.StringType},
                navArgument("password") {type = NavType.StringType}
            )
        ) { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username")
            val password = backStackEntry.arguments?.getString("password")
            if (username != null && password != null) {
                ModeChoosePage(navController, handler, username, password)
            }
        }
    }
}