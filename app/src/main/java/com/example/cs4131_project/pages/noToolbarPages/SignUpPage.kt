package com.example.cs4131_project.pages.noToolbarPages

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.graphics.Color
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
import com.example.cs4131_project.components.wrappers.NoToolbarWrapper
import com.example.cs4131_project.model.firestoreModels.FirestoreHandler
import com.example.cs4131_project.model.firestoreModels.GlobalDatastore
import com.example.cs4131_project.model.firestoreModels.SavedItem
import com.example.cs4131_project.model.firestoreModels.UserAccount

@Composable
fun SignUpPage(navController: NavController, handler: FirestoreHandler) {
    val context = LocalContext.current
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isConfirmPasswordVisible by remember { mutableStateOf(false) }

    var req1 by remember{ mutableStateOf(false)}
    var req2 by remember{ mutableStateOf(false)}
    var req3 by remember{ mutableStateOf(false)}
    var req4 by remember{ mutableStateOf(true)}

    NoToolbarWrapper(navController, getString(context, R.string.signUpPageTitle)) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(5.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))
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
                onValueChange = { newText ->
                    password = if (!newText.contains("\n")) newText else password

                    req1 = password.length >= 8
                    req2 = password.contains("[a-z]".toRegex()) && password.contains("[A-Z]".toRegex()) && password.contains("\\d".toRegex())
                    req3 = password.contains("[!@#\\\\\$%^&*()]".toRegex())
                    req4 = !password.contains(" ".toRegex())
                },
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
                value = confirmPassword,
                onValueChange = { newText ->
                    confirmPassword = if (!newText.contains("\n")) newText else confirmPassword
                },
                placeholder = { Text(getString(context, R.string.signUpPage7)) },
                visualTransformation = if (isConfirmPasswordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                trailingIcon = {
                    IconButton(onClick = {
                        isConfirmPasswordVisible = !isConfirmPasswordVisible
                    }) {
                        Icon(
                            painter = if (isConfirmPasswordVisible) {
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(30.dp))
                if (req1) {
                    Icon(
                        painter = painterResource(R.drawable.check),
                        tint = Color.Green,
                        contentDescription = "Close"
                    )
                }
                else {
                    Icon(
                        painter = painterResource(R.drawable.close),
                        tint = Color.Red,
                        contentDescription = "Close"
                    )
                }
                Text(
                    text = getString(context, R.string.signUpPage8),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = if (req1) Color.Green else Color.Red
                    )
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(30.dp))
                if (req2) {
                    Icon(
                        painter = painterResource(R.drawable.check),
                        tint = Color.Green,
                        contentDescription = "Close"
                    )
                }
                else {
                    Icon(
                        painter = painterResource(R.drawable.close),
                        tint = Color.Red,
                        contentDescription = "Close"
                    )
                }
                Text(
                    text = getString(context, R.string.signUpPage9),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = if (req2) Color.Green else Color.Red
                    )
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(30.dp))
                if (req3) {
                    Icon(
                        painter = painterResource(R.drawable.check),
                        tint = Color.Green,
                        contentDescription = "Close"
                    )
                }
                else {
                    Icon(
                        painter = painterResource(R.drawable.close),
                        tint = Color.Red,
                        contentDescription = "Close"
                    )
                }
                Text(
                    text = getString(context, R.string.signUpPage10),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = if (req3) Color.Green else Color.Red
                    )
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(30.dp))
                if (req4) {
                    Icon(
                        painter = painterResource(R.drawable.check),
                        tint = Color.Green,
                        contentDescription = "Close"
                    )
                }
                else {
                    Icon(
                        painter = painterResource(R.drawable.close),
                        tint = Color.Red,
                        contentDescription = "Close"
                    )
                }
                Text(
                    text = getString(context, R.string.signUpPage11),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = if (req4) Color.Green else Color.Red
                    )
                )
            }
            Spacer(modifier = Modifier.height(15.dp))
            Button(
                onClick = {
                    if (handler.data.containsKey(username)) {
                        Toast.makeText(context, getString(context, R.string.signUpPage5), Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    if (password != confirmPassword) {
                        Toast.makeText(context, getString(context, R.string.signUpPage6), Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    if (!(req1 && req2 && req3 && req4)) {
                        Toast.makeText(context, getString(context, R.string.signUpPage12), Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    navController.navigate("modeChoosePage/$username/$password")
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
                    navController.navigate("signInPage")
                }
            )
        }
    }
}