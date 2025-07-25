package com.vdavitashviliekvitsiani.messenger_app.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vdavitashviliekvitsiani.messenger_app.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignInScreen(
    onSignInClick: (String, String) -> Unit,
    onSignUpClick: () -> Unit,
    isLoading: Boolean = false,
    errorMessage: String? = null
) {
    var nickname by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(120.dp))

            Image(
                painter = painterResource(id = R.drawable.avatar_image_placeholder),
                contentDescription = "Profile placeholder",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(60.dp))

            OutlinedTextField(
                value = nickname,
                onValueChange = { nickname = it },
                label = { Text(stringResource(R.string.enter_nickname)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colorResource(id = R.color.primary_blue),
                    unfocusedBorderColor = colorResource(id = R.color.border_gray),
                    focusedLabelColor = colorResource(id = R.color.primary_blue)
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(stringResource(R.string.enter_password)) },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colorResource(id = R.color.primary_blue),
                    unfocusedBorderColor = colorResource(id = R.color.border_gray),
                    focusedLabelColor = colorResource(id = R.color.primary_blue)
                )
            )

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = { onSignInClick(nickname, password) },
                modifier = Modifier
                    .width(150.dp)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.primary_blue)
                ),
                shape = RoundedCornerShape(25.dp),
                enabled = !isLoading && nickname.isNotBlank() && password.isNotBlank()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text(
                        text = stringResource(R.string.sign_in),
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            errorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(vertical = 8.dp),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = stringResource(R.string.not_registered),
                color = colorResource(id = R.color.text_gray),
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            TextButton(
                onClick = onSignUpClick
            ) {
                Text(
                    text = stringResource(R.string.sign_up),
                    color = colorResource(id = R.color.primary_blue),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}