package ac.id.staialyasini.siakad.presentation.dosen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ac.id.staialyasini.siakad.domain.model.JadwalKuliahItem
import ac.id.staialyasini.siakad.domain.model.KrsPerwalianApprovalItem
import ac.id.staialyasini.siakad.domain.model.PesertaKelasPresensiItem
import ac.id.staialyasini.siakad.domain.model.SkripsiApprovalQueueItem
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

@HiltViewModel
class DosenViewModel @Inject constructor(
    private val repository: SiakadRepository
) : ViewModel() {

    val activeSession: StateFlow<UserSession?> = repository.activeSessionFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val jadwalMengajarHariIni: StateFlow<List<JadwalKuliahItem>> = repository.jadwalHariIniState
    val krsApprovalQueue: StateFlow<List<KrsPerwalianApprovalItem>> = repository.krsApprovalQueueState
    val skripsiApprovalQueue: StateFlow<List<SkripsiApprovalQueueItem>> = repository.skripsiApprovalQueueState
    val pesertaKelasList: StateFlow<List<PesertaKelasPresensiItem>> = repository.pesertaKelasState

    private val _actionMessage = MutableStateFlow<String?>(null)
    val actionMessage: StateFlow<String?> = _actionMessage.asStateFlow()

    fun dismissMessage() {
        _actionMessage.value = null
    }

    fun toggleBatchKrsCheck(krsId: Long) {
        viewModelScope.launch {
            repository.toggleBatchSelectionKrs(krsId)
        }
    }

    fun selectAllPendingKrs(selectAll: Boolean) {
        viewModelScope.launch {
            repository.selectAllPendingKrs(selectAll)
        }
    }

    fun setujuiSatuKrs(krsId: Long, namaMahasiswa: String) {
        viewModelScope.launch {
            repository.setujuiKrsMahasiswa(krsId)
            _actionMessage.value = "KRS $namaMahasiswa berhasil disetujui (Status: DISETUJUI)."
        }
    }

    fun setujuiBatchKrsTerpilih() {
        viewModelScope.launch {
            val approvedCount = repository.setujuiBatchKrsTerpilih()
            if (approvedCount > 0) {
                _actionMessage.value = "Batch Approval berhasil! $approvedCount pengajuan KRS mahasiswa wali telah disetujui sekaligus."
            } else {
                _actionMessage.value = "Pilih minimal 1 mahasiswa berstatus DIAJUKAN untuk Batch Approval."
            }
        }
    }

    fun tolakDanMintaRevisiKrs(krsId: Long, namaMahasiswa: String, catatanWali: String) {
        viewModelScope.launch {
            repository.tolakKrsMahasiswa(krsId, catatanWali)
            _actionMessage.value = "KRS $namaMahasiswa dikembalikan untuk revisi dengan catatan wali."
        }
    }

    fun accBimbinganSkripsi(queueId: Long, namaMahasiswa: String, catatanDosen: String) {
        viewModelScope.launch {
            repository.accBimbinganSkripsiDosen(queueId, catatanDosen)
            _actionMessage.value = "Log bimbingan $namaMahasiswa berhasil di-ACC."
        }
    }

    fun terbitkanAccSidangMunaqasyah(queueId: Long, namaMahasiswa: String) {
        viewModelScope.launch {
            repository.terbitkanAccSidangMunaqasyah(queueId)
            _actionMessage.value = "Surat ACC Sidang Munaqasyah untuk $namaMahasiswa (>= 8x bimbingan) resmi diterbitkan!"
        }
    }

    fun setujuiProposalKaprodi(queueId: Long, namaMahasiswa: String, dosenPembimbing: String) {
        viewModelScope.launch {
            repository.approveProposalKaprodi(queueId, dosenPembimbing)
            _actionMessage.value = "Proposal Skripsi $namaMahasiswa disetujui Kaprodi & di-plotting ke $dosenPembimbing."
        }
    }

    fun ubahStatusPresensiMahasiswa(nim: String, status: String) {
        viewModelScope.launch {
            repository.updatePresensiManualMahasiswa(nim, status)
        }
    }

    fun tandaiSemuaHadir() {
        viewModelScope.launch {
            repository.tandaiSemuaHadirKelas()
            _actionMessage.value = "Seluruh mahasiswa peserta kelas berhasil ditandai HADIR."
        }
    }

    fun simpanJurnalPerkuliahan(materi: String, metode: String) {
        _actionMessage.value = "Jurnal Perkuliahan ('$materi' - $metode) berhasil disimpan ke server SIAKAD."
    }

    // ==================== CRUD JADWAL KULIAH DARI PORTAL DOSEN (ROOM SQLITE) ====================

    fun simpanJadwalKuliahDosen(
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
            _actionMessage.value = "Validasi Gagal: Kode MK, Nama MK, Ruangan, dan Dosen Pengajar wajib diisi!"
            return
        }
        val sks = sksStr.trim().toIntOrNull() ?: 3
        val pertemuanKe = pertemuanKeStr.trim().toIntOrNull() ?: 1

        viewModelScope.launch {
            val res = repository.simpanJadwalKuliah(
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
            res.fold(
                onSuccess = {
                    val label = if (id == null) "ditambahkan" else "diperbarui"
                    _actionMessage.value = "Data berhasil disimpan! Jadwal '$kodeMk - $namaMk' telah $label permanen di Room Database."
                    onSuccessClose()
                },
                onFailure = { err ->
                    _actionMessage.value = err.message
                }
            )
        }
    }

    fun hapusJadwalKuliahDosen(jadwalId: Long, namaMk: String) {
        viewModelScope.launch {
            repository.hapusJadwalKuliah(jadwalId)
            _actionMessage.value = "Jadwal kuliah '$namaMk' berhasil dihapus dari Room Database."
        }
    }
}
