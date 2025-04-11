package com.example.cs4131_project.pages.noToolbarPages

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getString
import androidx.navigation.NavController
import com.example.cs4131_project.R
import com.example.cs4131_project.components.wrappers.NoToolbarWrapper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(navController: NavController) {
    val context = LocalContext.current
    val carouselMultiBrowseState = rememberCarouselState {3}

    NoToolbarWrapper(navController, getString(context, R.string.homePageTitle)) {
        Box(
            modifier = Modifier.fillMaxSize().padding(10.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                HorizontalMultiBrowseCarousel(
                    state = carouselMultiBrowseState,
                    preferredItemWidth = 250.dp,
                    itemSpacing = 19.dp
                ) { index ->
                    when (index) {
                        0 -> {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.notes),
                                    contentDescription = "Epic Notes"
                                )
                                Text(getString(context, R.string.homePage6), modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                            }
                        }
                        1 -> {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.graph),
                                    contentDescription = "Epic Graph"
                                )
                                Text(getString(context, R.string.homePage7), modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                            }
                        }
                        2 -> {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.graph2),
                                    contentDescription = "Epic Graph 2"
                                )
                                Text(getString(context, R.string.homePage8), modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(100.dp))
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        navController.navigate("signInPage")
                    }
                ) {
                    Text(getString(context, R.string.homePage4))
                }
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        navController.navigate("signUpPage")
                    }
                ) {
                    Text(getString(context, R.string.homePage5))
                }
            }
        }
    }
}