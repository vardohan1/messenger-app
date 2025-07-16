package com.vdavitashviliekvitsiani.messenger_app.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.vdavitashviliekvitsiani.messenger_app.R
import com.vdavitashviliekvitsiani.messenger_app.model.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    user: User?,
    onSignOutClick: () -> Unit,
    onEditProfileClick: (nickname: String, profession: String) -> Unit,
    onBackToHomeClick: () -> Unit,
    isLoading: Boolean = false,
    isUpdating: Boolean = false,
    onFabClick: () -> Unit
) {
    var nickname by remember(user?.nickname) {
        mutableStateOf(user?.nickname ?: "")
    }
    var profession by remember(user?.profession) {
        mutableStateOf(user?.profession ?: "")
    }

    val hasChanges = remember(nickname, profession, user) {
        nickname != user?.nickname || profession != user?.profession
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = colorResource(id =  R.color.primary_blue)
                ),
                shape = RoundedCornerShape(0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(40.dp))

                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        if (user?.profileImageUrl?.isNotEmpty() == true) {
                            AsyncImage(
                                model = user.profileImageUrl,
                                contentDescription = "Profile picture",
                                modifier = Modifier
                                    .size(120.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(120.dp)
                                    .clip(CircleShape)
                                    .background(Color.Gray.copy(alpha = 0.3f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.avatar_image_placeholder),
                                    contentDescription = "Default profile picture",
                                    modifier = Modifier
                                        .size(120.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(40.dp))
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(32.dp))

                OutlinedTextField(
                    value = nickname,
                    onValueChange = { nickname = it },
                    label = { Text("Nickname") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = colorResource(id = R.color.background_gray),
                        unfocusedContainerColor = colorResource(id = R.color.background_gray),
                        focusedBorderColor = colorResource(id = R.color.primary_blue),
                        unfocusedBorderColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    enabled = !isUpdating
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = profession,
                    onValueChange = { profession = it },
                    label = { Text("Profession") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = colorResource(id = R.color.background_gray),
                        unfocusedContainerColor = colorResource(id = R.color.background_gray),
                        focusedBorderColor = colorResource(id = R.color.primary_blue),
                        unfocusedBorderColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    enabled = !isUpdating
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        onEditProfileClick(nickname, profession)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(id = R.color.primary_blue)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    enabled = hasChanges && !isUpdating && nickname.isNotBlank()
                ) {
                    if (isUpdating) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Text(
                            text = "Update",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = onSignOutClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = colorResource(id =  R.color.text_gray)
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        width = 1.dp,
                        color = colorResource(id =  R.color.border_gray)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = colorResource(id =  R.color.text_gray),
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Text(
                            text = "SIGN OUT",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(100.dp))
            }
        }

        Box(
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            com.vdavitashviliekvitsiani.messenger_app.ui.main.BottomNavigationBar(
                onHomeClick = onBackToHomeClick,
                onProfileClick = onFabClick,
                currentScreen = "profile"
            )
        }

        FloatingActionButton(
            onClick = onFabClick,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 96.dp, end = 16.dp),
            shape = CircleShape,
            containerColor = colorResource(id =  R.color.primary_blue)
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = "Add",
                tint = Color.White
            )
        }
    }
}