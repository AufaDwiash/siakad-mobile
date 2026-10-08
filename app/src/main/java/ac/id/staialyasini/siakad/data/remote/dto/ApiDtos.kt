package ac.id.staialyasini.siakad.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ApiEnvelope<T>(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: T?
)

data class LoginRequestDto(
    @SerializedName("identifier") val identifier: String, // Email / NIM / NIDN
    @SerializedName("password") val password: String,
    @SerializedName("device_name") val deviceName: String = "Android-SIAKAD-AlYasini"
)

data class LoginResponseDto(
    @SerializedName("token") val token: String,
    @SerializedName("user_id") val userId: Long,
    @SerializedName("name") val name: String,
    @SerializedName("identifier") val identifier: String,
    @SerializedName("email") val email: String,
    @SerializedName("prodi") val prodi: String,
    @SerializedName("user_type") val userType: String,
    @SerializedName("roles") val roles: List<String> = emptyList()
)

data class SnapTokenRequestDto(
    @SerializedName("tagihan_id") val tagihanId: Long,
    @SerializedName("cicilan_id") val cicilanId: Long? = null,
    @SerializedName("channel") val channel: String
)

data class SnapTokenResponseDto(
    @SerializedName("snap_token") val snapToken: String,
    @SerializedName("redirect_url") val redirectUrl: String,
    @SerializedName("virtual_account") val virtualAccount: String?
)

data class PresensiQrRequestDto(
    @SerializedName("jurnal_perkuliahan_id") val jurnalId: Long,
    @SerializedName("qr_token") val qrToken: String,
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double,
    @SerializedName("is_mock_location") val isMockLocation: Boolean
)

data class BatchKrsApproveRequestDto(
    @SerializedName("krs_ids") val krsIds: List<Long>
)

data class KrsRevisionRequestDto(
    @SerializedName("catatan_wali") val catatanWali: String
)
