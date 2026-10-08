package ac.id.staialyasini.siakad.data.remote

import ac.id.staialyasini.siakad.data.remote.dto.ApiEnvelope
import ac.id.staialyasini.siakad.data.remote.dto.BatchKrsApproveRequestDto
import ac.id.staialyasini.siakad.data.remote.dto.KrsRevisionRequestDto
import ac.id.staialyasini.siakad.data.remote.dto.LoginRequestDto
import ac.id.staialyasini.siakad.data.remote.dto.LoginResponseDto
import ac.id.staialyasini.siakad.data.remote.dto.PresensiQrRequestDto
import ac.id.staialyasini.siakad.data.remote.dto.SnapTokenRequestDto
import ac.id.staialyasini.siakad.data.remote.dto.SnapTokenResponseDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Kontrak REST API Backend Laravel 13 + Sanctum (`/api/v1/...`)
 * untuk SIAKAD STAI Al-Yasini Pasuruan.
 */
interface SiakadApiService {

    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequestDto
    ): ApiEnvelope<LoginResponseDto>

    @POST("auth/logout")
    suspend fun logout(): ApiEnvelope<Unit>

    @POST("mahasiswa/pembayaran/snap-token")
    suspend fun createSnapPaymentToken(
        @Body request: SnapTokenRequestDto
    ): ApiEnvelope<SnapTokenResponseDto>

    @POST("mahasiswa/presensi/scan-qr")
    suspend fun submitQrAttendance(
        @Body request: PresensiQrRequestDto
    ): ApiEnvelope<Map<String, String>>

    @POST("dosen/perwalian/krs/{krsId}/approve")
    suspend fun approveKrsMahasiswa(
        @Path("krsId") krsId: Long
    ): ApiEnvelope<Unit>

    @POST("dosen/perwalian/krs/batch-approve")
    suspend fun batchApproveKrs(
        @Body request: BatchKrsApproveRequestDto
    ): ApiEnvelope<Unit>

    @POST("dosen/perwalian/krs/{krsId}/reject")
    suspend fun rejectKrsMahasiswa(
        @Path("krsId") krsId: Long,
        @Body request: KrsRevisionRequestDto
    ): ApiEnvelope<Unit>

    @GET("ping")
    suspend fun pingServer(): ApiEnvelope<Map<String, String>>
}
