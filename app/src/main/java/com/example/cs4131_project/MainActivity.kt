package com.example.cs4131_project

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
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
import com.example.cs4131_project.model.graph.Graph3ViewModel
import com.example.cs4131_project.model.graph.GraphViewModel
import com.example.cs4131_project.pages.contentPages.EquationEditor3Page
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
import com.example.cs4131_project.pages.contentPages.Graph3Page
import com.example.cs4131_project.pages.dashboardPages.InformationPage
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
import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.net.Uri
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getString

@Composable
fun RequestNotificationPermission() {
    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                Toast.makeText(context, getString(context, R.string.mainActivity1), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, getString(context, R.string.mainActivity2), Toast.LENGTH_SHORT).show()
            }
        }
    )

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !NotificationManagerCompat.from(context).areNotificationsEnabled()) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}

class MainActivity : ComponentActivity() {
    companion object {
        lateinit var sharedPreferences: SharedPreferences
        val PREF_KEY = "CS4131_PROJECT_KEY"
        var darkThemeState = mutableStateOf(false)
        var darkTheme: Boolean = false
            get() = darkThemeState.value
        var dynamicColorState = mutableStateOf(false)
        var dynamicColor: Boolean = false
            get() = dynamicColorState.value
        var channelID = 0

        fun createNotificationChannel(context: Context) {
            val name = "MyChannel"
            val descriptionText = "My notification channel"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("my_channel_id", name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        fun sendNotification(context: Context, title: String, content: String) {
            val builder = NotificationCompat.Builder(context, "my_channel_id")
                .setSmallIcon(R.drawable.app_icon)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            with(NotificationManagerCompat.from(context)) {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    notify(channelID, builder.build())
                }
            }
        }
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
            db.collection("mainData").document("classData")
        )

        sharedPreferences = applicationContext.getSharedPreferences(PREF_KEY, MODE_PRIVATE)
        GlobalDatastore.sharedPreferences = sharedPreferences

        dynamicColorState.value = sharedPreferences.getBoolean("dynamicColor", false)

        darkThemeState.value = sharedPreferences.getBoolean("darkTheme", isSystemInDarkMode(applicationContext))

        setContent {
            AppTheme(darkTheme = darkTheme, dynamicColor = dynamicColor) {
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
    val graph3ViewModel: Graph3ViewModel = viewModel()

    RequestNotificationPermission()

    LaunchedEffect(Unit) {
        MainActivity.createNotificationChannel(context)
    }

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
                })
            }
    ) {
        composable("onboardingPage") { Onboarding(navController, MainActivity.sharedPreferences) }
        composable("homePage") { HomePage(navController) }
        composable("signInPage") { SignInPage(navController, handler) }
        composable("signUpPage") { SignUpPage(navController, handler) }
        composable("personalDashboardPage") { PersonalDashboardPage(navController, handler, graphViewModel, graph3ViewModel)}
        composable("studentDashboardPage") { StudentDashboardPage(navController, handler, graphViewModel, graph3ViewModel)}
        composable("teacherDashboardPage") { TeacherDashboardPage(navController, handler, graphViewModel, graph3ViewModel)}
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
                ClassDashboardPage(navController, handler, graphViewModel, graph3ViewModel, mode, className)
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
                CreateNewPage(navController, mode, handler, graphViewModel, graph3ViewModel)
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
            val fileContent = Uri.decode(backStackEntry.arguments?.getString("fileContent") ?: "")
            if (fileName != null) {
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
        composable(
            "informationPage/{mode}",
            arguments = listOf(
                navArgument("mode") {type = NavType.StringType}
            )
        ) {backStackEntry ->
            val mode = backStackEntry.arguments?.getString("mode")
            if (mode != null) {
                InformationPage(navController, mode, handler)
            }
        }
        composable(
            "graph3Page/{mode}/{name}",
            arguments = listOf(
                navArgument("mode") {type = NavType.StringType},
                navArgument("name") {type = NavType.StringType}
            )
        ) { backStackEntry ->
            val mode = backStackEntry.arguments?.getString("mode")
            val name = backStackEntry.arguments?.getString("name")
            if (mode != null && name != null) {
                Graph3Page(navController, mode, graph3ViewModel, handler, name)
            }
        }
        composable(
            "equationEditor3Page/{mode}/{name}",
            arguments = listOf(
                navArgument("mode") {type = NavType.StringType},
                navArgument("name") {type = NavType.StringType}
            )
        ) {backStackEntry ->
            val mode = backStackEntry.arguments?.getString("mode")
            val name = backStackEntry.arguments?.getString("name")
            if (mode != null && name != null) {
                EquationEditor3Page(navController, mode, graph3ViewModel, handler, name)
            }
        }
    }
}