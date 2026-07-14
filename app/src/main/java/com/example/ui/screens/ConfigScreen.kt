package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ServerConfig
import com.example.ui.SplatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigScreen(
    viewModel: SplatViewModel,
    modifier: Modifier = Modifier
) {
    val serverConfigs by viewModel.serverConfigs.collectAsState()
    val activeConfig by viewModel.activeServerConfig.collectAsState()
    val isOnline by viewModel.isServerOnline.collectAsState()
    val isTesting by viewModel.isTestingConnection.collectAsState()

    var serverName by remember { mutableStateOf("") }
    var serverUrl by remember { mutableStateOf("") }
    var inputError by remember { mutableStateOf<String?>(null) }

    val focusManager = LocalFocusManager.current

    val cleanClayBackground = com.example.ui.theme.CosmicBackground
    val textPrimary = com.example.ui.theme.CosmicTextPrimary
    val brandPurple = com.example.ui.theme.NeonPurple
    val softPurpleBg = com.example.ui.theme.CosmicCard
    val borderGray = com.example.ui.theme.CosmicBorder
    val neonCyan = com.example.ui.theme.NeonCyan

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = cleanClayBackground,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Server Node Config",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = textPrimary
                        )
                        Text(
                            text = "VERSION 1.1 PIPELINE DIRECTORY",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.5.sp
                            ),
                            color = brandPurple
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = cleanClayBackground),
                actions = {
                    if (activeConfig != null) {
                        val isOnlineVal = isOnline
                        val badgeBg = if (isOnlineVal == true) Color(0xFF093325) else if (isOnlineVal == false) Color(0xFF421217) else Color(0xFF1C1833)
                        val badgeText = if (isOnlineVal == true) com.example.ui.theme.CosmicSuccess else if (isOnlineVal == false) com.example.ui.theme.CosmicError else com.example.ui.theme.CosmicTextSecondary
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = badgeBg,
                            modifier = Modifier.padding(end = 12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(
                                            color = badgeText,
                                            shape = CircleShape
                                        )
                                )
                                Text(
                                    text = if (isOnlineVal == true) "Active Node Online" else if (isOnlineVal == false) "Node Offline" else "Checking Node...",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Medium),
                                    color = badgeText
                                )
                            }
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // Server Node Form Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, borderGray, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = softPurpleBg),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "REGISTER NEW LOCAL / TUNNEL NODE",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            ),
                            color = brandPurple
                        )

                        OutlinedTextField(
                            value = serverName,
                            onValueChange = {
                                serverName = it
                                inputError = null
                            },
                            label = { Text("Server Name") },
                            placeholder = { Text("e.g. Office GPU Tower") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("server_name_input"),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            leadingIcon = { Icon(Icons.Outlined.Dns, contentDescription = null, tint = brandPurple) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = textPrimary,
                                unfocusedTextColor = textPrimary,
                                focusedBorderColor = neonCyan,
                                unfocusedBorderColor = borderGray,
                                focusedLabelColor = neonCyan,
                                unfocusedLabelColor = com.example.ui.theme.CosmicTextSecondary,
                                cursorColor = neonCyan
                            )
                        )

                        OutlinedTextField(
                            value = serverUrl,
                            onValueChange = {
                                serverUrl = it
                                inputError = null
                            },
                            label = { Text("Server URL or IP") },
                            placeholder = { Text("e.g. 192.168.1.42:8000 or ngrok-url") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("server_url_input"),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            leadingIcon = { Icon(Icons.Outlined.Link, contentDescription = null, tint = brandPurple) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = textPrimary,
                                unfocusedTextColor = textPrimary,
                                focusedBorderColor = neonCyan,
                                unfocusedBorderColor = borderGray,
                                focusedLabelColor = neonCyan,
                                unfocusedLabelColor = com.example.ui.theme.CosmicTextSecondary,
                                cursorColor = neonCyan
                            )
                        )

                        inputError?.let { error ->
                            Text(
                                text = error,
                                color = com.example.ui.theme.CosmicError,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }

                        Button(
                            onClick = {
                                focusManager.clearFocus()
                                if (serverName.isBlank() || serverUrl.isBlank()) {
                                    inputError = "Both fields are required."
                                } else {
                                    viewModel.addServerConfig(
                                        name = serverName.trim(),
                                        url = serverUrl.trim(),
                                        makeActive = serverConfigs.isEmpty() || activeConfig == null
                                    )
                                    serverName = ""
                                    serverUrl = ""
                                    inputError = null
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("add_server_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = brandPurple),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Filled.Add, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Register Server Node", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }

            // Connection tip/guide
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFF6B5100), RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1F1805)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = null,
                            tint = Color(0xFFFFD54F),
                            modifier = Modifier.size(20.dp)
                        )
                        Column {
                            Text(
                                text = "Cross-Network Hint (No Wifi Lock)",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFD54F)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "To process splats anywhere without sharing WiFi, run 'ngrok http 8000' (or your local server port) on your PC, then copy-paste the secure public HTTPS URL here.",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, lineHeight = 16.sp),
                                color = Color(0xFFFFD54F).copy(alpha = 0.85f)
                            )
                        }
                    }
                }
            }

            // Server node list header
            item {
                Text(
                    text = "REGISTERED SERVER NODES",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = textPrimary,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            if (serverConfigs.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Dns,
                                contentDescription = null,
                                tint = borderGray,
                                modifier = Modifier.size(56.dp)
                            )
                            Text(
                                text = "No server nodes registered yet",
                                style = MaterialTheme.typography.bodyMedium,
                                color = com.example.ui.theme.CosmicTextSecondary
                            )
                        }
                    }
                }
            } else {
                items(serverConfigs) { config ->
                    val isActive = config.id == activeConfig?.id
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(softPurpleBg)
                            .border(
                                width = if (isActive) 1.5.dp else 1.dp,
                                color = if (isActive) neonCyan else borderGray,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable {
                                viewModel.selectActiveServer(config.id)
                            }
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = config.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = textPrimary
                                    )
                                    if (isActive) {
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = brandPurple,
                                        ) {
                                            Text(
                                                text = "ACTIVE",
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    fontSize = 8.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White
                                                )
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = config.url,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = com.example.ui.theme.CosmicTextSecondary,
                                    maxLines = 1
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                // Ping Connection Test Button
                                IconButton(
                                    onClick = {
                                        viewModel.checkActiveServerConnection(config.url)
                                    },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    if (isActive && isTesting) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(18.dp),
                                            color = neonCyan,
                                            strokeWidth = 2.dp
                                        )
                                    } else {
                                        Icon(
                                            imageVector = Icons.Outlined.SignalWifiStatusbarConnectedNoInternet4,
                                            contentDescription = "Test connection",
                                            tint = if (isActive) neonCyan else com.example.ui.theme.CosmicTextSecondary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }

                                // Delete Button
                                IconButton(
                                    onClick = { viewModel.deleteServerConfig(config.id) },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Delete,
                                        contentDescription = "Delete Server",
                                        tint = com.example.ui.theme.CosmicError,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
