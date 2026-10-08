package ac.id.staialyasini.siakad.presentation.mahasiswa

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ac.id.staialyasini.siakad.BuildConfig
import ac.id.staialyasini.siakad.domain.model.BimbinganLogItem
import ac.id.staialyasini.siakad.domain.model.JadwalKuliahItem
import ac.id.staialyasini.siakad.domain.model.KelasDitawarkanItem
import ac.id.staialyasini.siakad.domain.model.KrsStatus
import ac.id.staialyasini.siakad.domain.model.SemesterKhsItem
import ac.id.staialyasini.siakad.domain.model.SkripsiApprovalQueueItem
import ac.id.staialyasini.siakad.domain.model.TagihanUktSummary
import ac.id.staialyasini.siakad.domain.model.UserSession
import ac.id.staialyasini.siakad.domain.repository.SiakadRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class QrSimulationMode {
    VALID_CAMPUS,
    OUTSIDE_CAMPUS,
    MOCK_GPS
}

@HiltViewModel
class MahasiswaViewModel @Inject constructor(
    private val repository: SiakadRepository
) : ViewModel() {

    val activeSession: StateFlow<UserSession?> = repository.activeSessionFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val tagihanUkt: StateFlow<TagihanUktSummary> = repository.tagihanUktState
    val jadwalHariIni: StateFlow<List<JadwalKuliahItem>> = repository.jadwalHariIniState
    val krsStatus: StateFlow<KrsStatus> = repository.krsStudentStatusState
    val krsCatatanWali: StateFlow<String?> = repository.krsCatatanWaliState
    val kelasDitawarkan: StateFlow<List<KelasDitawarkanItem>> = repository.kelasDitawarkanState
    val khsSemesterList: StateFlow<List<SemesterKhsItem>> = repository.khsSemesterListState
    val bimbinganLogs: StateFlow<List<BimbinganLogItem>> = repository.bimbinganLogsState
    val ahmadFauziSkripsi: StateFlow<SkripsiApprovalQueueItem> = repository.ahmadFauziSkripsiState

    private val _feedbackBanner = MutableStateFlow<String?>(null)
    val feedbackBanner: StateFlow<String?> = _feedbackBanner.asStateFlow()

    private val _errorBanner = MutableStateFlow<String?>(null)
    val errorBanner: StateFlow<String?> = _errorBanner.asStateFlow()

    fun dismissBanner() {
        _feedbackBanner.value = null
        _errorBanner.value = null
    }

    fun showSuccessBanner(message: String) {
        _errorBanner.value = null
        _feedbackBanner.value = message
    }

    fun bayarTagihanUkt(terminId: Long?, bayarPenuh: Boolean, metode: String) {
        viewModelScope.launch {
            repository.bayarCicilanAtauLunasUkt(terminId, bayarPenuh, metode)
            _errorBanner.value = null
            _feedbackBanner.value = "Pembayaran melalui $metode berhasil diverifikasi otomatis! Status UKT & akses KRS telah diperbarui."
        }
    }

    fun toggleSimulasiStatusUkt(lunas: Boolean) {
        viewModelScope.launch {
            repository.toggleSimulasiStatusUkt(lunas)
            _errorBanner.value = null
            _feedbackBanner.value = if (lunas) {
                "Simulasi: Tagihan UKT diatur LUNAS. Kunci Cekal KRS & Kartu Ujian terbuka."
            } else {
                "Simulasi: Tagihan UKT diatur BELUM LUNAS. Guard Cekal Keuangan aktif."
            }
        }
    }

    fun togglePilihKelasKrs(kelasId: Long) {
        viewModelScope.launch {
            val res = repository.togglePilihKelasKrs(kelasId)
            res.onFailure { err ->
                _errorBanner.value = err.message
            }
        }
    }

    fun ajukanKrsKeDosenWali() {
        viewModelScope.launch {
            val res = repository.ajukanKrsMahasiswa()
            res.fold(
                onSuccess = {
                    _errorBanner.value = null
                    _feedbackBanner.value = "KRS berhasil diajukan ke Dosen Wali (Status: DIAJUKAN). Data telah tersinkron ke antrean Dosen Wali."
                },
                onFailure = { err ->
                    _errorBanner.value = err.message
                }
            )
        }
    }

    fun resetKrsKeDraft() {
        viewModelScope.launch {
            repository.resetKrsToDraft()
            _errorBanner.value = null
            _feedbackBanner.value = "Status KRS dikembalikan ke DRAFT. Anda dapat mengubah pilihan mata kuliah atau mengajukan ulang."
        }
    }

    fun scanQrPresensiKelas(
        jadwalId: Long,
        qrToken: String = "QR-ALYASINI-PAI501-PERT7",
        mode: QrSimulationMode = QrSimulationMode.VALID_CAMPUS
    ) {
        viewModelScope.launch {
            val lat = when (mode) {
                QrSimulationMode.VALID_CAMPUS, QrSimulationMode.MOCK_GPS -> BuildConfig.CAMPUS_LATITUDE + 0.0002
                QrSimulationMode.OUTSIDE_CAMPUS -> BuildConfig.CAMPUS_LATITUDE + 0.0085
            }
            val lng = BuildConfig.CAMPUS_LONGITUDE
            val isMock = mode == QrSimulationMode.MOCK_GPS
            val res = repository.catatPresensiQrMahasiswa(
                jadwalId = jadwalId,
                qrToken = qrToken,
                lat = lat,
                lng = lng,
                isMockLocation = isMock
            )
            res.fold(
                onSuccess = { msg ->
                    _errorBanner.value = null
                    _feedbackBanner.value = msg
                },
                onFailure = { err ->
                    _errorBanner.value = err.message
                }
            )
        }
    }

    // ==================== CRUD LOG BIMBINGAN SKRIPSI (ROOM SQLITE) ====================

    fun tambahDanSimpanLogBimbingan(
        pertemuanKeStr: String,
        tanggal: String,
        bab: String,
        catatan: String,
        onSuccessClearForm: () -> Unit
    ) {
        val pertemuanKe = pertemuanKeStr.trim().toIntOrNull()
        if (pertemuanKe == null || pertemuanKe <= 0) {
            _errorBanner.value = "Validasi Gagal: Pertemuan Ke harus berupa angka positif (contoh: 9)!"
            return
        }
        if (tanggal.isBlank() || bab.isBlank() || catatan.isBlank()) {
            _errorBanner.value = "Validasi Gagal: Tanggal, Bab Bahasan, dan Catatan Mahasiswa wajib diisi!"
            return
        }

        viewModelScope.launch {
            val result = repository.tambahLogBimbinganMahasiswa(
                pertemuanKe = pertemuanKe,
                tanggal = tanggal,
                bab = bab,
                catatan = catatan
            )
            result.fold(
                onSuccess = {
                    _errorBanner.value = null
                    _feedbackBanner.value = "Data berhasil disimpan permanen ke Room Database SQLite (Pertemuan Ke-$pertemuanKe: $bab)."
                    onSuccessClearForm()
                },
                onFailure = { err ->
                    _errorBanner.value = err.message ?: "Gagal menyimpan log bimbingan."
                }
            )
        }
    }

    fun updateLogBimbingan(
        logId: Long,
        pertemuanKeStr: String,
        tanggal: String,
        bab: String,
        catatan: String,
        onSuccessClose: () -> Unit
    ) {
        val pertemuanKe = pertemuanKeStr.trim().toIntOrNull()
        if (pertemuanKe == null || pertemuanKe <= 0) {
            _errorBanner.value = "Validasi Gagal: Pertemuan Ke harus berupa angka positif!"
            return
        }
        if (tanggal.isBlank() || bab.isBlank() || catatan.isBlank()) {
            _errorBanner.value = "Validasi Gagal: Semua kolom wajib diisi sebelum menyimpan perubahan!"
            return
        }

        viewModelScope.launch {
            val result = repository.updateLogBimbinganMahasiswa(
                logId = logId,
                pertemuanKe = pertemuanKe,
                tanggal = tanggal,
                bab = bab,
                catatan = catatan
            )
            result.fold(
                onSuccess = {
                    _errorBanner.value = null
                    _feedbackBanner.value = "Perubahan log bimbingan Pertemuan #$pertemuanKe berhasil disimpan ke Room Database!"
                    onSuccessClose()
                },
                onFailure = { err ->
                    _errorBanner.value = err.message
                }
            )
        }
    }

    fun hapusLogBimbingan(logId: Long, bab: String) {
        viewModelScope.launch {
            repository.hapusLogBimbinganMahasiswa(logId)
            _errorBanner.value = null
            _feedbackBanner.value = "Item log bimbingan '$bab' berhasil dihapus dari Room Database."
        }
    }

    // ==================== CRUD JADWAL KULIAH (ROOM SQLITE) ====================

    fun simpanJadwalKuliah(
        id: Long?,
        kodeMk: String,
        namaMk: String,
        sksStr: String,
        kelas: String,
        hari: String,
        jamMulai: String,
        jamSelesai: String,
        ruangan: String,
        dosenPengajar: String,
        pertemuanKeStr: String,
        onSuccessClose: () -> Unit
    ) {
        if (kodeMk.isBlank() || namaMk.isBlank() || ruangan.isBlank() || dosenPengajar.isBlank()) {
            _errorBanner.value = "Validasi Gagal: Kode MK, Nama MK, Ruangan, dan Dosen Pengajar tidak boleh kosong!"
            return
        }
        val sks = sksStr.trim().toIntOrNull() ?: 3
        val pertemuanKe = pertemuanKeStr.trim().toIntOrNull() ?: 1

        viewModelScope.launch {
            val result = repository.simpanJadwalKuliah(
                id = id,
                kodeMk = kodeMk,
                namaMk = namaMk,
                sks = sks,
                kelas = kelas,
                hari = hari,
                jamMulai = jamMulai,
                jamSelesai = jamSelesai,
                ruangan = ruangan,
                dosenPengajar = dosenPengajar,
                pertemuanKe = pertemuanKe
            )
            result.fold(
                onSuccess = {
                    _errorBanner.value = null
                    val actionLabel = if (id == null) "ditambahkan & disimpan" else "diperbarui"
                    _feedbackBanner.value = "Data berhasil disimpan! Jadwal '$kodeMk - $namaMk' telah $actionLabel permanen di Room Database."
                    onSuccessClose()
                },
                onFailure = { err ->
                    _errorBanner.value = err.message
                }
            )
        }
    }

    fun hapusJadwalKuliah(jadwalId: Long, namaMk: String) {
        viewModelScope.launch {
            repository.hapusJadwalKuliah(jadwalId)
            _errorBanner.value = null
            _feedbackBanner.value = "Jadwal kuliah '$namaMk' berhasil dihapus dari Room Database."
        }
    }
}
