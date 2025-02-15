package com.example.cs4131_project.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getString
import androidx.navigation.NavController
import com.example.cs4131_project.R
import com.example.cs4131_project.components.NoToolbarWrapper.Companion.NoToolbarWrapper

class SignUpPage {
    companion object {
        @Composable
        fun SignUpPage(navController: NavController, mode: String) {
            val context = LocalContext.current
            var username by remember { mutableStateOf("") }
            var password by remember { mutableStateOf("") }
            var isPasswordVisible by remember { mutableStateOf(false) }

            NoToolbarWrapper(navController, getString(context, R.string.signUpPageTitle)) {
                Column (
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(50.dp))
                    TextField(
                        value = username,
                        onValueChange = { newText -> username = newText },
                        placeholder = { Text(getString(context, R.string.signUpPage1)) },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Done
                        )
                    )
                    Spacer(modifier = Modifier.height(15.dp))
                    TextField(
                        value = password,
                        onValueChange = { newText -> password = if (!newText.contains("\n")) newText else password },
                        placeholder = { Text(getString(context, R.string.signUpPage2)) },
                        visualTransformation = if (isPasswordVisible) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Done
                        ),
                        trailingIcon = {
                            IconButton(onClick = {
                                isPasswordVisible = !isPasswordVisible
                            }) {
                                Icon(
                                    painter = if (isPasswordVisible) {
                                        painterResource(R.drawable.eye)
                                    } else {
                                        painterResource(R.drawable.eye_off)
                                    },
                                    contentDescription = "Toggle password visibility"
                                )
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(15.dp))
                    TextField(
                        value = password,
                        onValueChange = { newText -> password = if (!newText.contains("\n")) newText else password },
                        placeholder = { Text(getString(context, R.string.signUpPage2)) },
                        visualTransformation = if (isPasswordVisible) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Done
                        ),
                        trailingIcon = {
                            IconButton(onClick = {
                                isPasswordVisible = !isPasswordVisible
                            }) {
                                Icon(
                                    painter = if (isPasswordVisible) {
                                        painterResource(R.drawable.eye)
                                    } else {
                                        painterResource(R.drawable.eye_off)
                                    },
                                    contentDescription = "Toggle password visibility"
                                )
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(15.dp))
                    Button(
                        onClick = {

                        }
                    ) {
                        Text(getString(context, R.string.signUpPage3))
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    ClickableText(
                        buildAnnotatedString {
                            withStyle(
                                style = MaterialTheme.typography.bodyLarge.toSpanStyle().copy(
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            ) {
                                append(getString(context, R.string.signUpPage4_1))
                            }

                            pushStringAnnotation(tag = "CREATE_ACCOUNT", annotation = "create_account")
                            withStyle(
                                style = MaterialTheme.typography.bodyLarge.toSpanStyle().copy(
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold,
                                    textDecoration = TextDecoration.Underline
                                )
                            ) {
                                append(getString(context, R.string.signUpPage4_2))
                            }
                            pop()
                        },
                        onClick = {
                            navController.navigate("signInPage/$mode")
                        }
                    )
                }
            }
        }
    }
}