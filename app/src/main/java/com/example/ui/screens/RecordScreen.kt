package com.example.ui.screens

import android.Manifest
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.*
import androidx.camera.view.PreviewView
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
import androidx.compose.material.icons.automirrored.outlined.Label
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.*
import com.example.ui.SplatViewModel
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RecordScreen(
    viewModel: SplatViewModel,
    onSuccessUpload: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val activeConfig by viewModel.activeServerConfig.collectAsState()

    var objectName by remember { mutableStateOf("") }
    var selectedVideoUri by remember { mutableStateOf<android.net.Uri?>(null) }
    var selectedVideoName by remember { mutableStateOf<String?>(null) }
    var localVideoFile by remember { mutableStateOf<File?>(null) }

    var isRecording by remember { mutableStateOf(false) }
    var recordingDuration by remember { mutableStateOf(0) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var videoCaptureState by remember { mutableStateOf<VideoCapture<Recorder>?>(null) }
    var activeRecording by remember { mutableStateOf<Recording?>(null) }

    DisposableEffect(Unit) {
        onDispose {
            activeRecording?.stop()
            activeRecording = null
        }
    }

    val cleanClayBackground = com.example.ui.theme.CosmicBackground
    val textPrimary = com.example.ui.theme.CosmicTextPrimary
    val brandPurple = com.example.ui.theme.NeonPurple
    val softPurpleBg = com.example.ui.theme.CosmicCard
    val borderGray = com.example.ui.theme.CosmicBorder
    val neonCyan = com.example.ui.theme.NeonCyan

    // Camera permission
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    // Video selector launcher (Gallery fallback)
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: android.net.Uri? ->
        if (uri != null) {
            selectedVideoUri = uri
            selectedVideoName = getFileName(context, uri) ?: "Selected_Video.mp4"
            
            // Save to local cache directory so we can upload it
            try {
                val cacheFile = File(context.cacheDir, "imported_${System.currentTimeMillis()}.mp4")
                context.contentResolver.openInputStream(uri)?.use { input ->
                    FileOutputStream(cacheFile).use { output ->
                        input.copyTo(output)
                    }
                }
                localVideoFile = cacheFile
                errorMessage = null
            } catch (e: Exception) {
                errorMessage = "Failed to copy video: ${e.message}"
            }
        }
    }

    // Timer effect for recording
    LaunchedEffect(isRecording) {
        if (isRecording) {
            while (isRecording) {
                delay(1000)
                recordingDuration++
            }
        } else {
            recordingDuration = 0
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = cleanClayBackground,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Record Scan Area",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = textPrimary
                        )
                        Text(
                            text = "VERSION 1.1 VIDEO ACQUISITION",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.5.sp
                            ),
                            color = brandPurple
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = cleanClayBackground)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Objective detail card
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
                        text = "SCAN METADATA",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        color = brandPurple
                    )

                    OutlinedTextField(
                        value = objectName,
                        onValueChange = {
                            objectName = it
                            errorMessage = null
                        },
                        label = { Text("Object Name") },
                        placeholder = { Text("e.g. Dyson_Fan_01") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("object_name_input"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.AutoMirrored.Outlined.Label, contentDescription = null, tint = brandPurple) },
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
                }
            }

            // Visual recorder canvas area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(cleanClayBackground)
                    .border(1.dp, borderGray, RoundedCornerShape(16.dp))
            ) {
                if (cameraPermissionState.status.isGranted) {
                    // Show camera preview
                    AndroidView(
                        factory = { ctx ->
                            val previewView = PreviewView(ctx)
                            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                            cameraProviderFuture.addListener({
                                val cameraProvider = cameraProviderFuture.get()
                                val preview = Preview.Builder().build().apply {
                                    surfaceProvider = previewView.surfaceProvider
                                }
                                val recorder = Recorder.Builder()
                                    .setQualitySelector(QualitySelector.from(Quality.HIGHEST))
                                    .build()
                                val videoCapture = VideoCapture.withOutput(recorder)
                                videoCaptureState = videoCapture

                                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                                try {
                                    cameraProvider.unbindAll()
                                    cameraProvider.bindToLifecycle(
                                        lifecycleOwner,
                                        cameraSelector,
                                        preview,
                                        videoCapture
                                    )
                                } catch (e: Exception) {
                                    errorMessage = "Camera binding failed: ${e.message}"
                                }
                            }, ContextCompat.getMainExecutor(ctx))
                            previewView
                        },
                        modifier = Modifier.fillMaxSize()
                    )

                    // Recording HUD Layer
                    Box(modifier = Modifier.fillMaxSize()) {
                        // Scan overlay ring
                        Box(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(200.dp)
                                .border(1.5.dp, neonCyan.copy(alpha = 0.4f), RoundedCornerShape(100.dp))
                        )

                        // Top timer pill
                        if (isRecording) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color.Red,
                                modifier = Modifier
                                    .align(Alignment.TopCenter)
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = String.format("REC %02d:%02d", recordingDuration / 60, recordingDuration % 60),
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }

                        // Bottom camera buttons overlay
                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Gallery pick
                            Surface(
                                shape = RoundedCornerShape(24.dp),
                                color = Color.White.copy(alpha = 0.15f),
                                modifier = Modifier
                                    .size(48.dp)
                                    .clickable { galleryLauncher.launch("video/*") }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Outlined.PhotoLibrary, contentDescription = "Pick Video", tint = Color.White)
                                }
                            }

                            // Record circle
                            Surface(
                                shape = RoundedCornerShape(32.dp),
                                color = if (isRecording) Color.Red else Color.White,
                                modifier = Modifier
                                    .size(64.dp)
                                    .clickable {
                                        val capture = videoCaptureState
                                        if (isRecording) {
                                            activeRecording?.stop()
                                            activeRecording = null
                                        } else {
                                            if (capture != null) {
                                                try {
                                                    val cacheFile = File(context.cacheDir, "scan_${System.currentTimeMillis()}.mp4")
                                                    val outputOptions = FileOutputOptions.Builder(cacheFile).build()
                                                    val recording = capture.output
                                                        .prepareRecording(context, outputOptions)
                                                        .start(ContextCompat.getMainExecutor(context)) { recordEvent ->
                                                            when (recordEvent) {
                                                                is VideoRecordEvent.Start -> {
                                                                    isRecording = true
                                                                    recordingDuration = 0
                                                                }
                                                                is VideoRecordEvent.Finalize -> {
                                                                    if (!recordEvent.hasError()) {
                                                                        localVideoFile = cacheFile
                                                                        selectedVideoName = "In_App_Capture_${System.currentTimeMillis().toString().takeLast(4)}.mp4"
                                                                        errorMessage = null
                                                                    } else {
                                                                        errorMessage = "Recording failed: ${recordEvent.error}"
                                                                    }
                                                                    isRecording = false
                                                                }
                                                            }
                                                        }
                                                    activeRecording = recording
                                                } catch (e: Exception) {
                                                    errorMessage = "Failed to start recording: ${e.message}"
                                                }
                                            } else {
                                                errorMessage = "Camera not ready for recording"
                                            }
                                        }
                                    }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    if (isRecording) {
                                        Box(
                                            modifier = Modifier
                                                .size(20.dp)
                                                .background(Color.White, RoundedCornerShape(4.dp))
                                        )
                                    } else {
                                        Box(
                                            modifier = Modifier
                                                .size(16.dp)
                                                .background(Color.Red, RoundedCornerShape(8.dp))
                                        )
                                    }
                                }
                            }

                            // Info details
                            Surface(
                                shape = RoundedCornerShape(24.dp),
                                color = Color.White.copy(alpha = 0.15f),
                                modifier = Modifier
                                    .size(48.dp)
                                    .clickable {
                                        errorMessage = "Circle object continuously keeping a steady pitch."
                                    }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.AutoMirrored.Outlined.HelpOutline, contentDescription = "Splat instruction", tint = Color.White)
                                }
                            }
                        }
                    }

                } else {
                    // Show Permission request state
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.padding(24.dp)
                        ) {
                            Icon(Icons.Outlined.PhotoCamera, contentDescription = null, tint = Color.White.copy(alpha = 0.4f), modifier = Modifier.size(64.dp))
                            Text(
                                text = "Camera Access Required",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Allow camera access to record high-quality 3D video loops directly, or skip and choose a pre-recorded file from your photo library.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.6f),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                              ) {
                                Button(
                                    onClick = { cameraPermissionState.launchPermissionRequest() },
                                    colors = ButtonDefaults.buttonColors(containerColor = brandPurple),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Grant Access", color = Color.White)
                                }
                                Button(
                                    onClick = { galleryLauncher.launch("video/*") },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.15f)),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Import .mp4", color = Color.White)
                                }
                            }
                        }
                    }
                }
            }

            // Displays the chosen/recorded video name
            selectedVideoName?.let { name ->
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = softPurpleBg,
                    border = BorderStroke(1.dp, borderGray)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Outlined.VideoFile, contentDescription = null, tint = neonCyan)
                            Column {
                                textPrimary?.let {
                                    Text(
                                        text = "Video Segment Selected",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = it
                                    )
                                }
                                Text(
                                    text = name,
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = com.example.ui.theme.CosmicTextSecondary,
                                    maxLines = 1
                                )
                            }
                        }
                        IconButton(
                            onClick = {
                                selectedVideoUri = null
                                selectedVideoName = null
                                localVideoFile = null
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Filled.Close, contentDescription = "Clear video", tint = com.example.ui.theme.CosmicTextSecondary, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            // Connection warning or details
            if (activeConfig == null) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF331417),
                    border = BorderStroke(1.dp, com.example.ui.theme.CosmicError)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Outlined.Error, contentDescription = null, tint = com.example.ui.theme.CosmicError)
                        Text(
                            text = "Please register and select an active server node in the 'Config' tab first before converting.",
                            style = MaterialTheme.typography.bodySmall,
                            color = com.example.ui.theme.CosmicError,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            errorMessage?.let { msg ->
                Text(
                    text = msg,
                    style = MaterialTheme.typography.bodySmall,
                    color = com.example.ui.theme.CosmicError,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            // Main Upload CTA Button
            Button(
                onClick = {
                    val file = localVideoFile
                    if (objectName.isBlank()) {
                        errorMessage = "Please enter an object name."
                    } else if (file == null) {
                        errorMessage = "Please record a scan loop or select an existing video file."
                    } else {
                        viewModel.startSplatConversion(
                            title = objectName.trim(),
                            videoFile = file
                        )
                        objectName = ""
                        selectedVideoUri = null
                        selectedVideoName = null
                        localVideoFile = null
                        errorMessage = null
                        onSuccessUpload()
                    }
                },
                enabled = activeConfig != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .border(
                        width = 1.dp,
                        color = if (activeConfig != null) borderGray else Color(0xFF25222E),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .testTag("upload_video_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = brandPurple,
                    disabledContainerColor = Color(0xFF1C1438),
                    contentColor = Color.White,
                    disabledContentColor = Color(0xFF534C73)
                ),
                shape = RoundedCornerShape(24.dp)
            ) {
                Icon(Icons.Filled.CloudUpload, contentDescription = null, tint = if (activeConfig != null) Color.White else Color(0xFF534C73))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Compile 3D Gaussian Splat",
                    fontWeight = FontWeight.Bold,
                    color = if (activeConfig != null) Color.White else Color(0xFF534C73)
                )
            }
        }
    }
}

private fun getFileName(context: Context, uri: android.net.Uri): String? {
    var result: String? = null
    if (uri.scheme == "content") {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        try {
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                if (index != -1) {
                    result = cursor.getString(index)
                }
            }
        } finally {
            cursor?.close()
        }
    }
    if (result == null) {
        result = uri.path
        val cut = result?.lastIndexOf('/') ?: -1
        if (cut != -1) {
            result = result?.substring(cut + 1)
        }
    }
    return result
}
