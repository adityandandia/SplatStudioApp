package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Cosmic Space Brand Color Tokens (Original SplatStudio Theme)
val CosmicBackground = Color(0xFF0A0714)       // Deep Obsidian Space
val CosmicCard = Color(0xFF130E26)             // Muted Space Purple Card
val CosmicBorder = Color(0xFF251C4A)           // Nebulous Indigo Border
val NeonPurple = Color(0xFFB15EFF)             // Cosmic Purple Accent (Primary Action)
val NeonCyan = Color(0xFF00F5D4)               // Neon Cyan (Secondary Action/Active State)
val CosmicTextPrimary = Color(0xFFF1EEFD)      // Starlight White
val CosmicTextSecondary = Color(0xFFA59EB2)    // Cosmic Dust Grey

val CosmicSuccess = Color(0xFF00C896)          // Stable Green
val CosmicError = Color(0xFFFF4D6D)            // Red Dwarf Flare
val BottomNavBg = Color(0xFF0E0B1B)            // Cosmic Bottom Nav Background

// Compatibility mappings for Cave Labs to prevent any build failures
val CaveBackground = CosmicBackground
val CaveSurface = CosmicCard
val CavePrimaryAccent = NeonPurple
val CaveSecondaryAccent = CosmicBorder
val CaveTextPrimary = CosmicTextPrimary
val CaveTextSecondary = CosmicTextSecondary
val CaveErrorBackground = Color(0xFF2A0F15)
val CaveErrorBorder = CosmicError
val CaveWarningBackground = Color(0xFF261D0F)
val CaveWarningBorder = Color(0xFFFFB300)
