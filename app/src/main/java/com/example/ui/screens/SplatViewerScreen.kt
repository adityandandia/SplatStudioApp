package com.example.ui.screens

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.webkit.ConsoleMessage
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.RotateRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.SplatJob
import com.example.ui.SplatViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.network.NetworkClient
import kotlinx.coroutines.launch
import java.io.ByteArrayInputStream

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun SplatViewerScreen(
    job: SplatJob,
    viewModel: SplatViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    var isWebLoading by remember { mutableStateOf(true) }
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }
    var downloadedBytes by remember { mutableStateOf<ByteArray?>(null) }
    var downloadError by remember { mutableStateOf<String?>(null) }
    var isDownloading by remember { mutableStateOf(false) }

    val serverUrl = viewModel.activeServerConfig.collectAsState().value?.url

    fun startDownload() {
        val modelUrl = job.modelUrl
        android.util.Log.d("SplatViewerScreen", "startDownload initiated. Job ID (Local): ${job.id}, Job Name: '${job.title}', serverJobId: '${job.serverJobId}', status: '${job.status}', modelUrl: '$modelUrl', activeServerUrl: '$serverUrl'")
        if (job.status == "Done" && !modelUrl.isNullOrEmpty()) {
            isDownloading = true
            downloadError = null
            scope.launch {
                try {
                    val resolvedUrl = if (!modelUrl.startsWith("http://") && !modelUrl.startsWith("https://") && serverUrl != null) {
                        NetworkClient.sanitizeUrl(serverUrl) + modelUrl.removePrefix("/")
                    } else {
                        modelUrl
                    }
                    android.util.Log.d("SplatViewerScreen", "Constructed/resolved download URL for PLY: '$resolvedUrl' (base serverUrl was '$serverUrl')")
                    val service = NetworkClient.getService(serverUrl ?: resolvedUrl)
                    android.util.Log.d("SplatViewerScreen", "Initiating HTTP stream download of PLY file...")
                    
                    val responseBody = withContext(Dispatchers.IO) {
                        service.downloadPointCloud(resolvedUrl)
                    }
                    val contentType = responseBody.contentType()
                    val contentLength = responseBody.contentLength()
                    android.util.Log.d("SplatViewerScreen", "HTTP Response received. Content-Type: '$contentType', advertised Content-Length: $contentLength bytes")
                    
                    val bytes = withContext(Dispatchers.IO) {
                        responseBody.bytes()
                    }
                    android.util.Log.d("SplatViewerScreen", "Successfully downloaded ${bytes.size} bytes from $resolvedUrl")
                    if (bytes.isEmpty()) {
                        throw IllegalStateException("The downloaded file is empty (0 bytes)!")
                    }
                    downloadedBytes = bytes
                    downloadError = null
                } catch (e: Exception) {
                    val errorMsg = "Failed to download 3D data: ${e.localizedMessage ?: e.message}"
                    downloadError = errorMsg
                    android.util.Log.e("SplatViewerScreen", "CRITICAL ERROR: Exception in PLY fetch/download step for job '${job.title}'", e)
                } finally {
                    isDownloading = false
                }
            }
        } else {
            android.util.Log.w("SplatViewerScreen", "Splat download bypassed. Criteria not met: job.status='${job.status}' (expected 'Done'), modelUrl='$modelUrl' (expected non-empty)")
        }
    }

    LaunchedEffect(job, serverUrl) {
        startDownload()
    }

    LaunchedEffect(downloadedBytes, isWebLoading) {
        val bytes = downloadedBytes
        if (!isWebLoading && bytes != null) {
            val timestamp = System.currentTimeMillis()
            android.util.Log.d("SplatViewerScreen", "Injecting loadSplatFromUrl triggered with timestamp: $timestamp")
            webViewInstance?.evaluateJavascript("window.loadSplatFromUrl('https://local.splat/local-data.ply?t=$timestamp')", null)
        }
    }

    val darkBgColor = com.example.ui.theme.CosmicBackground
    val textPrimary = com.example.ui.theme.CosmicTextPrimary
    val brandPurple = com.example.ui.theme.NeonPurple
    val neonCyan = com.example.ui.theme.NeonCyan

    // Generate our embedded interactive HTML string
    val htmlContent = remember(job) {
        SplatHtmlTemplate.getHtml(job.modelUrl, job.title)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = darkBgColor,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "3D Interactive Splat",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = Color.White
                        )
                        Text(
                            text = "OBJECT: ${job.title.uppercase()}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            ),
                            color = brandPurple
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = darkBgColor)
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(darkBgColor)
        ) {
            // WebGL interactive WebView
            AndroidView(
                factory = { ctx ->
                    WebView(ctx).apply {
                        settings.apply {
                            javaScriptEnabled = true
                            domStorageEnabled = true
                            loadWithOverviewMode = true
                            useWideViewPort = true
                            cacheMode = WebSettings.LOAD_NO_CACHE
                            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                        }
                        setDownloadListener { url, userAgent, contentDisposition, mimetype, contentLength ->
                            // No-op
                        }
                        webChromeClient = object : WebChromeClient() {
                            override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                                consoleMessage?.let {
                                    android.util.Log.d("SplatViewerJS", "[${it.messageLevel()}] ${it.message()} -- From line ${it.lineNumber()} of ${it.sourceId()}")
                                }
                                return true
                            }
                        }
                        webViewClient = object : WebViewClient() {
                            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                isWebLoading = true
                            }

                            override fun onPageFinished(view: WebView?, url: String?) {
                                isWebLoading = false
                            }

                            override fun shouldInterceptRequest(
                                view: WebView?,
                                request: WebResourceRequest?
                            ): WebResourceResponse? {
                                val urlStr = request?.url?.toString()
                                if (urlStr != null && urlStr.contains("local-data.ply")) {
                                    val bytes = downloadedBytes
                                    if (bytes != null) {
                                        android.util.Log.d("SplatViewerScreen", "shouldInterceptRequest: Intercepted $urlStr, serving ${bytes.size} bytes")
                                        return WebResourceResponse(
                                            "application/octet-stream",
                                            "UTF-8",
                                            ByteArrayInputStream(bytes)
                                        )
                                    }
                                }
                                return super.shouldInterceptRequest(view, request)
                            }
                        }
                        val loadTimestamp = System.currentTimeMillis()
                        loadDataWithBaseURL("https://local.splat/?t=$loadTimestamp", htmlContent, "text/html", "UTF-8", null)
                        webViewInstance = this
                    }
                },
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("splat_webview")
            )

            // Loading screen
            if (isWebLoading || isDownloading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(darkBgColor),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(180.dp)
                                .border(
                                    width = 2.dp,
                                    color = brandPurple,
                                    shape = RoundedCornerShape(90.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(130.dp)
                                    .border(
                                        width = 1.dp,
                                        color = com.example.ui.theme.CosmicBorder,
                                        shape = RoundedCornerShape(65.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Layers,
                                        contentDescription = null,
                                        tint = com.example.ui.theme.CosmicTextSecondary,
                                        modifier = Modifier.size(36.dp)
                                    )
                                    Text(
                                        text = if (isDownloading) "DOWNLOADING SPLAT" else "RENDERING SPLAT",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 1.5.sp
                                        ),
                                        color = com.example.ui.theme.CosmicTextSecondary
                                    )
                                }
                            }
                        }

                        Text(
                            text = if (isDownloading) "Downloading 3D point cloud data..." else "Initializing WebGL context...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = com.example.ui.theme.CosmicTextSecondary
                        )
                    }
                }
            }

            // Error Screen Overlay
            if (downloadError != null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(darkBgColor)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ErrorOutline,
                            contentDescription = "Error",
                            tint = com.example.ui.theme.CosmicError,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = downloadError ?: "An error occurred",
                            style = MaterialTheme.typography.bodyMedium,
                            color = com.example.ui.theme.CosmicError,
                            fontWeight = FontWeight.Medium
                        )
                        Button(
                            onClick = {
                                startDownload()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = brandPurple,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            Text("Retry", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Overlay HUD Controls
            if (!isWebLoading && !isDownloading && downloadError == null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Object tag and state
                    Surface(
                        modifier = Modifier.align(Alignment.TopStart),
                        shape = RoundedCornerShape(12.dp),
                        color = com.example.ui.theme.CosmicCard,
                        border = BorderStroke(1.dp, com.example.ui.theme.CosmicBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "Object:",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = com.example.ui.theme.CosmicTextSecondary
                            )
                            Text(
                                text = job.title,
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                        }
                    }

                    // Bottom Floating Tool Control Pad
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        InteractiveHudButton(
                            icon = Icons.AutoMirrored.Outlined.RotateRight,
                            onClick = {
                                webViewInstance?.evaluateJavascript("rotateY()", null)
                            }
                        )

                        InteractiveHudButton(
                            icon = Icons.Outlined.ZoomIn,
                            onClick = {
                                webViewInstance?.evaluateJavascript("zoomIn()", null)
                            }
                        )

                        InteractiveHudButton(
                            icon = Icons.Outlined.ZoomOut,
                            onClick = {
                                webViewInstance?.evaluateJavascript("zoomOut()", null)
                            }
                        )

                        InteractiveHudButton(
                            icon = Icons.Outlined.RestartAlt,
                            onClick = {
                                webViewInstance?.evaluateJavascript("resetView()", null)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun InteractiveHudButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .size(48.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        color = com.example.ui.theme.CosmicCard,
        border = BorderStroke(1.dp, com.example.ui.theme.CosmicBorder)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = com.example.ui.theme.NeonCyan,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}
