package com.vdavitashviliekvitsiani.messenger_app.ui.auth

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.background_gray))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(100.dp))

        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(colorResource(id = R.color.yellow_circle)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "👤",
                fontSize = 40.sp
            )
        }

        Spacer(modifier = Modifier.height(50.dp))

        OutlinedTextField(
            value = nickname,
            onValueChange = { nickname = it },
            label = { Text(stringResource(R.string.enter_nickname)) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colorResource(id = R.color.primary_blue),
                unfocusedBorderColor = colorResource(id = R.color.border_gray)
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(stringResource(R.string.enter_password)) },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colorResource(id = R.color.primary_blue),
                unfocusedBorderColor = colorResource(id = R.color.border_gray)
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { onSignInClick(nickname, password) },
            modifier = Modifier
                .height(48.dp)
                .widthIn(min = 120.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(id = R.color.primary_blue)
            ),
            shape = RoundedCornerShape(8.dp),
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
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        errorMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.not_registered),
            color = colorResource(id = R.color.text_gray)
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = onSignUpClick,
            modifier = Modifier
                .height(48.dp)
                .widthIn(min = 120.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = colorResource(id = R.color.primary_blue)
            ),
            border = BorderStroke(
                width = 1.dp,
                color = colorResource(id = R.color.primary_blue)
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(stringResource(R.string.sign_up))
        }
    }
}