package com.vdavitashviliekvitsiani.messenger_app.ui.main

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.vdavitashviliekvitsiani.messenger_app.R
import com.vdavitashviliekvitsiani.messenger_app.model.Conversation
import com.vdavitashviliekvitsiani.messenger_app.util.toTimeAgo

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun HomeScreen(
    conversations: List<Conversation> = emptyList(),
    searchQuery: String = "",
    onConversationClick: (Conversation) -> Unit = {},
    onAddConversationClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onSearchUsersClick: () -> Unit = {},
    isLoading: Boolean = false
) {
    val listState = rememberLazyListState()
    rememberCoroutineScope()

    val firstVisibleItemIndex by remember { derivedStateOf { listState.firstVisibleItemIndex } }
    val firstVisibleItemScrollOffset by remember { derivedStateOf { listState.firstVisibleItemScrollOffset } }

    val isHeaderCollapsed = remember(firstVisibleItemIndex, firstVisibleItemScrollOffset) {
        firstVisibleItemIndex > 0 || firstVisibleItemScrollOffset > 100
    }

    var showBottomNav by remember { mutableStateOf(true) }

    LaunchedEffect(listState.isScrollInProgress) {
        showBottomNav = !listState.isScrollInProgress
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colorResource(id = R.color.background_gray))
        ) {
            AnimatedContent(
                targetState = isHeaderCollapsed,
                transitionSpec = {
                    fadeIn(animationSpec = tween(300)) with
                            fadeOut(animationSpec = tween(300))
                }
            ) { collapsed ->
                if (collapsed) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = colorResource(id = R.color.primary_blue)
                        ),
                        shape = RoundedCornerShape(0.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onSearchUsersClick() }
                            ) {
                                OutlinedTextField(
                                    value = searchQuery,
                                    onValueChange = {},
                                    placeholder = { Text("Search", fontSize = 14.sp) },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.Search,
                                            contentDescription = "Search",
                                            tint = Color.Gray,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(20.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        disabledBorderColor = Color.Transparent,
                                        disabledContainerColor = Color.White,
                                        disabledPlaceholderColor = Color.Gray,
                                        disabledLeadingIconColor = Color.Gray
                                    ),
                                    enabled = false,
                                    singleLine = true
                                )
                            }
                        }
                    }
                } else {
                    Box {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = colorResource(id = R.color.primary_blue)
                            ),
                            shape = RoundedCornerShape(
                                bottomStart = 16.dp,
                                bottomEnd = 16.dp
                            )
                        ) {
                            Box {
                                Image(
                                    painter = painterResource(id = R.drawable.background),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(180.dp),
                                    contentScale = ContentScale.FillBounds
                                )

                                Column(
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .height(180.dp)
                                ) {
                                    Spacer(modifier = Modifier.weight(1f))
                                    Box(
                                        modifier = Modifier
                                            .clickable { onSearchUsersClick() }
                                    ) {
                                        OutlinedTextField(
                                            value = searchQuery,
                                            onValueChange = {},
                                            placeholder = { Text("Search") },
                                            leadingIcon = {
                                                Icon(
                                                    Icons.Default.Search,
                                                    contentDescription = "Search",
                                                    tint = Color.Gray
                                                )
                                            },
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(25.dp),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                disabledBorderColor = Color.Transparent,
                                                disabledContainerColor = Color.White,
                                                disabledPlaceholderColor = Color.Gray,
                                                disabledLeadingIconColor = Color.Gray
                                            ),
                                            enabled = false
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))
                                }
                            }
                        }
                    }
                }
            }

            Box(modifier = Modifier.weight(1f)) {
                when {
                    isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = colorResource(id = R.color.primary_blue)
                            )
                        }
                    }

                    conversations.isEmpty() -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "No conversations yet",
                                    color = colorResource(id = R.color.text_gray),
                                    fontSize = 16.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Start a new conversation",
                                    color = colorResource(id = R.color.text_gray),
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }

                    else -> {
                        LazyColumn(
                            state = listState,
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(
                                top = 8.dp,
                                bottom = 88.dp
                            )
                        ) {
                            items(conversations) { conversation ->
                                ConversationItem(
                                    conversation = conversation,
                                    onClick = { onConversationClick(conversation) }
                                )
                            }
                        }
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = showBottomNav,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it }),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            BottomNavigationBar(
                onHomeClick = { },
                onProfileClick = onProfileClick,
                currentScreen = "home"
            )
        }

        AnimatedVisibility(
            visible = true,
            enter = scaleIn(),
            exit = scaleOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(
                    bottom = 96.dp,
                    end = 16.dp
                )
        ) {
            FloatingActionButton(
                onClick = onAddConversationClick,
                containerColor = colorResource(id = R.color.primary_blue),
                shape = CircleShape
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add conversation",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun ConversationItem(
    conversation: Conversation,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box {
                if (conversation.otherUserProfileUrl.isNotEmpty()) {
                    AsyncImage(
                        model = conversation.otherUserProfileUrl,
                        contentDescription = "Profile picture",
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(colorResource(id = R.color.border_gray))
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(colorResource(id = R.color.yellow_circle)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "Default avatar",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                if (conversation.unreadCount > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(Color.Red),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (conversation.unreadCount > 9) "9+" else conversation.unreadCount.toString(),
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = conversation.otherUserNickname,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.Black,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = conversation.lastMessageTime.toTimeAgo(),
                        fontSize = 12.sp,
                        color = colorResource(id = R.color.text_gray),
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                Text(
                    text = conversation.lastMessage.ifEmpty { "Start a conversation" },
                    fontSize = 14.sp,
                    color = colorResource(id = R.color.text_gray),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
fun BottomNavigationBar(
    onHomeClick: () -> Unit,
    onProfileClick: () -> Unit,
    currentScreen: String
) {
    NavigationBar(
        containerColor = Color.White,
        modifier = Modifier.height(80.dp)
    ) {
        NavigationBarItem(
            selected = currentScreen == "home",
            onClick = onHomeClick,
            icon = {
                Icon(
                    Icons.Default.Home,
                    contentDescription = "Home"
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = colorResource(id = R.color.primary_blue),
                unselectedIconColor = Color.Gray,
                indicatorColor = Color.Transparent
            )
        )

        NavigationBarItem(
            selected = currentScreen == "profile",
            onClick = onProfileClick,
            icon = {
                Icon(
                    Icons.Default.Settings,
                    contentDescription = "Settings"
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = colorResource(id = R.color.primary_blue),
                unselectedIconColor = Color.Gray,
                indicatorColor = Color.Transparent
            )
        )
    }
}