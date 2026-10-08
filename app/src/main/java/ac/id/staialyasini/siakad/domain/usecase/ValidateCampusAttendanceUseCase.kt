package ac.id.staialyasini.siakad.domain.usecase

import ac.id.staialyasini.siakad.core.util.GeoValidationResult
import ac.id.staialyasini.siakad.core.util.LocationValidator
import ac.id.staialyasini.siakad.domain.repository.SiakadRepository
import javax.inject.Inject

/**
 * UseCase untuk memvalidasi lokasi GPS mahasiswa di area Kampus STAI Al-Yasini
 * dan mencatat presensi QR ke server / cache lokal.
 */
class ValidateCampusAttendanceUseCase @Inject constructor(
    private val repository: SiakadRepository
) {
    fun checkGeoRadius(lat: Double, lng: Double, isMock: Boolean): GeoValidationResult {
        return LocationValidator.validateCampusProximity(
            userLat = lat,
            userLng = lng,
            isMockLocation = isMock
        )
    }

    suspend fun submitQrAttendance(
        jadwalId: Long,
        qrToken: String,
        lat: Double,
        lng: Double
    ): Result<String> {
        return repository.catatPresensiQrMahasiswa(jadwalId, qrToken, lat, lng)
    }
}
