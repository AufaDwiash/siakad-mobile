package ac.id.staialyasini.siakad.core.util

import android.location.Location
import android.os.Build
import ac.id.staialyasini.siakad.BuildConfig
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

data class GeoValidationResult(
    val isWithinRadius: Boolean,
    val distanceMeters: Int,
    val maxRadiusMeters: Int,
    val isMockLocationDetected: Boolean,
    val message: String
)

/**
 * Validator koordinat GPS Kampus STAI Al-Yasini Pasuruan & Anti-Fake GPS (Mock Location).
 */
object LocationValidator {

    val campusLatitude: Double = BuildConfig.CAMPUS_LATITUDE
    val campusLongitude: Double = BuildConfig.CAMPUS_LONGITUDE
    val maxAllowedRadiusMeters: Double = BuildConfig.CAMPUS_RADIUS_METERS

    fun validateCampusProximity(
        userLat: Double,
        userLng: Double,
        isMockLocation: Boolean = false
    ): GeoValidationResult {
        val distance = calculateDistanceMeters(
            lat1 = userLat,
            lon1 = userLng,
            lat2 = campusLatitude,
            lon2 = campusLongitude
        ).roundToInt()

        val maxRadius = maxAllowedRadiusMeters.roundToInt()

        if (isMockLocation) {
            return GeoValidationResult(
                isWithinRadius = false,
                distanceMeters = distance,
                maxRadiusMeters = maxRadius,
                isMockLocationDetected = true,
                message = "Terdeteksi Fake GPS / Mock Location! Presensi ditolak demi integritas akademik."
            )
        }

        val within = distance <= maxRadius
        val msg = if (within) {
            "Lokasi valid di area Kampus STAI Al-Yasini ($distance m dari titik pusat)."
        } else {
            "Anda berada di luar radius kampus ($distance m > batas $maxRadius m)."
        }

        return GeoValidationResult(
            isWithinRadius = within,
            distanceMeters = distance,
            maxRadiusMeters = maxRadius,
            isMockLocationDetected = false,
            message = msg
        )
    }

    @Suppress("DEPRECATION")
    fun isLocationMocked(location: Location): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            location.isMock
        } else {
            location.isFromMockProvider
        }
    }

    /**
     * Rumus Haversine untuk menghitung jarak meter antar 2 titik koordinat bumi
     */
    private fun calculateDistanceMeters(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
    ): Double {
        val earthRadiusMeters = 6371000.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2.0) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
            sin(dLon / 2).pow(2.0)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return earthRadiusMeters * c
    }
}

object CurrencyFormatter {
    private val rupiahFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID")).apply {
        maximumFractionDigits = 0
    }

    fun formatRupiah(amount: Long): String = rupiahFormat.format(amount)
}
