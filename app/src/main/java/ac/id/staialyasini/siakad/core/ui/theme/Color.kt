package ac.id.staialyasini.siakad.core.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Design Tokens identik dengan CSS Aplikasi Web SIAKAD STAI Al-Yasini Pasuruan.
 * Nuansa Islami Modern, Bersih, dan Kontras Tinggi.
 */
object SiakadColors {
    // Brand Primary (Emerald Green)
    val BrandPrimary = Color(0xFF0D7C66)
    val BrandPrimaryDark = Color(0xFF075E4D)
    val BrandPrimaryLight = Color(0xFFECFDF5)

    // Brand Accent (Warm Sunny Golden)
    val BrandAccent = Color(0xFFD97706)
    val BrandAccentLight = Color(0xFFFEF3C7)

    // Background & Surface
    val SurfaceBase = Color(0xFFF8FAFC)
    val SurfaceCard = Color(0xFFFFFFFF)
    val SurfaceMuted = Color(0xFFF1F5F9)
    val BorderDefault = Color(0xFFE2E8F0)

    // Typography Colors
    val TextPrimary = Color(0xFF0F172A)
    val TextSecondary = Color(0xFF475569)
    val TextMuted = Color(0xFF64748B)

    // Semantic Status Colors
    val StatusSuccess = Color(0xFF10B981)
    val StatusSuccessBg = Color(0xFFECFDF5)

    val StatusWarning = Color(0xFFF59E0B)
    val StatusWarningBg = Color(0xFFFFFBEB)

    val StatusDanger = Color(0xFFEF4444)
    val StatusDangerBg = Color(0xFFFEF2F2)

    val StatusInfo = Color(0xFF3B82F6)
    val StatusInfoBg = Color(0xFFEFF6FF)
}

// Top-level aliases for direct Material 3 / screen convenience
val BrandPrimary = SiakadColors.BrandPrimary
val BrandPrimaryDark = SiakadColors.BrandPrimaryDark
val BrandPrimaryLight = SiakadColors.BrandPrimaryLight
val BrandAccent = SiakadColors.BrandAccent
val BrandAccentLight = SiakadColors.BrandAccentLight
val SurfaceBase = SiakadColors.SurfaceBase
val SurfaceCard = SiakadColors.SurfaceCard
val SurfaceMuted = SiakadColors.SurfaceMuted
val BorderDefault = SiakadColors.BorderDefault
val TextPrimary = SiakadColors.TextPrimary
val TextSecondary = SiakadColors.TextSecondary
val TextMuted = SiakadColors.TextMuted
val StatusSuccess = SiakadColors.StatusSuccess
val StatusSuccessBg = SiakadColors.StatusSuccessBg
val StatusWarning = SiakadColors.StatusWarning
val StatusWarningBg = SiakadColors.StatusWarningBg
val StatusDanger = SiakadColors.StatusDanger
val StatusDangerBg = SiakadColors.StatusDangerBg
val StatusInfo = SiakadColors.StatusInfo
val StatusInfoBg = SiakadColors.StatusInfoBg
