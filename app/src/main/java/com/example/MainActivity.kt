package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SplatDatabase
import com.example.data.SplatJob
import com.example.data.SplatRepository
import com.example.ui.SplatViewModel
import com.example.ui.SplatViewModelFactory
import com.example.ui.screens.ConfigScreen
import com.example.ui.screens.LibraryScreen
import com.example.ui.screens.RecordScreen
import com.example.ui.screens.SplatViewerScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 1. Initialize local repository and database singletons
        val database = SplatDatabase.getDatabase(applicationContext)
        val repository = SplatRepository(database.splatDao())

        setContent {
            MyApplicationTheme {
                // 2. Instantiate the centralized ViewModel
                val viewModel: SplatViewModel by viewModels {
                    SplatViewModelFactory(application, repository)
                }

                SplatAppShell(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun SplatAppShell(viewModel: SplatViewModel) {
    var currentTab by remember { mutableStateOf("library") } // "library", "record", "config"
    var activeViewerJob by remember { mutableStateOf<SplatJob?>(null) }

    val cleanClayBackground = com.example.ui.theme.CosmicBackground
    val softPurpleBg = com.example.ui.theme.CosmicCard
    val borderGray = com.example.ui.theme.CosmicBorder
    val brandPurple = com.example.ui.theme.NeonPurple
    val textPrimary = com.example.ui.theme.CosmicTextPrimary
    val neonCyan = com.example.ui.theme.NeonCyan

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = cleanClayBackground,
        bottomBar = {
            // Render custom elevated navigation bar unless the 3D Viewer overlay is active
            if (activeViewerJob == null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .wrapContentHeight()
                        .padding(top = 16.dp), // Extra headroom at the top to prevent clipping of the FAB
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .border(2.dp, borderGray, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
                        color = softPurpleBg,
                        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 12.dp),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Tab 1: Library
                            NavigationItem(
                                label = "Library",
                                icon = Icons.Outlined.GridView,
                                activeIcon = Icons.Filled.GridView,
                                isSelected = currentTab == "library",
                                onClick = { currentTab = "library" },
                                brandPurple = brandPurple,
                                textPrimary = textPrimary,
                                testTag = "nav_library"
                            )

                            // Central Spacer placeholder to clear space for the floating FAB
                            Spacer(modifier = Modifier.size(68.dp))

                            // Tab 2: Config Settings
                            NavigationItem(
                                label = "Config",
                                icon = Icons.Outlined.SettingsSuggest,
                                activeIcon = Icons.Filled.SettingsSuggest,
                                isSelected = currentTab == "config",
                                onClick = { currentTab = "config" },
                                brandPurple = brandPurple,
                                textPrimary = textPrimary,
                                testTag = "nav_config"
                            )
                        }
                    }

                    // Floating Record FAB above/overlapping the bottom bar
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 36.dp) // Float perfectly overlapping the top edge
                            .size(68.dp)
                            .border(
                                width = 3.dp,
                                color = if (currentTab == "record") brandPurple else borderGray,
                                shape = androidx.compose.foundation.shape.CircleShape
                            )
                            .padding(2.dp)
                            .clip(androidx.compose.foundation.shape.CircleShape)
                            .background(
                                color = brandPurple,
                                shape = androidx.compose.foundation.shape.CircleShape
                            )
                            .clickable { currentTab = "record" }
                            .testTag("nav_record"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Videocam,
                            contentDescription = "Scan/Record Video",
                            tint = cleanClayBackground, // high-contrast deep lavender on Coral Red background
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(cleanClayBackground)
        ) {
            // Main tab content switcher
            val bottomPadding = if (activeViewerJob == null) {
                val navBarPadding = androidx.compose.foundation.layout.WindowInsets.navigationBars
                    .asPaddingValues()
                    .calculateBottomPadding()
                80.dp + navBarPadding
            } else {
                0.dp
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = bottomPadding)
            ) {
                when (currentTab) {
                    "library" -> LibraryScreen(
                        viewModel = viewModel,
                        onViewSplat = { job -> activeViewerJob = job },
                        onNavigateToRecord = { currentTab = "record" },
                        onNavigateToConfig = { currentTab = "config" }
                    )
                    "record" -> RecordScreen(
                        viewModel = viewModel,
                        onSuccessUpload = { currentTab = "library" }
                    )
                    "config" -> ConfigScreen(
                        viewModel = viewModel
                    )
                }
            }

            // Fullscreen 3D WebGL Viewer overlay
            activeViewerJob?.let { job ->
                SplatViewerScreen(
                    job = job,
                    viewModel = viewModel,
                    onBack = { activeViewerJob = null },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
fun NavigationItem(
    label: String,
    icon: ImageVector,
    activeIcon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    brandPurple: Color,
    textPrimary: Color,
    testTag: String
) {
    val activeColor = com.example.ui.theme.NeonPurple
    val inactiveColor = com.example.ui.theme.CosmicTextSecondary
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .testTag(testTag)
    ) {
        Icon(
            imageVector = if (isSelected) activeIcon else icon,
            contentDescription = label,
            tint = if (isSelected) activeColor else inactiveColor,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            ),
            color = if (isSelected) activeColor else inactiveColor
        )
    }
}
