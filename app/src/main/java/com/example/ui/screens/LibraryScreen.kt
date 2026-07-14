package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SplatJob
import com.example.ui.SplatViewModel
import com.example.network.RenderQuality
import com.example.network.AuditResponse
import com.example.network.NetworkClient
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    viewModel: SplatViewModel,
    onViewSplat: (SplatJob) -> Unit,
    onNavigateToRecord: () -> Unit,
    onNavigateToConfig: () -> Unit,
    modifier: Modifier = Modifier
) {
    val splatJobs by viewModel.splatJobs.collectAsState()
    val activeConfig by viewModel.activeServerConfig.collectAsState()
    val isOnline by viewModel.isServerOnline.collectAsState()

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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            modifier = Modifier.size(36.dp),
                            shape = RoundedCornerShape(18.dp),
                            color = Color(0xFF1F1245)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Outlined.ViewInAr,
                                    contentDescription = null,
                                    tint = neonCyan,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Column {
                            Text(
                                text = "SplatStudio",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = textPrimary
                            )
                            Text(
                                text = "V1.1 GAUSSIAN SYSTEM",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.5.sp
                                ),
                                color = brandPurple
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = cleanClayBackground),
                actions = {
                    // Server configuration status pill
                    Surface(
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .clickable { onNavigateToConfig() },
                        shape = RoundedCornerShape(12.dp),
                        color = softPurpleBg,
                        border = BorderStroke(1.dp, borderGray)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(
                                        color = if (activeConfig == null) Color.Gray
                                        else if (isOnline == true) Color(0xFF4CAF50)
                                        else if (isOnline == false) com.example.ui.theme.CosmicError
                                        else Color(0xFFFFB300),
                                        shape = RoundedCornerShape(4.dp)
                                    )
                            )
                            Text(
                                text = activeConfig?.name ?: "Configure Server",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold),
                                color = textPrimary
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        if (splatJobs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .background(softPurpleBg, RoundedCornerShape(60.dp))
                            .border(1.dp, brandPurple, RoundedCornerShape(60.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.PhotoCamera,
                            contentDescription = null,
                            tint = brandPurple,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                    Text(
                        text = "Build Your First 3D Splat",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = textPrimary
                    )
                    Text(
                        text = "Record a short, steady video circling any object, then upload it to your local node to run colmap, fastgs, and construct an interactive model.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = com.example.ui.theme.CosmicTextSecondary,
                        modifier = Modifier.padding(horizontal = 16.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = onNavigateToRecord,
                        modifier = Modifier
                            .height(48.dp)
                            .padding(horizontal = 24.dp)
                            .testTag("record_cta_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = brandPurple,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Icon(Icons.Filled.Videocam, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Record Object Now", fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 48.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(splatJobs, key = { it.id }) { job ->
                    SplatJobCard(
                        job = job,
                        onView = { onViewSplat(job) },
                        onDelete = { viewModel.deleteJob(job.id) },
                        brandPurple = brandPurple,
                        textPrimary = textPrimary,
                        borderGray = borderGray,
                        softPurpleBg = softPurpleBg,
                        activeServerUrl = activeConfig?.url
                    )
                }
            }
        }
    }
}

@Composable
fun SplatJobCard(
    job: SplatJob,
    onView: () -> Unit,
    onDelete: () -> Unit,
    brandPurple: Color,
    textPrimary: Color,
    borderGray: Color,
    softPurpleBg: Color,
    activeServerUrl: String? = null
) {
    val isFinished = job.status == "Done"
    val isFailed = job.status == "Failed"
    val isProcessing = !isFinished && !isFailed

    val cleanClayBackground = com.example.ui.theme.CosmicBackground
    val neonCyan = com.example.ui.theme.NeonCyan
    val cosmicSuccess = com.example.ui.theme.CosmicSuccess
    val cosmicError = com.example.ui.theme.CosmicError

    val dateFormatter = remember { SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.getDefault()) }
    val formattedDate = remember(job.timestamp) { dateFormatter.format(Date(job.timestamp)) }

    var menuExpanded by remember { mutableStateOf(false) }

    var showQualityMetricsDialog by remember { mutableStateOf(false) }
    var qualityMetricsData by remember { mutableStateOf<RenderQuality?>(null) }
    var qualityMetricsError by remember { mutableStateOf<String?>(null) }
    var isFetchingQualityMetrics by remember { mutableStateOf(false) }

    var showAuditLogDialog by remember { mutableStateOf(false) }
    var auditLogData by remember { mutableStateOf<AuditResponse?>(null) }
    var auditLogError by remember { mutableStateOf<String?>(null) }
    var isFetchingAuditLog by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (isProcessing) 1.5.dp else 1.dp,
                color = if (isProcessing) brandPurple else borderGray,
                shape = RoundedCornerShape(16.dp)
            )
            .testTag("splat_job_card_${job.id}"),
        colors = CardDefaults.cardColors(containerColor = softPurpleBg),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(bottom = 6.dp)
                ) {
                    Text(
                        text = job.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = textPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = formattedDate,
                        style = MaterialTheme.typography.bodySmall,
                        color = com.example.ui.theme.CosmicTextSecondary
                    )
                }

                // Delete or View status
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (isFinished) {
                        Button(
                            onClick = onView,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = brandPurple,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(24.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Icon(Icons.Filled.ViewInAr, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("View", style = MaterialTheme.typography.labelSmall, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "Delete project",
                            tint = cosmicError,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Box {
                        IconButton(
                            onClick = { menuExpanded = true },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "More options",
                                tint = textPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false },
                            modifier = Modifier.background(softPurpleBg)
                        ) {
                            DropdownMenuItem(
                                text = { Text("Quality Metrics", color = textPrimary) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Outlined.Analytics,
                                        contentDescription = null,
                                        tint = brandPurple
                                    )
                                },
                                onClick = {
                                    menuExpanded = false
                                    showQualityMetricsDialog = true
                                    if (activeServerUrl != null && !job.serverJobId.isNullOrEmpty()) {
                                        scope.launch {
                                            isFetchingQualityMetrics = true
                                            qualityMetricsError = null
                                            qualityMetricsData = null
                                            try {
                                                val service = NetworkClient.getService(activeServerUrl)
                                                val response = try {
                                                    service.getStatusWithApi(job.serverJobId)
                                                } catch (e: Exception) {
                                                    try {
                                                        service.getStatusNoApi(job.serverJobId)
                                                    } catch (fallbackEx: Exception) {
                                                        throw e
                                                    }
                                                }
                                                val renderQuality = response.metrics?.renderQuality
                                                if (renderQuality != null) {
                                                    qualityMetricsData = renderQuality
                                                } else {
                                                    qualityMetricsError = "Metrics not available yet."
                                                }
                                            } catch (e: Exception) {
                                                qualityMetricsError = "Metrics not available yet."
                                            } finally {
                                                isFetchingQualityMetrics = false
                                            }
                                        }
                                    } else {
                                        qualityMetricsError = "Metrics not available yet."
                                    }
                                }
                            )

                            DropdownMenuItem(
                                text = { Text("Audit Log", color = textPrimary) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Outlined.Description,
                                        contentDescription = null,
                                        tint = neonCyan
                                    )
                                },
                                onClick = {
                                    menuExpanded = false
                                    showAuditLogDialog = true
                                    if (activeServerUrl != null && !job.serverJobId.isNullOrEmpty()) {
                                        scope.launch {
                                            isFetchingAuditLog = true
                                            auditLogError = null
                                            auditLogData = null
                                            try {
                                                val service = NetworkClient.getService(activeServerUrl)
                                                val response = try {
                                                    service.getAuditWithApi(job.serverJobId)
                                                } catch (e: Exception) {
                                                    try {
                                                        service.getAuditNoApi(job.serverJobId)
                                                    } catch (fallbackEx: Exception) {
                                                        throw e
                                                    }
                                                }
                                                auditLogData = response
                                            } catch (e: retrofit2.HttpException) {
                                                if (e.code() == 404) {
                                                    auditLogError = "Audit log not available yet."
                                                } else {
                                                    auditLogError = "Audit log not available yet."
                                                }
                                            } catch (e: Exception) {
                                                auditLogError = "Audit log not available yet."
                                            } finally {
                                                isFetchingAuditLog = false
                                            }
                                        }
                                    } else {
                                        auditLogError = "Audit log not available yet."
                                    }
                                }
                            )
                        }
                    }
                }
            }

            // Pipeline Progress tracking
            if (isProcessing) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(com.example.ui.theme.CosmicBackground, RoundedCornerShape(12.dp))
                        .border(1.dp, borderGray, RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "WORKFLOW PIPELINE",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp),
                            color = neonCyan
                        )
                        Text(
                            text = "${job.progress}% Complete",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp),
                            color = neonCyan
                        )
                    }

                    // Linear Segmented indicator
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(Color(0xFF1E173C))
                    ) {
                        val activeSegmentCount = when (job.status) {
                            "Local" -> 0
                            "Uploading" -> 1
                            "Processing" -> 2
                            "COLMAP" -> 2
                            "FastGS" -> 3
                            "Post-processing" -> 4
                            else -> 4
                        }
                        repeat(4) { index ->
                            val isCompleted = index < activeSegmentCount
                            val isCurrent = index == activeSegmentCount
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .background(
                                        color = if (isCompleted) brandPurple
                                        else if (isCurrent) brandPurple.copy(alpha = 0.5f)
                                        else Color.Transparent
                                    )
                            )
                            if (index < 3) {
                                Spacer(modifier = Modifier.width(2.dp))
                            }
                        }
                    }

                    // Step Names & Icons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        PipelineStep(
                            label = "Upload",
                            icon = Icons.Outlined.CloudUpload,
                            active = job.status == "Uploading",
                            completed = job.status in listOf("Processing", "COLMAP", "FastGS", "Post-processing", "Done")
                        )
                        PipelineArrow()
                        PipelineStep(
                            label = "COLMAP",
                            icon = Icons.Outlined.PhotoCamera,
                            active = job.status == "COLMAP" || job.status == "Processing",
                            completed = job.status in listOf("FastGS", "Post-processing", "Done")
                        )
                        PipelineArrow()
                        PipelineStep(
                            label = "FastGS",
                            icon = Icons.Outlined.Bolt,
                            active = job.status == "FastGS",
                            completed = job.status in listOf("Post-processing", "Done")
                        )
                        PipelineArrow()
                        PipelineStep(
                            label = "Anisotropy",
                            icon = Icons.Outlined.FilterCenterFocus,
                            active = job.status == "Post-processing",
                            completed = job.status == "Done"
                        )
                    }
                }
            } else if (isFailed) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF331417),
                    border = BorderStroke(1.dp, cosmicError)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Outlined.Error, contentDescription = null, tint = cosmicError)
                        Column {
                            Text(
                                text = "Conversion Failed",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = cosmicError
                            )
                            Text(
                                text = job.modelUrl ?: "An unknown server error occurred.",
                                style = MaterialTheme.typography.bodySmall,
                                color = cosmicError.copy(alpha = 0.85f)
                            )
                        }
                    }
                }
            } else {
                // Completed State
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF093325), RoundedCornerShape(12.dp))
                        .border(1.dp, Color(0xFF4CAF50), RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Outlined.CheckCircle, contentDescription = null, tint = Color(0xFF4CAF50))
                        Text(
                            text = "3D Reconstruction Completed",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4CAF50)
                        )
                    }

                    // Touch target for viewing
                    Text(
                        text = "PLAY 3D",
                        modifier = Modifier
                            .clickable { onView() }
                            .padding(4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4CAF50)
                    )
                }
            }
        }
    }

    if (showQualityMetricsDialog) {
        AlertDialog(
            onDismissRequest = { showQualityMetricsDialog = false },
            title = {
                Text(
                    text = "Quality Metrics",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = textPrimary
                )
            },
            text = {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    if (isFetchingQualityMetrics) {
                        CircularProgressIndicator(color = brandPurple)
                    } else if (qualityMetricsError != null) {
                        Text(
                            text = qualityMetricsError ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = com.example.ui.theme.CosmicTextSecondary
                        )
                    } else {
                        val data = qualityMetricsData
                        if (data != null) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                MetricRow("PSNR (Peak Signal-to-Noise Ratio)", String.format(Locale.US, "%.4f", data.psnrMean))
                                MetricRow("SSIM (Structural Similarity Index)", String.format(Locale.US, "%.4f", data.ssimMean))
                                MetricRow("LPIPS (Perceptual Loss)", data.lpipsMean?.let { String.format(Locale.US, "%.4f", it) } ?: "N/A")
                                MetricRow("Held-out views evaluated", "${data.numHeldOutViews}")
                            }
                        } else {
                            Text(
                                text = "Metrics not available yet.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = com.example.ui.theme.CosmicTextSecondary
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showQualityMetricsDialog = false }) {
                    Text("Close", color = brandPurple, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = softPurpleBg,
            textContentColor = textPrimary,
            titleContentColor = textPrimary
        )
    }

    if (showAuditLogDialog) {
        AlertDialog(
            onDismissRequest = { showAuditLogDialog = false },
            title = {
                Text(
                    text = "Audit Log",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = textPrimary
                )
            },
            text = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 400.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (isFetchingAuditLog) {
                        CircularProgressIndicator(color = brandPurple)
                    } else if (auditLogError != null) {
                        Text(
                            text = auditLogError ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = com.example.ui.theme.CosmicTextSecondary
                        )
                    } else {
                        val data = auditLogData
                        val entries = data?.entries
                        if (entries != null && entries.isNotEmpty()) {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(entries) { entry ->
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(com.example.ui.theme.CosmicBackground, RoundedCornerShape(8.dp))
                                            .border(BorderStroke(1.dp, borderGray), RoundedCornerShape(8.dp))
                                            .padding(10.dp),
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = entry.stage.uppercase(),
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                color = neonCyan
                                            )
                                            Text(
                                                text = entry.timestamp,
                                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                                                color = com.example.ui.theme.CosmicTextSecondary
                                            )
                                        }
                                        Text(
                                            text = "Reason: ${entry.reason}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = textPrimary
                                        )
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "Points Removed: ${entry.pointsRemoved}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = textPrimary
                                            )
                                            Text(
                                                text = "Threshold: ${entry.threshold?.let { String.format(Locale.US, "%.2f", it) } ?: "—"}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = com.example.ui.theme.CosmicTextSecondary
                                            )
                                        }
                                    }
                                }
                            }
                        } else {
                            Text(
                                text = "Audit log not available yet.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = com.example.ui.theme.CosmicTextSecondary
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAuditLogDialog = false }) {
                    Text("Close", color = brandPurple, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = softPurpleBg,
            textContentColor = textPrimary,
            titleContentColor = textPrimary
        )
    }
}


