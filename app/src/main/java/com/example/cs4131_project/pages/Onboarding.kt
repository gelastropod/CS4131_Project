package com.example.cs4131_project.pages

import android.content.SharedPreferences
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.cs4131_project.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class Onboarding {
    companion object {
        @Composable
        fun Onboarding(navController: NavController, userPreferences: SharedPreferences) {
            val pagerState = rememberPagerState(pageCount = {3})
            val scrollScope = rememberCoroutineScope()

            HorizontalPager(state = pagerState) { page ->
                Column (
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row (
                        modifier = Modifier.fillMaxHeight(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        when (page) {
                            0 -> Onboarding1()
                            1 -> Onboarding2()
                            2 -> Onboarding3()
                        }
                    }
                    Row (
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (page != 0) {
                            Button(
                                onClick = {
                                    scrollScope.launch {
                                        pagerState.scrollToPage(page - 1)
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(0.4f)
                            ) {
                                Text("Previous")
                            }
                        }
                        if (page != 2) {
                            Button(
                                onClick = {
                                    scrollScope.launch {
                                        pagerState.scrollToPage(page + 1)
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(0.4f)
                            ) {
                                Text("Next")
                            }
                        }
                        else {
                            Button(
                                onClick = {
                                    val editor = userPreferences.edit()
                                    editor.putBoolean("showOnboarding", false)
                                    editor.apply()

                                    navController.navigate("homePage") {
                                        popUpTo("onBoardingPage") { inclusive = true }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(0.4f)
                            ) {
                                Text("Done")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Onboarding1() {
    Column (
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Welcome to\nGraphium!",
            style = MaterialTheme.typography.titleLarge
        )
        Image(
            painter = painterResource(R.drawable.app_icon),
            contentDescription = "CS4131 image funny",
            modifier = Modifier.fillMaxWidth(0.8f)
        )
        Text(
            text = "Your all-in-one math workspace",
            modifier = Modifier.fillMaxWidth(0.8f)
        )
        Text(
            text = "Create · Graph · Learn"
        )
    }
}

@Composable
fun Onboarding2() {

}

@Composable
fun Onboarding3() {

}