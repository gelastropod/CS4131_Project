package com.example.cs4131_project.pages.misc

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getString
import androidx.navigation.NavController
import com.example.cs4131_project.R
import kotlinx.coroutines.launch

@Composable
fun Onboarding(navController: NavController, userPreferences: SharedPreferences) {
    val pagerState = rememberPagerState(pageCount = { 2 })
    val scrollScope = rememberCoroutineScope()
    val context = LocalContext.current

    HorizontalPager(state = pagerState) { page ->
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                when (page) {
                    0 -> Onboarding1(context)
                    1 -> Onboarding2(context)
                    //2 -> Onboarding3(context)
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (page != 0) {
                    Button(
                        onClick = {
                            scrollScope.launch {
                                pagerState.scrollToPage(page - 1)
                            }
                        },
                        modifier = Modifier.width(150.dp)
                    ) {
                        Text("Previous")
                    }
                } else {
                    Button(
                        onClick = {
                            scrollScope.launch {
                                pagerState.scrollToPage(page - 1)
                            }
                        },
                        modifier = Modifier.width(150.dp),
                        enabled = false
                    ) {
                        Text("Previous")
                    }
                }
                Spacer(modifier = Modifier.width(20.dp))
                if (page != 2) {
                    Button(
                        onClick = {
                            scrollScope.launch {
                                pagerState.scrollToPage(page + 1)
                            }
                        },
                        modifier = Modifier.width(150.dp)
                    ) {
                        Text("Next")
                    }
                } else {
                    Button(
                        onClick = {
                            val editor = userPreferences.edit()
                            editor.putBoolean("showOnboarding", false)
                            editor.apply()

                            navController.navigate("homePage") {
                                popUpTo("onBoardingPage") { inclusive = true }
                            }
                        },
                        modifier = Modifier.width(150.dp)
                    ) {
                        Text("Done")
                    }
                }
            }
        }
    }
}

@Composable
private fun Onboarding1(context: Context) {
    Column (
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = getString(context, R.string.onboarding1_1),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )
        Image(
            painter = painterResource(R.drawable.app_icon),
            contentDescription = "CS4131 image funny",
            modifier = Modifier.fillMaxWidth(0.9f)
        )
        Text(
            text = getString(context, R.string.onboarding1_2),
            modifier = Modifier.fillMaxWidth(0.9f),
            textAlign = TextAlign.Center
        )
        Text(
            text = getString(context, R.string.onboarding1_3),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun Onboarding2(context: Context) {
    Column (
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = getString(context, R.string.onboarding2_1),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(0.dp))
        Text(
            buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(getString(context, R.string.onboarding2_2_1))
                }
                append(getString(context, R.string.onboarding2_2_2))

                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(getString(context, R.string.onboarding2_2_3))
                }
                append(getString(context, R.string.onboarding2_2_4))

                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(getString(context, R.string.onboarding2_2_5))
                }
                append(getString(context, R.string.onboarding2_2_6))
            },
            modifier = Modifier.fillMaxWidth(0.9f)
        )
    }
}

@Composable
private fun Onboarding3(context: Context) {
    Column (
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Placeholder for more Onboarding content")
    }
}