@Composable
fun PipelineStep(
    label: String,
    icon: ImageVector,
    active: Boolean,
    completed: Boolean
) {
    val activeColor = com.example.ui.theme.NeonCyan
    val completedColor = com.example.ui.theme.CosmicSuccess
    val color = if (active) activeColor else if (completed) completedColor else com.example.ui.theme.CosmicTextSecondary.copy(alpha = 0.4f)
    val alpha = if (active || completed) 1f else 0.4f
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp),
        modifier = Modifier.alpha(alpha)
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Medium),
            color = color
        )
    }
}

@Composable
fun PipelineArrow() {
    Icon(
        imageVector = Icons.Default.ChevronRight,
        contentDescription = null,
        tint = com.example.ui.theme.CosmicTextSecondary,
        modifier = Modifier
            .size(12.dp)
            .alpha(0.3f)
    )
}

@Composable
fun MetricRow(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(com.example.ui.theme.CosmicBackground, RoundedCornerShape(8.dp))
            .border(BorderStroke(1.dp, com.example.ui.theme.CosmicBorder), RoundedCornerShape(8.dp))
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
            color = com.example.ui.theme.CosmicTextSecondary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace, fontWeight = FontWeight.Bold),
            color = com.example.ui.theme.NeonCyan
        )
    }
}
